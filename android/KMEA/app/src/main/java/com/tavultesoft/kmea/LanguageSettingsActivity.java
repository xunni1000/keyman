/**
 * Copyright (C) 2019 SIL International. All rights reserved.
 */

package com.tavultesoft.kmea;

import com.tavultesoft.kmea.JSONParser;
import com.tavultesoft.kmea.packages.JSONUtils;
import com.tavultesoft.kmea.util.FileUtils;
import com.tavultesoft.kmea.util.MapCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Keyman Settings --> Languages Settings --> Language Settings
 * Displays a list of installed keyboards and some lexical model switches.
 */
public final class LanguageSettingsActivity extends AppCompatActivity {
  private Context context;
  private static Toolbar toolbar = null;
  private static ListView listView = null;
  private ImageButton addButton = null;
  private static ArrayList<HashMap<String, String>> installedKeyboardList = null;
  //private static ArrayList<HashMap<String, String>> languagesArrayList = null;
  private static ArrayList<HashMap<String, String>> keyboardsArrayList = null;
  private boolean didExecuteParser = false;
  private static final String jsonCacheFilename = "jsonCache.dat";
  private final String titleKey = "title";
  private final String subtitleKey = "subtitle";
  private final String iconKey = "icon";
  private String associatedLexicalModel = "";
  private String languageID = "";
  private String languageName = "";
  private boolean dismissOnSelect = false;
  private static final String TAG = "LanguageSettings";

  /*
  private static JSONArray languages = null;

  protected static JSONArray languages() {
    return languages;
  }
  */
  private static JSONObject language = null;

  protected static JSONObject language() {
    return language;
  }

  private static JSONObject options = null;

  protected static JSONObject options() {
    return options;
  }

  private static HashMap<String, HashMap<String, String>> keyboardsInfo = null;
  private static HashMap<String, String> keyboardModifiedDates = null;

  private int selectedIndex = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    context = this;
    setContentView(R.layout.language_settings_list_layout);

    toolbar = (Toolbar) findViewById(R.id.list_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    TextView textView = (TextView) findViewById(R.id.bar_title);
    textView.setText(getString(R.string.title_language_settings));

    listView = (ListView) findViewById(R.id.listView);
    listView.setFastScrollEnabled(true);

    keyboardsArrayList = new ArrayList<HashMap<String, String>>();

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      installedKeyboardList = (ArrayList<HashMap<String, String>>) bundle.getSerializable("associatedKeyboards");
      associatedLexicalModel = bundle.getString(KMManager.KMKey_LexicalModelName, "");
      languageID = bundle.getString(KMManager.KMKey_LanguageID);
      languageName = bundle.getString(KMManager.KMKey_LanguageName);
    } else {
      installedKeyboardList = new ArrayList<HashMap<String, String>>();
    }

    RelativeLayout layout = (RelativeLayout)findViewById(R.id.corrections_toggle);
    textView = (TextView) layout.findViewById(R.id.text1);
    textView.setText(getString(R.string.enable_corrections));

    layout = (RelativeLayout)findViewById(R.id.predictions_toggle);
    textView = (TextView) layout.findViewById(R.id.text1);
    textView.setText(getString(R.string.enable_predictions));

    layout = (RelativeLayout)findViewById(R.id.model_picker);
    textView = (TextView) layout.findViewById(R.id.text1);
    textView.setText(getString(R.string.model));
    if (!associatedLexicalModel.isEmpty()) {
      textView = (TextView) layout.findViewById(R.id.text2);
      textView.setText(associatedLexicalModel);
      textView.setEnabled(true);
    }
    ImageView imageView = (ImageView) layout.findViewById(R.id.image1);
    imageView.setImageResource(R.drawable.ic_arrow_forward);
    layout.setEnabled(true);
    layout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Start Model Picker Activity.
        Bundle bundle = new Bundle();
        bundle.putString(KMManager.KMKey_LanguageID, languageID);
        bundle.putString(KMManager.KMKey_LanguageName, languageName);
        Intent i = new Intent(context, ModelsPickerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtras(bundle);
        startActivity(i);
      }
    });

    /**
     * This is a placeholder for "Manage dictionary" settings
     *
     * layout = (RelativeLayout)findViewById(R.id.manage_dictionary);
     * textView = (TextView) layout.findViewById(R.id.text1);
     * textView.setText(getString(R.string.manage_dictionary));
     * imageView = (ImageView) layout.findViewById(R.id.image1);
     * imageView.setImageResource(R.drawable.ic_arrow_forward);
     */

    String[] from = new String[]{KMManager.KMKey_KeyboardName, KMManager.KMKey_Icon};
    int[] to = new int[]{R.id.text1, R.id.image1};
    ListAdapter listAdapter = new KMListAdapter(context, keyboardsArrayList, R.layout.list_row_layout1, from, to);

    listView.setAdapter(listAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listView.setItemChecked(position, true);
        listView.setSelection(position);
        HashMap<String, String> kbInfo = keyboardsArrayList.get(position);
        String packageID = kbInfo.get(KMManager.KMKey_PackageID);
        String keyboardID = kbInfo.get(KMManager.KMKey_KeyboardID);
        if (packageID == null || packageID.isEmpty()) {
          packageID = KMManager.KMDefault_UndefinedPackageID;
        }
        Intent intent = new Intent(context, KeyboardSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(KMManager.KMKey_PackageID, packageID);
        intent.putExtra(KMManager.KMKey_KeyboardID, keyboardID);
        intent.putExtra(KMManager.KMKey_LanguageID, kbInfo.get(KMManager.KMKey_LanguageID));
        intent.putExtra(KMManager.KMKey_LanguageName, kbInfo.get(KMManager.KMKey_LanguageName));
        intent.putExtra(KMManager.KMKey_KeyboardName, kbInfo.get(KMManager.KMKey_KeyboardName));
        intent.putExtra(KMManager.KMKey_KeyboardVersion, KMManager.getLatestKeyboardFileVersion(context, packageID, keyboardID));
        boolean isCustom = kbInfo.get(KMManager.KMKey_CustomKeyboard).equals("Y") ? true : false;
        intent.putExtra(KMManager.KMKey_CustomKeyboard, isCustom);
        String customHelpLink = kbInfo.get(KMManager.KMKey_CustomHelpLink);
        if (customHelpLink != null)
          intent.putExtra(KMManager.KMKey_CustomHelpLink, customHelpLink);
        startActivity(intent);
      }
    });

    addButton = (ImageButton) findViewById(R.id.add_button);
    addButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Check that available keyboard information can be obtained via:
        // 1. connection to cloud catalog
        // 2. cached file
        // 3. local kmp.json files in packages/
        if (KMManager.hasConnection(context) || getCacheFile(context).exists() ||
          KeyboardPickerActivity.hasKeyboardFromPackage()){
          dismissOnSelect = false;

          // TODO: implement getgLangugeIndex
          int index = 2;
          //int index = getLanguageIndex(context, languageID);
          if (index == -1) {
            Log.d(TAG, "Invalid index in language list");
          }
          Bundle bundle = new Bundle();
          bundle.putInt("selectedIndex", index);
          Intent i = new Intent(context, KeyboardListActivity.class);
          i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
          i.putExtras(bundle);
          context.startActivity(i);
        } else {
          AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
          dialogBuilder.setTitle(getString(R.string.title_add_keyboard));
          dialogBuilder.setMessage(String.format("\n%s\n", getString(R.string.cannot_connect)));
          dialogBuilder.setPositiveButton(getString(R.string.label_ok), null);
          AlertDialog dialog = dialogBuilder.create();
          dialog.show();
        }
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    //KMKeyboardDownloaderActivity.addKeyboardDownloadEventListener(this);

    if (!didExecuteParser) {
      didExecuteParser = true;
      new JSONParse().execute();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override
  public void onBackPressed() {
    finish();
  }

  protected static HashMap<String, String> getKeyboardInfo(int languageIndex, int keyboardIndex) {
    if (language == null)
      return null;

    HashMap<String, String> kbInfo = null;
    try {
      String langID = language.getString(KMManager.KMKey_ID);
      String langName = language.getString(KMManager.KMKey_Name);

      JSONArray keyboards = language.getJSONArray(KMKeyboardDownloaderActivity.KMKey_LanguageKeyboards);
      String pkgID = keyboards.getJSONObject(keyboardIndex).optString(KMManager.KMKey_PackageID, KMManager.KMDefault_UndefinedPackageID);
      String kbID = keyboards.getJSONObject(keyboardIndex).getString(KMManager.KMKey_ID);
      String kbName = keyboards.getJSONObject(keyboardIndex).getString(KMManager.KMKey_Name);
      String kbVersion = keyboards.getJSONObject(keyboardIndex).optString(KMManager.KMKey_KeyboardVersion, "1.0");
      String isCustom = keyboards.getJSONObject(keyboardIndex).optString(KMManager.KMKey_CustomKeyboard, "N");
      String kbFont = keyboards.getJSONObject(keyboardIndex).optString(KMManager.KMKey_Font, "");

      kbInfo = new HashMap<String, String>();
      kbInfo.put(KMManager.KMKey_PackageID, pkgID);
      kbInfo.put(KMManager.KMKey_KeyboardID, kbID);
      kbInfo.put(KMManager.KMKey_LanguageID, langID);
      kbInfo.put(KMManager.KMKey_KeyboardName, kbName);
      kbInfo.put(KMManager.KMKey_LanguageName, langName);
      kbInfo.put(KMManager.KMKey_KeyboardVersion, kbVersion);
      kbInfo.put(KMManager.KMKey_CustomKeyboard, isCustom);
      kbInfo.put(KMManager.KMKey_Font, kbFont);
    } catch (JSONException e) {
      kbInfo = null;
      Log.e(TAG, "getKeyboardInfo - JSON Error: " + e);
    }

    return kbInfo;
  }

  protected static HashMap<String, HashMap<String, String>> getKeyboardsInfo(Context context) {
    if (keyboardsInfo != null) {
      return keyboardsInfo;
    } else {
      try {
        JSONObject jsonObj = getCachedJSONObject(context);
        if (jsonObj == null) {
          return null;
        }

        //languages = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Languages).getJSONArray(KMKeyboardDownloaderActivity.KMKey_Languages);
        options = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Options);
        language = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Language);
        keyboardsInfo = new HashMap<String, HashMap<String, String>>();
        keyboardModifiedDates = new HashMap<String, String>();

        String kbKey = "";
        String pkgID = "";
        String kbID = "";
        String langID = language.getString(KMManager.KMKey_ID);
        String kbName = "";
        String langName = language.getString(KMManager.KMKey_Name);
        String kbVersion = "1.0";
        String isCustom = "N";
        String kbFont = "";
        JSONArray langKeyboards = language.getJSONArray(KMKeyboardDownloaderActivity.KMKey_LanguageKeyboards);
        JSONObject keyboard = null;

        int kbLength = langKeyboards.length();
        if (kbLength == 1) {
          keyboard = langKeyboards.getJSONObject(0);
          pkgID = keyboard.optString(KMManager.KMKey_PackageID, KMManager.KMDefault_UndefinedPackageID);
          kbID = keyboard.getString(KMManager.KMKey_ID);
          kbName = keyboard.getString(KMManager.KMKey_Name);
          kbVersion = keyboard.optString(KMManager.KMKey_KeyboardVersion, "1.0");
          kbFont = keyboard.optString(KMManager.KMKey_Font, "");

          kbKey = String.format("%s_%s", langID, kbID);
          HashMap<String, String> hashMap = new HashMap<String, String>();
          hashMap.put(KMManager.KMKey_PackageID, pkgID);
          hashMap.put(KMManager.KMKey_KeyboardName, kbName);
          hashMap.put(KMManager.KMKey_LanguageName, langName);
          hashMap.put(KMManager.KMKey_KeyboardVersion, kbVersion);
          hashMap.put(KMManager.KMKey_CustomKeyboard, isCustom);
          hashMap.put(KMManager.KMKey_Font, kbFont);
          keyboardsInfo.put(kbKey, hashMap);

          if (keyboardModifiedDates.get(kbID) == null) {
            keyboardModifiedDates.put(kbID, keyboard.getString(KMManager.KMKey_KeyboardModified));
          }
        } else {
          for (int j = 0; j < kbLength; j++) {
            keyboard = langKeyboards.getJSONObject(j);
            pkgID = keyboard.optString(KMManager.KMKey_PackageID, KMManager.KMDefault_UndefinedPackageID);
            kbID = keyboard.getString(KMManager.KMKey_ID);
            kbName = keyboard.getString(KMManager.KMKey_Name);
            kbVersion = keyboard.optString(KMManager.KMKey_KeyboardVersion, "1.0");
            kbFont = keyboard.optString(KMManager.KMKey_Font, "");

            kbKey = String.format("%s_%s", langID, kbID);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put(KMManager.KMKey_PackageID, pkgID);
            hashMap.put(KMManager.KMKey_KeyboardName, kbName);
            hashMap.put(KMManager.KMKey_LanguageName, langName);
            hashMap.put(KMManager.KMKey_KeyboardVersion, kbVersion);
            hashMap.put(KMManager.KMKey_CustomKeyboard, isCustom);
            hashMap.put(KMManager.KMKey_Font, kbFont);
            keyboardsInfo.put(kbKey, hashMap);

            if (keyboardModifiedDates.get(kbID) == null) {
              keyboardModifiedDates.put(kbID, keyboard.getString(KMManager.KMKey_KeyboardModified));
            }
          }
        }

        return keyboardsInfo;
      } catch (Exception e) {
        Log.e(TAG, "getKeyboardsInfo() error: " + e);
        return null;
      }
    }
  }

  protected static File getCacheFile(Context context) {
    return new File(context.getCacheDir(), jsonCacheFilename);
  }

  protected static JSONObject getCachedJSONObject(Context context) {
    JSONObject jsonObj;
    try {
      // Read from cache file
      ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(getCacheFile(context)));
      jsonObj = new JSONObject(objInput.readObject().toString());
      objInput.close();
    } catch (Exception e) {
      Log.e(TAG, "Failed to read from cache file. Error: " + e);
      jsonObj = null;
    }

    return jsonObj;
  }

  private static void saveToCache(Context context, JSONObject jsonObj) {
    ObjectOutput objOutput;
    try {
      // Save to cache file
      objOutput = new ObjectOutputStream(new FileOutputStream(getCacheFile(context)));
      objOutput.writeObject(jsonObj.toString());
      objOutput.close();
    } catch (Exception e) {
      Log.e(TAG, "Failed to save to cache file. Error: " + e);
    }
  }

  protected class JSONParse extends AsyncTask<Void, Integer, JSONObject> {

    private final boolean hasConnection = KMManager.hasConnection(context);
    private ProgressDialog progressDialog;
    private boolean loadFromCache;
    private final String iconKey = "icon";

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loadFromCache = false;
      File cacheFile = getCacheFile(context);
      if (cacheFile.exists()) {
        Calendar lastModified = Calendar.getInstance();
        lastModified.setTime(new Date(cacheFile.lastModified()));
        lastModified.add(Calendar.HOUR_OF_DAY, 1);
        Calendar now = Calendar.getInstance();
        if (!hasConnection || lastModified.compareTo(now) > 0)
          loadFromCache = true;
      }

      if (hasConnection && !loadFromCache) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          progressDialog = new ProgressDialog(context, R.style.AppTheme_Dialog_Progress);
        } else {
          progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(getString(R.string.getting_keyboard_catalog));
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_cancel),
          new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
              cancel(true);
              progressDialog.dismiss();
              progressDialog = null;
              LanguageSettingsActivity.this.finish();
              return;
            }
          });
        progressDialog.setCancelable(true);
        if (!((AppCompatActivity) context).isFinishing()) {
          progressDialog.show();
        } else {
          cancel(true);
          progressDialog = null;
        }
      }
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
      if (isCancelled()) {
        return null;
      }

      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObj = null;
      if (loadFromCache) {
        jsonObj = getCachedJSONObject(context);
      } else if (hasConnection) {
        try {
          String deviceType = getString(R.string.device_type);
          if (deviceType.equals("AndroidTablet")) {
            deviceType = "androidtablet";
          } else {
            deviceType = "androidphone";
          }
          // Query for keyboards associated with Language ID
          String remoteUrl = String.format("%s/%s?version=%s&device=%s&languageidtype=bcp47",
            KMKeyboardDownloaderActivity.kKeymanApiBaseURL, languageID, BuildConfig.VERSION_NAME, deviceType);
          jsonObj = jsonParser.getJSONObjectFromUrl(remoteUrl);
        } catch (Exception e) {
          jsonObj = null;
        }
      } else {
        jsonObj = null;
      }

      return jsonObj;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObj) {
      if (progressDialog != null && progressDialog.isShowing()) {
        try {
          progressDialog.dismiss();
          progressDialog = null;
        } catch (Exception e) {
          progressDialog = null;
        }
      }

      // Consolidate kmp.json info from packages/
      JSONArray kmpLanguagesArray = JSONUtils.getLanguages();

      if (jsonObj == null && kmpLanguagesArray.length() == 0) {
        Toast.makeText(context, "Failed to access Keyman server!", Toast.LENGTH_SHORT).show();
        finish();
        return;
      } else if (!loadFromCache) {
        saveToCache(context, jsonObj);
      }

      try {
        JSONObject kmpLanguage = null;
        for (int i=0; i<kmpLanguagesArray.length(); i++) {
          JSONObject languageObj = kmpLanguagesArray.getJSONObject(i);
          if (languageObj.getString("id").equalsIgnoreCase(languageID)) {
            kmpLanguage = languageObj;
          }
        }

        keyboardsInfo = new HashMap<String, HashMap<String, String>>();
        keyboardModifiedDates = new HashMap<String, String>();

        if (!hasConnection) {
          // When offline, only use keyboards available from kmp.json
          // Use default options
          options = JSONUtils.defaultOptions(context.getString(R.string.device_type));
          language = kmpLanguage;  //languages = kmpLanguagesArray;
        } else {
          // Otherwise, merge kmpLanguagesArray with cloud languagesArray
          options = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Options);
          //languages = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Languages).getJSONArray(KMKeyboardDownloaderActivity.KMKey_Languages);
          // Merge language info
          language = jsonObj.getJSONObject(KMKeyboardDownloaderActivity.KMKey_Language);

          JSONArray keyboards = language.getJSONArray("keyboards");
          if (kmpLanguage != null && kmpLanguage.has("keyboards")) {
            JSONArray kmpKeyboards = kmpLanguage.getJSONArray("keyboards");
            for (int j = 0; j < kmpKeyboards.length(); j++) {
              JSONObject kmpKeyboard = kmpKeyboards.getJSONObject(j);
              String kmpKeyboardID = kmpKeyboard.getString("id");
              int keyboardIndex = JSONUtils.findID(keyboards, kmpKeyboardID);
              if (keyboardIndex == -1) {
                // Add new keyboard object
                keyboards.put(kmpKeyboard);
              } else {
                // Merge keyboard info
                JSONObject keyboard = keyboards.getJSONObject(keyboardIndex);

                String keyboardVersion = keyboard.getString(KMManager.KMKey_KeyboardVersion);
                String kmpKeyboardVersion = kmpKeyboard.getString(KMManager.KMKey_KeyboardVersion);
                int versionComparison = FileUtils.compareVersions(kmpKeyboardVersion, keyboardVersion);
                if ((versionComparison == FileUtils.VERSION_GREATER) || (versionComparison == FileUtils.VERSION_EQUAL)) {
                  // Keyboard from package >= Keyboard from cloud so replace keyboard entry with local kmp info
                  keyboards.put(keyboardIndex, kmpKeyboard);
                }
              }
            }
          }
        }

        String kbKey = "";
        String kbID = "";
        String langID = language.getString(KMManager.KMKey_ID);
        String kbName = "";
        String langName = language.getString(KMManager.KMKey_Name);
        String kbVersion = "1.0";
        String isCustom = "N";
        String kbFont = "";
        String icon = "0";
        String isEnabled = "true";
        JSONArray langKeyboards = language.getJSONArray(KMKeyboardDownloaderActivity.KMKey_LanguageKeyboards);
        JSONObject keyboard = null;

        icon = String.valueOf(R.drawable.ic_arrow_forward);
        for (int j = 0; j < langKeyboards.length(); j++) {
          keyboard = langKeyboards.getJSONObject(j);
          kbID = keyboard.getString(KMManager.KMKey_ID);
          kbName = keyboard.getString(KMManager.KMKey_Name);
          kbVersion = keyboard.optString(KMManager.KMKey_KeyboardVersion, "1.0");
          kbFont = keyboard.optString(KMManager.KMKey_Font, "");

          kbKey = String.format("%s_%s", langID, kbID);
          HashMap<String, String> hashMap = new HashMap<String, String>();
          hashMap.put(KMManager.KMKey_KeyboardName, kbName);
          hashMap.put(KMManager.KMKey_LanguageName, langName);
          hashMap.put(KMManager.KMKey_KeyboardVersion, kbVersion);
          hashMap.put(KMManager.KMKey_CustomKeyboard, isCustom);
          hashMap.put(KMManager.KMKey_Font, kbFont);
          keyboardsInfo.put(kbKey, hashMap);

          if (keyboardModifiedDates.get(kbID) == null)
            keyboardModifiedDates.put(kbID, keyboard.getString(KMManager.KMKey_KeyboardModified));
        }
        kbName = "";


        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(KMManager.KMKey_LanguageName, langName);
        hashMap.put(KMManager.KMKey_KeyboardName, kbName);
        hashMap.put(iconKey, icon);
        hashMap.put("isEnabled", isEnabled);
        keyboardsArrayList.add(hashMap);


        String[] from = new String[]{KMManager.KMKey_LanguageName, KMManager.KMKey_KeyboardName, iconKey};
        int[] to = new int[]{R.id.text1, R.id.text2, R.id.image1};
        ListAdapter adapter = new KMListAdapter(context, keyboardsArrayList, R.layout.list_row_layout2, from, to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            selectedIndex = position;
            String kbName = keyboardsArrayList.get(+position).get(KMManager.KMKey_KeyboardName);
            String langName = keyboardsArrayList.get(+position).get(KMManager.KMKey_LanguageName);

            if (kbName == "") {
              Intent i = new Intent(context, KeyboardListActivity.class);
              i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              i.putExtra("selectedIndex", selectedIndex);
              int listPosition = listView.getFirstVisiblePosition();
              i.putExtra("listPosition", listPosition);
              View v = listView.getChildAt(0);
              int offsetY = (v == null) ? 0 : v.getTop();
              i.putExtra("offsetY", offsetY);
              startActivityForResult(i, 1);
            } else {
              HashMap<String, String> kbInfo = getKeyboardInfo(selectedIndex, 0);
              final String pkgID = kbInfo.get(KMManager.KMKey_PackageID);
              final String kbID = kbInfo.get(KMManager.KMKey_KeyboardID);
              final String langID = kbInfo.get(KMManager.KMKey_LanguageID);
              String kFont = MapCompat.getOrDefault(kbInfo, KMManager.KMKey_Font, "");
              String kOskFont = MapCompat.getOrDefault(kbInfo, KMManager.KMKey_OskFont, "");
              String isCustom = MapCompat.getOrDefault(kbInfo, KMManager.KMKey_CustomKeyboard, "N");

              if (!pkgID.equals(KMManager.KMDefault_UndefinedPackageID)) {
                // Custom keyboard already exists in packages/ so just add the language association
                KeyboardPickerActivity.addKeyboard(context, kbInfo);
                KMManager.setKeyboard(pkgID, kbID, langID, kbName, langName, kFont, kOskFont);
                Toast.makeText(context, "Keyboard installed", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                ((AppCompatActivity) context).finish();
              } else {
                // Keyboard needs to be downloaded
                Bundle bundle = new Bundle();
                bundle.putString(KMKeyboardDownloaderActivity.ARG_PKG_ID, pkgID);
                bundle.putString(KMKeyboardDownloaderActivity.ARG_KB_ID, kbID);
                bundle.putString(KMKeyboardDownloaderActivity.ARG_LANG_ID, langID);
                bundle.putString(KMKeyboardDownloaderActivity.ARG_KB_NAME, kbName);
                bundle.putString(KMKeyboardDownloaderActivity.ARG_LANG_NAME, langName);
                bundle.putBoolean(KMKeyboardDownloaderActivity.ARG_IS_CUSTOM, isCustom.toUpperCase().equals("Y"));
                Intent i = new Intent(getApplicationContext(), KMKeyboardDownloaderActivity.class);
                i.putExtras(bundle);
                startActivity(i);
              }
            }
          }
        });

        Intent i = getIntent();
        listView.setSelectionFromTop(i.getIntExtra("listPosition", 0), i.getIntExtra("offsetY", 0));
      } catch (JSONException e) {
        Log.e("JSONParse", "Error: " + e);
      }
    }
  }
}
