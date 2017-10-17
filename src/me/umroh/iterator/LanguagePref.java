package me.umroh.iterator;

import me.umroh.iterator.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LanguagePref extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.language_pref);
		
	}

}
