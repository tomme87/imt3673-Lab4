package no.ntnu.tomme87.imt3673.lab4;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Tomme on 03.04.2018.
 */

public class SettingsFragment extends PreferenceFragment {
    static final String FREQUENCY = "pref_frequency";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
