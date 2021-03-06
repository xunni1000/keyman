package com.tavultesoft.kmapro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.tavultesoft.kmea.KMManager;

import java.util.ArrayList;
import java.util.HashMap;

public class KeymanSettingsFragment extends PreferenceFragmentCompat {
  private static final String TAG = "SettingsFragment";
  private static Context context;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    context = getActivity();
    PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

    // Set the filename so this Fragment uses the common preferences file as other Activities
    getPreferenceManager().setSharedPreferencesName(getString(R.string.kma_prefs_name));

    Preference languagesPreference = new Preference(context);
    ArrayList<HashMap<String, String>> kbList = KMManager.getKeyboardsList(context);
    String keyboardCount = (kbList != null && kbList.size() > 1) ?
      String.format(" (%d)", kbList.size()) : "";
    languagesPreference.setKey(KeymanSettingsActivity.installedLanguagesKey);
    languagesPreference.setTitle(getString(R.string.installed_languages) + keyboardCount);
    languagesPreference.setWidgetLayoutResource(R.layout.preference_icon_layout);
    Intent languagesIntent = new Intent();
    languagesIntent.setClassName(context.getPackageName(), "com.tavultesoft.kmea.LanguagesSettingsActivity");
    languagesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    languagesIntent.putExtra(KMManager.KMKey_DisplayKeyboardSwitcher, false);
    languagesPreference.setIntent(languagesIntent);

    SwitchPreference bannerPreference = new SwitchPreference(context);
    bannerPreference.setKey(KeymanSettingsActivity.showBannerKey);
    bannerPreference.setTitle(getString(R.string.show_banner));
    bannerPreference.setSummaryOn(getString(R.string.show_banner_on));

    SwitchPreference getStartedPreference = new SwitchPreference(context);
    getStartedPreference.setKey(GetStartedActivity.showGetStartedKey);
    getStartedPreference.setTitle(getString(R.string.show_get_started));
    getStartedPreference.setDefaultValue(true);

    screen.addPreference(languagesPreference);
    screen.addPreference(bannerPreference);
    screen.addPreference(getStartedPreference);

    setPreferenceScreen(screen);
  }
}
