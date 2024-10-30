/**
 * \page csoundforandroid Csound for Android App
 * <p>
 * This Android app provides pretty much all of Csound, including an HTML/JavaScript interface
 * that runs HTML5 code in the <html> element of a csd file.
 * <p>
 * See CsoundAppActivity.java and csound_oboe.hpp for more information.
 */
package com.csounds.Csound6;

//import static android.support.design.widget.TabLayout.*;
//import static android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.List;

import csnd.CsoundCallbackWrapper;
import csnd.CsoundOboe;
import csnd.csound_oboeJNI;

import fi.iki.elonen.NanoHTTPD;

import android.webkit.MimeTypeMap;

@SuppressWarnings("unused")

class CsoundHTTPD extends NanoHTTPD {
    private final Context context;
    private final Uri baseDirectoryUri;
    public CsoundHTTPD(int port, Context context, Uri baseDirectoryUri) {
        super(port);
        this.context = context;
        this.baseDirectoryUri = baseDirectoryUri;
    }

    public boolean uriEndsWithFilename(Uri uri, String filename) {
        if (uri == null || filename == null) {
            return false;
        }
        // Decode the URI and get the last path segment
        String decodedLastSegment = Uri.decode(uri.getLastPathSegment());
        // Check if the last segment ends with the specified filename
        return decodedLastSegment != null && decodedLastSegment.endsWith(filename);
    }
    /**
     * Recursively performs a depth-first search for a file in a directory
     * tree, using the Storage Access Framework. The local URLs are not
     * encoded, so the URIs here are decoded before comparing them to
     * the target filename.
     * @param directory_file
     * @param target_file
     * @return DocumentFile for located file, or null if not found.
     */
    public DocumentFile recursive_find(DocumentFile directory_file, String target_file) {
        for (DocumentFile file : directory_file.listFiles()) {
            if (file.isDirectory()) {
                // Recursive call to search within subdirectories
                DocumentFile foundFile = recursive_find(file, target_file);
                if (foundFile != null) {
                    return foundFile; // Return the file if found in a subdirectory
                }
            } else if (file.isFile() && file.getName() != null && uriEndsWithFilename(file.getUri(), target_file)) {
                return file; // Found the target file
            }
        }
        return null; // File not found in this directory or its subdirectories
    }

    @Override
    public Response serve(IHTTPSession session) {
        String decodedPath;
        try {
            decodedPath = URLDecoder.decode(session.getUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Encoding Error");
        }
        DocumentFile baseDir = DocumentFile.fromTreeUri(context, baseDirectoryUri);
        if (baseDir == null) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Directory Access Error");
        }
        ///DocumentFile fileToServe = baseDir.findFile(decodedPath.substring(1));
        DocumentFile fileToServe = recursive_find(baseDir, decodedPath.substring(decodedPath.lastIndexOf("/")));
        if (fileToServe != null) { /// && fileToServe.isFile()) {
            try {
                Uri uri_to_serve = fileToServe.getUri();
                InputStream inputStream = context.getContentResolver().openInputStream(uri_to_serve);
                String mimeType = getMimeType(decodedPath);
                return newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, inputStream.available());
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "File Access Error");
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found");
    }

    private Uri getFileUri(Uri baseUri, String relativePath) {
        Uri fileUri = Uri.withAppendedPath(baseUri, relativePath); // Build the file URI from base and relative path
        return fileUri;
    }

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}

public class CsoundAppActivity extends AppCompatActivity implements /* CsoundObjListener,
        CsoundObj.MessagePoster, */ TabLayout.OnTabSelectedListener,
    SharedPreferences.OnSharedPreferenceChangeListener, ValueCallback<String>,
    ActivityCompat.OnRequestPermissionsResultCallback {
    String code = "";
    CsoundOboe csound_oboe = null;
    Uri csound_uri = null;
    Intent csound_uri_intent = null;
    Button pad = null;
    ArrayList<SeekBar> sliders = new ArrayList<SeekBar>();
    ArrayList<Button> buttons = new ArrayList<Button>();
    ArrayList<String> str = new ArrayList<String>();
    private final Boolean firstLvl = true;
    private File external_files_dir_music = null;
    String csdTemplate = null;
    URL baseUrl = null;
    PackageInfo packageInfo = null;
    // Csound environment variables managed on Android.
    static String OPCODE6DIR = null;
    static String SFDIR = null;
    static String SSDIR = null;
    static String SADIR = null;
    static String INCDIR = null;
    private final String screenLayout = "2";
    protected CsoundCallbackWrapper oboe_callback_wrapper = null;
    Intent csound_service_intent = null;
    static final int OPEN_FILE_REQUEST = 8391;
    static final int SAVE_FILE_REQUEST = 8392;
    static final int NEW_FILE_REQUEST = 8393;
    private boolean is_running = false;
    protected Handler handler = new Handler();
    private TabLayout tabs = null;
    private WebView editor = null;
    private LinearLayout editor_tab = null;
    private TextView messageTextView = null;
    private TextView messageTextViewSmall = null;
    private WebView html_tab = null;
    private Uri csound_httpd_directory = null;
    private CsoundHTTPD csound_httpd = null;
    private TableLayout widgets_tab = null;
    private ScrollView messages_tab = null;
    private ScrollView messageTextViewSmallScrollView = null;
    private WebView help_tab = null;
    private WebView portal_tab = null;
    private MenuItem renderItem = null;

    static {
        int result = 0;
        try {
            java.lang.System.loadLibrary("sndfile");
        } catch (Throwable e) {
            Log.e("Csound: ", "sndfile native code library failed to load.\n");
            Log.e("Csound: ", e.toString());
        }
        try {
            java.lang.System.loadLibrary("csoundandroid");
        } catch (Throwable e) {
            Log.e("Csound: ", "Csound: csoundandroid native code library failed to load.\n"
                + e);
            Log.e("Csound: ", e.toString());
        }
        try {
            result = csnd.csound_oboeJNI
                .csoundInitialize(csnd.csound_oboeConstants.CSOUNDINIT_NO_ATEXIT);
        } catch (Throwable e) {
            Log.e("Csound: ", "csoundInitialize failed.\n"
                + e);
            Log.e("Csound: ", e.toString());
            // java.lang.System.exit(1);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Copy the file indicated by fromAssetPath to
    // the public external storage music directory.
    // The asset directory becomes a subdirectory of the music directory.
    private File copyAsset(String fromAssetPath) {
        if (Build.VERSION.SDK_INT < 30) {
            if (checkOnePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        File outputFile = null;
        boolean result;
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            outputFile = new File(externalStoragePublicDirectory, fromAssetPath);
            //result = outputFile.mkdirs();
            String filename = outputFile.getName();
            File directory = outputFile.getParentFile();
            result = directory.mkdirs();
            outputFile.createNewFile();
            in = assetManager.open(fromAssetPath);
            out = new FileOutputStream(outputFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setEnv(String variable, String value) {
        try {
            Class<?> libcore = Class.forName("libcore.io.Libcore");
            if (libcore == null) {
                Log.w("Csound", "Cannot find libcore.os.Libcore;");
                //                return;
            } else {
                final Object os = Class.forName("libcore.io.Libcore").getField("os").get(null);
                Method method = os.getClass().getMethod("setenv", String.class, String.class, boolean.class);
                method.invoke(os, variable, value, true);
            }
        } catch (Exception e) {
            Log.w("Csound", Log.getStackTraceString(e));
            Log.w("Csound", e.getMessage());
        }
    }

    private void copyAssetsRecursively(String path) {
        try {
            String[] files = getAssets().list(path);
            if (files.length == 0) {
                copyAsset(path);
            } else {
                for (int i = 0; i < files.length; i++) {
                    String file = path + "/" + files[i];
                    copyAssetsRecursively(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyRawwaves() {
        try {
            String[] files = getAssets().list("rawwaves");
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                copyAsset("rawwaves/" + file);
            }
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            String rawwavePath = externalStoragePublicDirectory.toString() + "/rawwaves/";
            setEnv("RAWWAVE_PATH", rawwavePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnVisibilityChanged(boolean is_visible) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        renderItem = menu.findItem(R.id.action_render);
        return true;
    }

    public void startRendering() {
        messageTextView.setText("");
        messageTextViewSmall.setText("");
        File file = new File(OPCODE6DIR);
        String getOPCODE6DIR = csnd.csound_oboeJNI.csoundGetEnv(0,
            "OPCODE6DIR");
        postMessage(
            "OPCODE6DIR: " + getOPCODE6DIR
                + "\n");
        File[] files = file.listFiles();
        CsoundAppActivity.this
            .postMessage("Loading Csound plugins:\n");
        for (int i = 0; i < files.length; i++) {
            String pluginPath = files[i].getAbsoluteFile()
                .toString();
            try {
                CsoundAppActivity.this.postMessage(pluginPath
                    + "\n");
                System.load(pluginPath);
            } catch (Throwable e) {
                CsoundAppActivity.this.postMessage(e.toString()
                    + "\n");
            }
        }
        // This must be set before the Csound object is created.
        csnd.csound_oboeJNI.csoundSetGlobalEnv("OPCODE6DIR", OPCODE6DIR);
        csnd.csound_oboeJNI.csoundSetGlobalEnv("SFDIR", SFDIR);
        csnd.csound_oboeJNI.csoundSetGlobalEnv("SSDIR", SSDIR);
        csnd.csound_oboeJNI.csoundSetGlobalEnv("SADIR", SADIR);
        csnd.csound_oboeJNI.csoundSetGlobalEnv("INCDIR", INCDIR);
        int[] exclusive_cores = getExclusiveCores();
        csound_oboe = new CsoundOboe();
        String driver_value = PreferenceManager
            .getDefaultSharedPreferences(this).getString("audioDriver", "0");
        int driver_index = Integer.parseInt(driver_value);
        csound_oboe.setOboeApi(driver_index);
        oboe_callback_wrapper = new CsoundCallbackWrapper(csound_oboe.getCsound()) {
            @Override
            public void MessageCallback(int attr, String msg) {
                Log.d("CsoundOboe:", msg);
                postMessage(msg);
            }
        };
        oboe_callback_wrapper.SetMessageCallback();
        // Csound will not be in scope of any JavaScript on the page
        // until the page is reloaded. Also, we want to show any edits
        // to the page.
        loadWebView();
        postMessageClear("Csound is starting...\n");
        // Make sure this stuff really got packaged.
        String[] samples = null;
        try {
            samples = getAssets().list("samples");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (csound_uri == null) {
            Toast.makeText(getApplicationContext(), "You have not selected a piece to run!", Toast.LENGTH_LONG).show();
        } else {
            String uri_path = csound_uri.getPath();
            Log.d("Csound", "csound_uri.getPath(): " + uri_path);
            // Can use content URI for .csd pieces if MANAGE_EXTERNAL_STORAGE permission has not
            // been granted.
            if (csound_uri.toString().toLowerCase().endsWith(".csd") || csound_uri.toString().startsWith("content://com.android.providers.media.documents/document/audio")) {
                int result = 0;
                String filepath = uriToFilepath(csound_uri);
                result = csound_oboe.CompileCsdText(code);
                result = csound_oboe.Start();
                result = csound_oboe.PerformAndReset();
            }
        }
    }

    // Obtain CPU cores which are reserved for the foreground app. The audio thread can be
    // bound to these cores prevent it being migrated to slower or more contended
    // core(s).
    private int[] getExclusiveCores() {
        int[] exclusiveCores = {};
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.w("Csound", "getExclusiveCores() not supported. Only available on API " +
                Build.VERSION_CODES.N + "+");
        } else {
            try {
                exclusiveCores = android.os.Process.getExclusiveCores();
            } catch (RuntimeException e) {
                Log.w("Csound", "getExclusiveCores() is not supported on this device.");
            }
        }
        return exclusiveCores;
    }

    public void stopRendering() {
        csound_oboe.Stop();
        postMessage("Csound has been stopped.\n");
    }

    public void render() {
        synchronized (this) {
            if (is_running == false) {
                is_running = true;
                renderItem.setTitle("Stop");
                startRendering();
            } else {
                is_running = false;
                stopRendering();
                renderItem.setTitle("Run");
            }
        }
    }

    // This seems to be what I need to understand: https://developer.android.com/guide/topics/providers/document-provider
    // and https://developer.android.com/training/data-storage/shared/documents-files

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            Log.d("Csound", "onOptionsItemSelected: " + item.toString() + "\n");
            File outFile = null;
            switch (item.getItemId()) {
                case R.id.action_new:
                    Intent new_intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    new_intent.setType("*/*");
                    new_intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent new_chooser = Intent.createChooser(new_intent, "Name");
                    startActivityForResult(new_chooser, NEW_FILE_REQUEST);
                    return true;
                case R.id.action_open:
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent chooser = Intent.createChooser(intent, "Open file");
                    chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(chooser, OPEN_FILE_REQUEST);
                    return true;
                case R.id.action_save:
                    // Use this Intent, saved from "Open..." or "New...", to
                    // preserve write and truncate permissions on the file. This
                    // is a hack.
                    onActivityResult(SAVE_FILE_REQUEST, 0, csound_uri_intent);
                    return true;
                case R.id.action_save_as:
                    Intent save_as_intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    save_as_intent.setType("*/*");
                    save_as_intent.addCategory(Intent.CATEGORY_OPENABLE);
                    Intent save_as_chooser = Intent.createChooser(save_as_intent, "Save as");
                    startActivityForResult(save_as_chooser, SAVE_FILE_REQUEST);
                    return true;
                case R.id.action_render:
                    getEditorTextAndRun();
                    return true;
                case R.id.itemGuide:
                    File user_guide = copyAsset("Csound6_User_Guide.pdf");
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, user_guide);
                    Intent guide_intent = new Intent(Intent.ACTION_VIEW, uri);
                    guide_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    guide_intent.setDataAndType(uri, "application/pdf");
                    startActivity(guide_intent);
                    return true;
                case R.id.itemPrivacy:
                    goToUrl("http://csound.github.io/csound_for_android_privacy.html");
                    return true;
                case R.id.itemSettings:
                    // https://www.25yearsofprogramming.com/computer-science/how-to-create-android-settings-screen-using-preferencefragment.html
                    /*
                    // Create new fragment and transaction
                    SettingsFragment newFragment = new SettingsFragment();
                    // consider using Java coding conventions (upper first char class names!!!)
                    androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.fragment_container_view, newFragment);
                    transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                    return true;
                    */
                    Intent settingsIntent = new Intent(this.getBaseContext(), SettingsActivity.class);
                    startActivity(settingsIntent);
                    return true;
                case R.id.itemTrapped:
                    outFile = copyAsset("examples/Boulanger/trapped.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemDroneIV: {
                    outFile = copyAsset("examples/Gogins/Drone-IV.csd");
                    if (outFile != null)
                        LoadFile(outFile);
                }
                return true;
                case R.id.itemPartikkel:
                    outFile = copyAsset("examples/Khosravi/partikkel.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemXanadu:
                    outFile = copyAsset("examples/Kung/xanadu.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemModulatedDelay:
                    outFile = copyAsset("examples/Gogins/ModulateInput.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemBuiltinChannels:
                    outFile = copyAsset("examples/Gogins/BuiltinChannels.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemMessage:
                    outFile = copyAsset("examples/Gogins/message.html");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemKoanI:
                    outFile = copyAsset("examples/McCurdy/i.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemScrims:
                    outFile = copyAsset("examples/Gogins/scrims.html");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemOblivion:
                    outFile = copyAsset("examples/Gogins/oblivion-gm.csd");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                case R.id.itemChordSpace:
                    outFile = copyAsset("examples/Gogins/trichord_space.html");
                    if (outFile != null) {
                        LoadFile(outFile);
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @JavascriptInterface
    public void postMessage(String message_) {
        postMessageClear_(message_, false);
    }

    @JavascriptInterface
    public void postMessageClear(String message_) {
        postMessageClear_(message_, true);
    }

    private final StringBuilder csoundMessageStringBuilder = new StringBuilder();

    private synchronized void postMessageClear_(String message_,
                                                boolean doClear_) {
        final String message = message_;
        final boolean doClear = doClear_;
        CsoundAppActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (doClear == true) {
                    messageTextView.setText("");
                    messageTextViewSmall.setText("");
                }
                if (message == null) {
                    return;
                }
                messageTextView.append(message);
                messages_tab.fullScroll(ScrollView.FOCUS_DOWN);
                messageTextViewSmall.append(message);
                messageTextViewSmallScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                // Send Csound messages to the WebView's console.log function.
                // This should happen when and only when a newline appears.
                for (int i = 0, n = message.length(); i < n; i++) {
                    char c = message.charAt(i);
                    if (c == '\n') {
                        String line = csoundMessageStringBuilder.toString();
                        String code = String.format("console.log(\"%s\\n\");", line);
                        html_tab.evaluateJavascript(code, null);
                        csoundMessageStringBuilder.setLength(0);
                    } else {
                        csoundMessageStringBuilder.append(c);
                    }
                }
                Log.i("Csound", message);
            }
        });
    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void displayLog() {
        try {
            Process process = Runtime.getRuntime().exec(
                "logcat -dt 16 CsoundObj:D AndroidCsound:D *:S");
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            postMessage("Csound system log:\n");
            while ((line = bufferedReader.readLine()) != null) {
                postMessage(line + "\n");
            }
        } catch (IOException e) {
        }
    }

    public void setTitle(String title) {
        String fullTitle = "Csound";
        if (title != null) {
            fullTitle = fullTitle + ": " + title;
        }
        super.setTitle(fullTitle);
    }

    /**
     * Loads text from a file, normally a source code file.
     *
     * @param file Filepath to the file.
     * @return Source code.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public String loadTextFile(File file) {
        String code = null;
        try {
            if (Build.VERSION.SDK_INT < 30) {
                if (checkOnePermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            FileReader in = new FileReader(file);
            StringBuilder contents = new StringBuilder();
            char[] buffer = new char[4096];
            int read = 0;
            do {
                contents.append(buffer, 0, read);
                read = in.read(buffer);
            } while (read >= 0);
            in.close();
            code = contents.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public String getFilePathFromContentUri(Uri contentUri) {
        try {
            File file = new File(contentUri.getPath());
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow("document_id");
                cursor.moveToFirst();
                String result = cursor.getString(columnIndex);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private Uri getHtmlDirectoryUri() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String uriString = prefs.getString("html_directory_uri", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void loadWebView() {
        try {
            getEditorText();
            int start = code.indexOf("<html");
            int end = code.indexOf("</html>") + 7;
            if (!(start == -1 || end == -1)) {
                String html5_page = code.substring(start, end);
                if (html5_page.length() > 1) {
                    html_tab.loadUrl("about:blank");
                    html_tab.removeJavascriptInterface("csound");
                    html_tab.removeJavascriptInterface("CsoundApp");
                    html_tab.setLayerType(View.LAYER_TYPE_NONE, null);
                    WebSettings settings = html_tab.getSettings();
                    // Page itself must specify utf-8 in meta tag?
                    settings.setDefaultTextEncodingName("utf-8");
                    settings.setDomStorageEnabled(true);
                    settings.setDatabaseEnabled(true);
                    settings.setBuiltInZoomControls(true);
                    settings.setDisplayZoomControls(false);
                    settings.setAllowFileAccess(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        settings.setAllowFileAccessFromFileURLs(true);
                        settings.setAllowUniversalAccessFromFileURLs(true);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        settings.setJavaScriptEnabled(true);
                    }
                    String filepath = uriToFilepath(csound_uri);
                    Log.d("Csound", "uriToFilepath: filepath: " + filepath);
                    String filename = filepath.substring(filepath.lastIndexOf('/') + 1);
                    if (csound_httpd != null) {
                        csound_httpd.stop();
                    }
                    Uri htmlDirectoryUri = this.getHtmlDirectoryUri();
                    csound_httpd = new CsoundHTTPD(8080, getApplicationContext(), htmlDirectoryUri);
                    csound_httpd.start();
                    settings.setJavaScriptEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                            html_tab.setWebContentsDebuggingEnabled(true);
                        }
                    }
                    html_tab.addJavascriptInterface(csound_oboe, "csound");
                    html_tab.addJavascriptInterface(CsoundAppActivity.this, "CsoundApp");
                    String local_url = "http://localhost:8080/" + filename;
                    html_tab.loadUrl(local_url);
                    html_tab.invalidate();
                }
            } else {
                html_tab.onPause();
                html_tab.pauseTimers();
            }
            View mainLayout = findViewById(R.id.mainLayout);
            mainLayout.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadFile(File file) {
        Log.d("Csound", file.getAbsolutePath());
        String pathname = file.getPath();
        String extension = "";
        int i = pathname.lastIndexOf('.');
        if (i >= 0) {
            extension = pathname.substring(i + 1);
        }
        String code = loadTextFile(file);
        setEditorText(code, extension);
        setTitle(file.getName());
        csound_uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, file);
        loadWebView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Csound", "onStop...");
    }

    @Override
    protected void onDestroy() {
        Log.d("Csound", "onDestroy...");
        super.onDestroy();
        final boolean b = stopService(csound_service_intent);
        try {
            synchronized (this) {
                if (csound_oboe != null) {
                    csound_oboe.Stop();
                }
            }
        } catch (Exception e) {
            Log.e("Csound", "Could not stop csound_oboe: \n" + e.toString());
        }
        if (csound_httpd != null) {
            csound_httpd.stop();
        }
        finishAndRemoveTask();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Log.d("Csound", "onCreate...");
        external_files_dir_music = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        // Start a service that will keep this activity's process running in the background.
        csound_service_intent = new Intent(this, CsoundService.class);
        ComponentName componentName = startService(csound_service_intent);
        // We ask for the data directory in case Android changes on
        // us without warning.
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),
                0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // Bug since not turned back on: audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        OPCODE6DIR = getBaseContext().getApplicationInfo().nativeLibraryDir;
        final SharedPreferences sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        OPCODE6DIR = sharedPreferences.getString("OPCODE6DIR", OPCODE6DIR);
        SSDIR = packageInfo.applicationInfo.dataDir + "/samples";
        SSDIR = sharedPreferences.getString("SSDIR", SSDIR);
        SFDIR = sharedPreferences.getString("SFDIR", SFDIR);
        SADIR = sharedPreferences.getString("SADIR", SADIR);
        INCDIR = sharedPreferences.getString("INCDIR", INCDIR);
        String driver = sharedPreferences.getString("audioDriver", "");
        // Do this before copying anything.
        checkDangerousPermissions();
/**
 try {
 csound_httpd = new CsoundHTTPD(http_port, html_directory);
 csound_httpd.start();
 } catch (Throwable e) {
 Log.e("Csound: ", "Could not create CsoundHTTPD.\n"
 + e);
 Log.e("Csound: ", e.toString());
 }
 */
        // Pre-load plugin opcodes, not only to ensure that Csound
        // can load them, but for easier debugging if they fail to load.
        File file = new File(OPCODE6DIR);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String pluginPath = files[i].getAbsoluteFile().toString();
            try {
                System.load(pluginPath);
            } catch (Throwable e) {
                postMessage(e.toString() + "\n");
            }
        }
        copyRawwaves();
        copyAssetsRecursively("samples");
        copyAssetsRecursively("examples");
        copyAssetsRecursively("cloud-5");
        csdTemplate = "<CsoundSynthesizer>\n" + "<CsLicense>\n"
            + "</CsLicense>\n" + "<CsOptions>\n" + "</CsOptions>\n"
            + "<CsInstruments>\n" + "</CsInstruments>\n" + "<CsScore>\n"
            + "</CsScore>\n" + "</CsoundSynthesizer>\n";
        tabs = findViewById(R.id.tab_layout);
        tabs.addOnTabSelectedListener(this);
        widgets_tab = findViewById(R.id.widgets_tab);
        widgets_tab.setVisibility(View.GONE);
        html_tab = findViewById(R.id.html_tab);
        editor_tab = findViewById(R.id.editor_tab);
        editor = findViewById(R.id.editor);
        registerForContextMenu(editor);
        editor_tab.setVisibility(View.VISIBLE);
        html_tab = findViewById(R.id.html_tab);
        html_tab.setVisibility(View.GONE);
        messages_tab = findViewById((R.id.messages_tab));
        messages_tab.setVisibility(View.GONE);
        help_tab = findViewById(R.id.help_tab);
        help_tab.setVisibility(View.GONE);
        portal_tab = findViewById(R.id.portal_tab);
        portal_tab.setVisibility(View.GONE);
        messageTextView = findViewById(R.id.messageTextView);
        messageTextViewSmall = findViewById(R.id.messageTextViewSmall);
        messageTextViewSmallScrollView = findViewById(R.id.messageTextSmallScroll);
        sliders.add((SeekBar) findViewById(R.id.seekBar1));
        sliders.add((SeekBar) findViewById(R.id.seekBar2));
        sliders.add((SeekBar) findViewById(R.id.seekBar3));
        sliders.add((SeekBar) findViewById(R.id.seekBar4));
        sliders.add((SeekBar) findViewById(R.id.seekBar5));
        sliders.add((SeekBar) findViewById(R.id.seekBar6));
        sliders.add((SeekBar) findViewById(R.id.seekBar7));
        sliders.add((SeekBar) findViewById(R.id.seekBar8));
        sliders.add((SeekBar) findViewById(R.id.seekBar9));
        sliders.add((SeekBar) findViewById(R.id.seekBar10));
        sliders.add((SeekBar) findViewById(R.id.seekBar11));
        sliders.add((SeekBar) findViewById(R.id.seekBar12));
        buttons.add((Button) findViewById(R.id.button1));
        buttons.add((Button) findViewById(R.id.button2));
        buttons.add((Button) findViewById(R.id.button3));
        buttons.add((Button) findViewById(R.id.button4));
        buttons.add((Button) findViewById(R.id.button5));
        buttons.add((Button) findViewById(R.id.button6));
        buttons.add((Button) findViewById(R.id.button7));
        buttons.add((Button) findViewById(R.id.button8));
        buttons.add((Button) findViewById(R.id.button9));
        buttons.add((Button) findViewById(R.id.button10));
        buttons.add((Button) findViewById(R.id.button11));
        buttons.add((Button) findViewById(R.id.button12));
        pad = findViewById(R.id.pad);
        html_tab.getSettings().setJavaScriptEnabled(true);
        html_tab.getSettings().setBuiltInZoomControls(true);
        html_tab.getSettings().setDisplayZoomControls(false);
        html_tab.setWebViewClient(new CsoundWebViewClient());
        help_tab.getSettings().setJavaScriptEnabled(true);
        help_tab.getSettings().setBuiltInZoomControls(true);
        help_tab.getSettings().setDisplayZoomControls(false);
        help_tab.setWebViewClient(new CsoundWebViewClient());
        help_tab.loadUrl("https://csound.com/docs/manual/index.html");
        portal_tab.getSettings().setJavaScriptEnabled(true);
        portal_tab.getSettings().setBuiltInZoomControls(true);
        portal_tab.getSettings().setDisplayZoomControls(false);
        portal_tab.setWebViewClient(new CsoundWebViewClient());
        portal_tab.loadUrl("https://csound.com/");
        editor.getSettings().setJavaScriptEnabled(true);
        editor.getSettings().setBuiltInZoomControls(true);
        editor.getSettings().setDisplayZoomControls(false);
        editor.setWebViewClient(new CsoundWebViewClient());
        editor.addJavascriptInterface(this, "csoundApp");
        editor.loadUrl("file:///android_asset/embedded_editor.html");
        editor.setBackgroundColor(0);
        // Add slider handlers.
        for (int i = 0; i < 12; i++) {
            SeekBar seekBar = sliders.get(i);
            final String channelName = "slider" + (i + 1);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    if (fromUser) {
                        double value = progress / (double) seekBar.getMax();
                        synchronized (this) {
                            if (csound_oboe != null) {
                                csound_oboe.SetChannel(channelName, value);
                            }
                        }
                    }
                }
            });
        }
        // Add button handlers.
        for (int i = 0; i < 12; i++) {
            Button button = buttons.get(i);
            final String channelName = "butt" + (i + 1);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        synchronized (this) {
                            if (csound_oboe != null) {
                                csound_oboe.SetChannel(channelName, 1.);
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        synchronized (this) {
                            if (csound_oboe != null) {
                                csound_oboe.SetChannel(channelName, 0.);
                            }
                        }
                    }
                    return true;
                }
            });
        }
        // Add trackpad handler.
        pad.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction() & MotionEvent.ACTION_MASK;
                double xpos = 0;
                double ypos = 0;
                boolean selected = false;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        pad.setPressed(true);
                        selected = true;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP:
                        selected = false;
                        pad.setPressed(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                if (pad.isPressed() == true) {
                    xpos = event.getX() / v.getWidth();
                    ypos = 1. - (event.getY() / v.getHeight());
                    synchronized (this) {
                        if (csound_oboe != null) {
                            csound_oboe.SetChannel("trackpad.x", xpos);
                            csound_oboe.SetChannel("trackpad.y", ypos);
                        }
                    }
                }
                return true;
            }
        });
        // Add motion handler.
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            SensorEventListener motionListener = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Not used.
                }

                public void onSensorChanged(SensorEvent event) {
                    double accelerometerX = event.values[0];
                    double accelerometerY = event.values[1];
                    double accelerometerZ = event.values[2];
                    synchronized (this) {
                        if (csound_oboe != null) {
                            csound_oboe.SetChannel("accelerometerX", accelerometerX);
                            csound_oboe.SetChannel("accelerometerY", accelerometerY);
                            csound_oboe.SetChannel("accelerometerZ", accelerometerZ);
                        }
                    }
                }
            };
            sensorManager.registerListener(motionListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        // Log some useful information for the user.
        postMessage("This is the Csound for Android app version code: " + BuildConfig.VERSION_CODE + "\n");
        postMessage("The Csound native library version is: " + csound_oboeJNI.csoundGetVersion() + "\n");
        postMessage(
            "The external storage public directory for music for Csound is: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + "\n");
        postMessage(
            "The external files directory for music for Csound is: " + external_files_dir_music.toString() + "\n");
        List<Sensor> all_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        postMessage("The available sensors on this device are:\n");
        SensorEventListener genericSensorEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Not used.
            }

            public void onSensorChanged(SensorEvent event) {
                synchronized (this) {
                    if (csound_oboe != null) {
                        String[] parts = event.sensor.getStringType().split("\\.");
                        String base_name = parts[parts.length - 1];
                        for (int i = 0; i < event.values.length; ++i) {
                            double value = event.values[i];
                            String name = base_name + String.valueOf(i + 1);
                            csound_oboe.setControlChannel(name, value);
                            //Log.d("Csound sensor", name + ": " + value + "\n");
                        }
                    }
                }
            }
        };
        for (int i = 0; i < all_sensors.size(); ++i) {
            Sensor sensor = all_sensors.get(i);
            String[] parts = sensor.getStringType().split("\\.");
            String name = parts[parts.length - 1];
            postMessage("  type: " + name + " maximum range:" + sensor.getMaximumRange() + "\n");
            sensorManager.registerListener(genericSensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
        ;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private String uriToFilename(android.net.Uri uri) {
        String result = uri.getPath();
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }
        return result;
    }

    private String uriToTitle(android.net.Uri contentUri) {
        try {
            File file = new File(contentUri.getPath());
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            Cursor cursor = contentResolver.query(contentUri, null, null, null, null);
            String column_names[] = cursor.getColumnNames();
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow("_display_name");
                cursor.moveToFirst();
                String result = cursor.getString(columnIndex);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String uriToFilepath(android.net.Uri uri) {
        String absolute_filepath = null;
        // We must distinguish between content (external) Uris and
        // internal (root) Uris.
        try {
            String uri_string = uri.toString();
            String uri_path = uri.getPath();
            final String[] split = uri_string.split(":");
            final String[] pathpart = uri_path.split(":");
            String filepath = "";
            if (pathpart.length > 1) {
                filepath = Environment.getExternalStorageDirectory() + "/" + pathpart[1];
            } else {
                filepath = uri_path;
            }
            File file = new File(filepath);
            absolute_filepath = file.getAbsolutePath();
            return absolute_filepath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return absolute_filepath;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        // NOTE: Fullscreen layout makes automatic resizing for soft keyboard quit working.
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    static final int MY_REQUEST_DANGEROUS_PERMISSIONS = 2149;
    static final int REQUEST_CODE_PICK_HTML_DIR = 2948;

    protected int checkOnePermission(String permission) {
        int result = ContextCompat.checkSelfPermission(CsoundAppActivity.this, permission);
        Log.d("Csound", "ContextCompat.checkSelfPermission for " + permission + " returned: " + result + "\n");
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CsoundAppActivity.this, new String[]{permission}, MY_REQUEST_DANGEROUS_PERMISSIONS);
        }
        return result;
    }

    final int REQUEST_ALL_FILES_ACCESS_PERMISSION = 4930;

    protected boolean checkDangerousPermissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Snackbar.make(findViewById(android.R.id.content), "Permission to access all files is needed to run examples and read/write files!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(intent, REQUEST_ALL_FILES_ACCESS_PERMISSION);
                            } catch (Exception ex) {
                                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                                startActivityForResult(intent, REQUEST_ALL_FILES_ACCESS_PERMISSION);
                            }
                        }
                    })
                    .show();
            }
        }
        boolean request_permissions = false;
        ArrayList<String> permissions_to_request = new ArrayList();
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                request_permissions = true;
                permissions_to_request.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }
        }
        if (Build.VERSION.SDK_INT <= 29) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                request_permissions = true;
                permissions_to_request.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
            Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            request_permissions = true;
            permissions_to_request.add(Manifest.permission.BODY_SENSORS);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            request_permissions = true;
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            request_permissions = true;
            permissions_to_request.add(Manifest.permission.RECORD_AUDIO);
        }
        if (request_permissions == true) {
            ActivityCompat.requestPermissions(CsoundAppActivity.this,
                permissions_to_request.toArray(new String[permissions_to_request.size()]),
                MY_REQUEST_DANGEROUS_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }

    public void saveTextToUri(String text, Uri uri) {
        postMessage("saveTextToUri...\n");
        try {
            String uri_path = uri.getPath();
            Log.d("Csound", "uri.getPath(): " + uri_path);
            ContentResolver contentResolver = getContentResolver();
            OutputStream outputStream = contentResolver.openOutputStream(uri, "rwt");
            outputStream.write(code.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (java.io.IOException e) {
            Log.d("Csound", e.getMessage());
            postMessage(e.getMessage());
        } catch (java.lang.NullPointerException e) {
            Log.d("Csound", e.getMessage());
            postMessage(e.getMessage());
        }
    }

    public String loadTextFromUri(Uri uri) throws java.io.IOException {
        postMessage("loadTextFromUri...\n");
        String uri_path;
        Uri document_uri;
        String document_uri_path = null;
        Uri media_uri;
        String media_uri_path = null;
        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                document_uri = MediaStore.getDocumentUri(getApplicationContext(), uri);
                document_uri_path = document_uri.getPath();
            } catch (Exception e) {
                Log.e("Csound", e.toString());
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                media_uri = MediaStore.getMediaUri(getApplicationContext(), uri);
                media_uri_path = media_uri.getPath();
            } catch (Exception e) {
                Log.e("Csound", e.toString());
            }
        }
        uri_path = uri.getPath();
        Log.d("Csound", "uri.getPath(): " + uri_path);
        Log.d("Csound", "document_uri.getPath(): " + document_uri_path);
        Log.d("Csound", "media_uri.getPath(): " + media_uri_path);
        InputStream inputStream = getContentResolver().openInputStream(uri);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        for (String line; (line = bufferedReader.readLine()) != null; ) {
            stringBuilder.append(line).append('\n');
        }
        String text = stringBuilder.toString();
        return text;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            Log.d("Csound", "onActivityResult: " + requestCode + "," + resultCode + ", " + intent.toString() + "\n");
            if (requestCode == NEW_FILE_REQUEST && intent != null) {
                if (checkOnePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                csound_uri_intent = intent;
                csound_uri = intent.getData();
                setEditorText(csdTemplate, "csd");
                saveTextToUri(csdTemplate, csound_uri);
                String title = uriToTitle(csound_uri);
                setTitle(title);
            }
            if (requestCode == SAVE_FILE_REQUEST && intent != null) {
                if (Build.VERSION.SDK_INT <= 29) {
                    if (checkOnePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                csound_uri_intent = intent;
                csound_uri = intent.getData();
                String csound_uri_path = csound_uri.getPath();
                Log.e("Csound", "csound_uri.getPath(): " + csound_uri_path);
                getEditorText();
                saveTextToUri(code, csound_uri);
                String title = uriToTitle(csound_uri);
                setTitle(title);
            }
            if (requestCode == OPEN_FILE_REQUEST && intent != null) {
                if (Build.VERSION.SDK_INT < 30) {
                    if (checkOnePermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                csound_uri_intent = intent;
                csound_uri = intent.getData();
                String text = loadTextFromUri(csound_uri);
                String pathname = csound_uri.getPath();
                String extension = "";
                int i = pathname.lastIndexOf('.');
                if (i >= 0) {
                    extension = pathname.substring(i + 1);
                }
                setEditorText(text, extension);
                loadWebView();
                String title = uriToTitle(csound_uri);
                setTitle(title);
            }
            if (requestCode == REQUEST_CODE_PICK_HTML_DIR && resultCode == Activity.RESULT_OK) {
                Uri directoryUri = intent.getData();
                if (directoryUri != null) {
                    // Persist permissions for future access
                    getContentResolver().takePersistableUriPermission(
                        directoryUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // Save the URI to shared preferences or another storage location
                }
            }
        } catch (Exception e) {
            Log.e("Csound", e.toString());
        }
    }

    public void setEditorText(String text, String extension) {
        Log.i("Csound", "setEditorText...");
        code = text;
        String mode = "ace/mode/csound_document";
        if (extension.compareToIgnoreCase("html") == 0) {
            mode = "ace/mode/html";
        }
        String snippet = "setEmbeddedEditorText(\"" + mode + "\");";
        editor.evaluateJavascript(snippet, this);
    }

    public void getEditorText() {
        Log.i("Csound", "getEditorText...");
        editor.evaluateJavascript("getEmbeddedEditorText();", this);
    }

    public void getEditorTextAndRun() {
        Log.i("Csound", "getEditorTextAndRun...");
        editor.evaluateJavascript("getEmbeddedEditorTextAndRun();", this);
    }

    @JavascriptInterface
    public void setCsoundText(String text) {
        postMessage("setCsoundText...\n");
        Log.i("Csound", "setCsoundText...");
        code = text;
    }

    @JavascriptInterface
    public void setCsoundTextAndRun(String text) {
        postMessage("setCsoundTextAndRun...\n");
        Log.i("Csound", "setCsoundTextAndRun...");
        code = text;
    }

    @JavascriptInterface
    public String getCsoundText() {
        postMessage("getCsoundText...\n");
        return code;
    }

    @JavascriptInterface
    public void setControlChannel(String channelName, double value) {
        csound_oboe.SetChannel(channelName, value);
    }

    @JavascriptInterface
    public double getControlChannel(String channelName) {
        double value = 0;
        value = csound_oboe.getChannel(channelName);
        return value;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        String text = tab.getText().toString();
        if (text.contentEquals("Editor")) {
            editor_tab.setVisibility(View.VISIBLE);
            editor_tab.invalidate();
        } else if (text.contentEquals("Widgets")) {
            widgets_tab.setVisibility(View.VISIBLE);
            widgets_tab.invalidate();
        } else if (text.contentEquals("Messages")) {
            messages_tab.setVisibility(View.VISIBLE);
            messages_tab.invalidate();
        } else if (text.contentEquals("HTML")) {
            html_tab.setVisibility(View.VISIBLE);
            html_tab.invalidate();
        } else if (text.contentEquals("Help")) {
            help_tab.setVisibility(View.VISIBLE);
            help_tab.invalidate();
        } else if (text.contentEquals("About")) {
            portal_tab.setVisibility(View.VISIBLE);
            portal_tab.invalidate();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        String text = tab.getText().toString();
        if (text.contentEquals("Editor")) {
            editor_tab.setVisibility(View.GONE);
        } else if (text.contentEquals("Widgets")) {
            widgets_tab.setVisibility(View.GONE);
        } else if (text.contentEquals("Messages")) {
            messages_tab.setVisibility(View.GONE);
        } else if (text.contentEquals("HTML")) {
            html_tab.setVisibility(View.GONE);
        } else if (text.contentEquals("Help")) {
            help_tab.setVisibility(View.GONE);
        } else if (text.contentEquals("About")) {
            portal_tab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onReceiveValue(String value) {
        if (value.contains("getEmbeddedEditorTextAndRun")) {
            render();
        } else if (value.contains("getEmbeddedEditorTextAndSave")) {
            saveTextToUri(code, csound_uri);
        }
    }

    /**
     * Enable self-contained browsers to run JavaScript and load external URLs.
     */
    private class CsoundWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,
                                                WebResourceRequest request) {
            return false;
        }

        @SuppressLint("NewApi")
        @Override
        public void onReceivedError(WebView view,
                                    WebResourceRequest request,
                                    WebResourceError error) {
            String originalUrl = view.getOriginalUrl();
            (new Throwable()).printStackTrace();
            Toast.makeText(CsoundAppActivity.this,
                    "WebView error for " + request.getUrl() + " from " + originalUrl + "! " + error.getDescription(), Toast.LENGTH_LONG)
                .show();
        }
    }
}
