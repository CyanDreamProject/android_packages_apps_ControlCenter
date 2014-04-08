package com.cyandream.controlcenter.updatechecker;

import com.cyandream.controlcenter.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class cmchanges extends Activity {
	private WebView mWebView;
    ProgressDialog _dialog ;
	SharedPreferences preferences;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
	    preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String usenightly;
		usenightly = preferences.getString("buildtype","-release");
	    	mWebView = (WebView) findViewById(R.id.webview);
	    	mWebView.loadUrl("http://yanniks.de/roms/cd-changes.php?device=" + android.os.Build.PRODUCT + usenightly);
	        mWebView.setInitialScale(110);
	        mWebView.setBackgroundColor(0x00000000); 
	        mWebView.setWebViewClient(new WebViewClient(){

@Override
public void onPageStarted(WebView view, String url, Bitmap favicon) {
 // TODO Auto-generated method stub
 _dialog =ProgressDialog.show(cmchanges.this, "", getString(R.string.wait));
 super.onPageStarted(view, url, favicon);
}
@Override
public void onPageFinished(WebView view, String url) {
 // TODO Auto-generated method stub
 super.onPageFinished(view, url);
 _dialog.dismiss();
}

@Override
public void onReceivedError(WebView view, int errorCode,
  String description, String failingUrl) {
 // TODO Auto-generated method stub
 super.onReceivedError(view, errorCode, description, failingUrl);
 mWebView.loadUrl("file:///android_asset/" + getString(R.string.local) + "-error.html");
 try{
  _dialog.dismiss();
 }catch (Exception e) {
  // TODO: handle exception
 }
}
});
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.changes, menu);
        return super.onCreateOptionsMenu(menu);
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
    	            if(item.getItemId() == R.id.refresh){
    	                mWebView.reload();
    	                return true;
    	}
    	}
        return super.onOptionsItemSelected(item);
    }
    }
}
