package com.tavultesoft.kmapro;

import android.annotation.SuppressLint;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.tavultesoft.kmea.KMManager;
import com.tavultesoft.kmea.KeyboardEventHandler;
import com.tavultesoft.kmea.packages.PackageProcessor;
import com.tavultesoft.kmea.packages.LexicalModelPackageProcessor;
import com.tavultesoft.kmea.util.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackageActivity extends AppCompatActivity {

  private Toolbar toolbar;
  private WebView webView;
  private AlertDialog alertDialog;
  private File kmpFile;
  private File tempPackagePath;
  private List<Map<String, String>> installedPackageKeyboards;
  private List<Map<String, String>> installedLexicalModels;
  private static ArrayList<KeyboardEventHandler.OnKeyboardDownloadEventListener> kbDownloadEventListeners = null;

  @SuppressLint({"SetJavaScriptEnabled", "InflateParams"})
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_package_installer);

    final Context context = this;
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      kmpFile = new File(bundle.getString("kmpFile"));
    }

    final String pkgId = PackageProcessor.getPackageID(kmpFile);
    final String pkgTarget = PackageProcessor.getPackageTarget(kmpFile, false);

    try {
      if (pkgTarget.equals(PackageProcessor.PP_TARGET_KEYBOARDS)) {
        tempPackagePath = PackageProcessor.unzipKMP(kmpFile);
      } else if (pkgTarget.equals(PackageProcessor.PP_TARGET_LEXICAL_MODELS)) {
        tempPackagePath = LexicalModelPackageProcessor.unzipKMP(kmpFile);
      } else {
        showErrorToast(context, getString(R.string.no_targets_to_install));
        return;
      }
    } catch (Exception e) {
      showErrorToast(context, getString(R.string.failed_to_extract));
      return;
    }

    JSONObject pkgInfo = PackageProcessor.loadPackageInfo(tempPackagePath);
    if (pkgInfo == null) {
      showErrorToast(context, getString(R.string.invalid_metadata));
      return;
    }

    String pkgVersion = PackageProcessor.getPackageVersion(pkgInfo);
    String pkgName = PackageProcessor.getPackageName(pkgInfo);

    toolbar = (Toolbar) findViewById(R.id.titlebar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(null);
    getSupportActionBar().setDisplayUseLogoEnabled(false);
    getSupportActionBar().setDisplayShowHomeEnabled(false);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setBackgroundDrawable(MainActivity.getActionBarDrawable(this));

    TextView packageActivityTitle = new TextView(this);
    packageActivityTitle.setWidth((int) getResources().getDimension(R.dimen.package_label_width));
    packageActivityTitle.setTextSize(getResources().getDimension(R.dimen.titlebar_label_textsize));
    packageActivityTitle.setGravity(Gravity.CENTER);

    String pkgTargetTitle = pkgTarget.equals(PackageProcessor.PP_TARGET_KEYBOARDS) ? 
      getString(R.string.install_keyboard_package) : getString(R.string.install_lexical_model_package);
    String titleStr = String.format("%s %s", pkgTargetTitle, pkgVersion);
    packageActivityTitle.setText(titleStr);
    getSupportActionBar().setCustomView(packageActivityTitle);

    final Button installButton = (Button) findViewById(R.id.installButton);
    final Button cancelButton = (Button) findViewById(R.id.cancelButton);

    webView = (WebView) findViewById(R.id.packageWebView);
    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setUseWideViewPort(true);
    webView.getSettings().setLoadWithOverviewMode(true);
    webView.getSettings().setBuiltInZoomControls(true);
    webView.getSettings().setSupportZoom(true);
    webView.getSettings().setTextZoom(200);
    webView.setVerticalScrollBarEnabled(true);
    webView.setHorizontalScrollBarEnabled(true);
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    webView.setWebChromeClient(new WebChromeClient() {
    });
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null && !url.toLowerCase().equals("about:blank"))
          view.loadUrl(url);

        return true;
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
      }

      @Override
      public void onPageFinished(WebView view, String url) {
      }
    });

    // Determine if KMP contains welcome.htm (case-insensitive) to display
    FileFilter welcomeFilter = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        if (pathname.isFile() && pathname.getName().equalsIgnoreCase("welcome.htm")) {
          return true;
        }
        return false;
      }
    };

    File[] files = tempPackagePath.listFiles(welcomeFilter);
    if (files.length > 0 && files[0].exists() && files[0].length() > 0) {
      webView.loadUrl("file:///" + files[0].getAbsolutePath());
    } else {
      // No welcome.htm so display minimal package information
      String targetString = "";
      if (pkgTarget.equals(PackageProcessor.PP_TARGET_KEYBOARDS)) {
        targetString = pkgName != null && pkgName.toLowerCase().endsWith("keyboard")
          ? "" : String.format(" %s", getString(R.string.title_keyboard));
      } else if (pkgTarget.equals(PackageProcessor.PP_TARGET_LEXICAL_MODELS)) {
        targetString = pkgName != null && pkgName.toLowerCase().endsWith("model")
          ? "" :String.format(" %s", "model");
      }
      String htmlString = String.format(
        "<body style=\"max-width:600px;\"><H1>The %s%s Package</H1></body>",
        pkgName, targetString);
      webView.loadData(htmlString, "text/html; charset=utf-8", "UTF-8");
    }

    installButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          if (pkgTarget.equals(PackageProcessor.PP_TARGET_KEYBOARDS)) {
            // processKMP will remove currently installed package and install
            installedPackageKeyboards = PackageProcessor.processKMP(kmpFile, true);
            // Do the notifications!
            boolean success = installedPackageKeyboards.size() != 0;
            if (success) {
              notifyPackageInstallListeners(KeyboardEventHandler.EventType.PACKAGE_INSTALLED, installedPackageKeyboards, 1);
              if (installedPackageKeyboards != null) {
                notifyPackageInstallListeners(KeyboardEventHandler.EventType.PACKAGE_INSTALLED, installedPackageKeyboards, 1);
              }
              cleanup();
            } else {
              showErrorDialog(context, pkgId, getString(R.string.no_new_touch_keyboards_to_install));
            }
          } else if (pkgTarget.equals(PackageProcessor.PP_TARGET_LEXICAL_MODELS)) {
            installedLexicalModels = LexicalModelPackageProcessor.processKMP(kmpFile, true);
            // Do the notifications
            boolean success = installedLexicalModels.size() != 0;
            if (success) {

            }
            cleanup();
          }
        } catch (Exception e) {
          Log.e("PackageActivity", "Error " + e);
          showErrorDialog(context, pkgId, getString(R.string.no_valid_touch_keyboards_to_install)); // DDW or lexical model
        }
      }
    });

    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        cleanup();
      }
    });
  }

  @Override
  public void onDestroy(){
    super.onDestroy();
    if ( alertDialog !=null && alertDialog.isShowing() ){
      alertDialog.dismiss();
    }
  }

  private void cleanup() {
    try {
      if (kmpFile != null && kmpFile.exists()) {
        kmpFile.delete();
      }
      if (tempPackagePath != null && tempPackagePath.exists()) {
        FileUtils.deleteDirectory(tempPackagePath);
      }
    } catch (Exception e) {
      Log.e("PackageActivity", "cleanup() failed with error " + e);
    } finally {
      finish();
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  @Override
  public void onBackPressed() {
    finish();
    overridePendingTransition(0, android.R.anim.fade_out);
  }

  private void showErrorToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    // Setting result to 1 so calling activity will finish too
    setResult(1);
    cleanup();
  }

  private void showErrorDialog(Context context, String pkgId, String message) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

    alertDialogBuilder.setTitle(String.format("%s %s %s",
      getString(R.string.title_package), pkgId, getString(R.string.title_failed_to_install)));
    alertDialogBuilder
      .setMessage(message)
      .setCancelable(false)
      .setPositiveButton(getString(R.string.label_close),new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,int id) {
          if (dialog != null) {
            dialog.dismiss();
          }
          cleanup();
        }
      });

    alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  void notifyPackageInstallListeners(KeyboardEventHandler.EventType eventType, List<Map<String, String>> keyboards, int result) {
    if (kbDownloadEventListeners != null) {
      KeyboardEventHandler.notifyListeners(kbDownloadEventListeners, eventType, keyboards, result);
    }
  }

  public static void addKeyboardDownloadEventListener(KeyboardEventHandler.OnKeyboardDownloadEventListener listener) {
    if (kbDownloadEventListeners == null) {
      kbDownloadEventListeners = new ArrayList<KeyboardEventHandler.OnKeyboardDownloadEventListener>();
    }

    if (listener != null && !kbDownloadEventListeners.contains(listener)) {
      kbDownloadEventListeners.add(listener);
    }
  }

  public static void removeKeyboardDownloadEventListener(KeyboardEventHandler.OnKeyboardDownloadEventListener listener) {
    if (kbDownloadEventListeners != null) {
      kbDownloadEventListeners.remove(listener);
    }
  }

}
