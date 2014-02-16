package com.cyandream.controlcenter.updatechecker;

import com.cyandream.controlcenter.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {
  @SuppressWarnings("deprecation")
@Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getActionBar().setDisplayHomeAsUpEnabled(true);
      addPreferencesFromResource(R.xml.settings);
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
  	{
  	       switch (item.getItemId()) 
  	        {
  	        case android.R.id.home: 
  	            onBackPressed();
  	            break;
  	        default:
  	}
  	}    return super.onOptionsItemSelected(item);
}
}
