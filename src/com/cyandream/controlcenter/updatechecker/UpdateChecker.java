package com.cyandream.controlcenter.updatechecker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.impl.client.DefaultHttpClient;

import com.cyandream.controlcenter.R;
import com.cyandream.controlcenter.updatechecker.splash.SplashScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateChecker extends Activity {
    /** Called when the activity is first created. */
    private TextView mTextView, mTextView2, sizetext;
	private DownloadManager mgr=null;
	private long lastDownload=-1L;;
	DefaultHttpClient httpclient = new DefaultHttpClient();
    @SuppressWarnings("unused")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatecheck);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    	Intent i = getIntent();
    	String currentversion = i.getStringExtra("currentversion");
    	String size = i.getStringExtra("size");
    	String otasize = i.getStringExtra("otasize");
    	String filename = i.getStringExtra("filename");
    	String upgradefrom = i.getStringExtra("upgradefrom");
    	String installupdate = i.getStringExtra("installupdate");
    	Button button = (Button) findViewById(R.id.start);
        if (installupdate.equalsIgnoreCase("false")) {
            button.setEnabled(false);
        }
        if (currentversion.equalsIgnoreCase(android.os.Build.VERSION.INCREMENTAL)) {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".zip");
            InputStream is;
            try {
                is = new FileInputStream(f);
                button.setText(R.string.install);
            } catch (FileNotFoundException ex) {
            }
        }
    	mTextView = (TextView) findViewById(R.id.current);
    	mTextView2 = (TextView) findViewById(R.id.installed);
    	sizetext = (TextView) findViewById(R.id.filesize);
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".zip");
            InputStream is;
            try {
                is = new FileInputStream(f);
        	sizetext.setText("");
            } catch (FileNotFoundException ex) {
        if (currentversion.equalsIgnoreCase(android.os.Build.VERSION.INCREMENTAL)) {
        	sizetext.setText("");
        } else {
            File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + upgradefrom);
            InputStream is2;
            try {
                is2 = new FileInputStream(f2);
                sizetext.setText(getString(R.string.size) + " " + otasize);
            } catch (FileNotFoundException ex1) {
            	sizetext.setText(getString(R.string.size) + " " + size);
            }
        }
        }
        mTextView.setText(getString(R.string.current) + " " + currentversion);
        mTextView2.setText(getString(R.string.installed) + " " + android.os.Build.VERSION.INCREMENTAL);
            ((TextView)findViewById(R.id.installedversion)).setText(filename.equals("DoNotFlash") ? getString(R.string.notsupported) : android.os.Build.VERSION.INCREMENTAL.equals(currentversion) ? getString(R.string.noupdates) : getString(R.string.updatesavailable));
        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
      }
      
      @Override
      public void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(onComplete);
      }
      
      public void startDownload() {
        Intent i = getIntent();
        String filename = i.getStringExtra("filename");
        Uri uri=Uri.parse("http://yauniks.dynvpn.de:85/jenkins/mirror/" + filename + ".zip" );
        Environment
          .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
          .mkdirs();
        
        lastDownload=
          mgr.enqueue(new DownloadManager.Request(uri)
                      .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                              DownloadManager.Request.NETWORK_MOBILE)
                      .setAllowedOverRoaming(false)
                      .setTitle(getString(R.string.loadingcurrentrom))
                      .setDescription(getString(R.string.gettingnewbuild))
                      .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                         filename + ".zip"));
        
        findViewById(R.id.query).setEnabled(true);
      }

      public void startDownloadOTA() {
          Intent i = getIntent();
          String filename = i.getStringExtra("filename");
          Uri uri=Uri.parse("http://yauniks.dynvpn.de:85/jenkins/mirror/" + filename + ".patch" );
          Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .mkdirs();
          
          lastDownload=
            mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(getString(R.string.loadingcurrentrom))
                        .setDescription(getString(R.string.gettingnewbuild))
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                           filename + ".patch"));
          
          findViewById(R.id.query).setEnabled(true);
        }

      public void queryStatus(View v) {
        Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));
        
        if (c==null) {
        }
        else {
          c.moveToFirst();
          
          Log.d(getClass().getName(), "COLUMN_ID: "+
                c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
          Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+
                c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
          Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: "+
                c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
          Log.d(getClass().getName(), "COLUMN_LOCAL_URI: "+
                c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
          Log.d(getClass().getName(), "COLUMN_STATUS: "+
                c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
          Log.d(getClass().getName(), "COLUMN_REASON: "+
                c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
          
          Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG).show();
        }
      }
      
      public void viewLog(View v) {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
      }
      
      private String statusMessage(Cursor c) {
        String msg="???";
        
        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
          case DownloadManager.STATUS_SUCCESSFUL:
            break;
          
          default:
            break;
        }
        
        return(msg);
      }
      
      BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
        	createzip(null);
        	delfile(null);
        	delfile2(null);
        	flashupdate(null); 
          findViewById(R.id.start).setEnabled(true);
        }
      };

      @SuppressWarnings("unused")
	public void startdl (final View view) {
    	Button button = (Button) findViewById(R.id.start);
        button.setEnabled(false);
    	Intent i = getIntent();
    	String upgradefrom = i.getStringExtra("upgradefrom");
    	String filename = i.getStringExtra("filename");
        File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".zip");
        InputStream is2;
        try {
            is2 = new FileInputStream(f2);
        	createzip(null);
        	delfile(null);
        	delfile2(null);
        	flashupdate(null); 
          findViewById(R.id.start).setEnabled(true);
        } catch (FileNotFoundException ex1) {
          File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + upgradefrom);
          InputStream is;
          try {
          	is = new FileInputStream(f);
          	startDownloadOTA();
          } catch (FileNotFoundException ex) {
          	startDownload();
          }
        }
      }
      public void delfile (final View view) {
       	Intent i = getIntent();
      	String filename = i.getStringExtra("filename");
    	  File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".patch");
          @SuppressWarnings("unused")
    		boolean deleted = file.delete();
      }
      public void flashupdate (final View view) {
          new AlertDialog.Builder(this)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .setTitle(R.string.prepared)
          .setMessage(R.string.flashnow)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                  flashupdate2(null);   
              }

          })
          .setNegativeButton(R.string.no, null)
          .show();
        }
      public void flashupdate2 (final View view) {
    	        Process process = null;
    	        try {
    	            process = Runtime.getRuntime().exec("su");
    	            DataOutputStream os = new DataOutputStream(process.getOutputStream());
    	            os = new DataOutputStream(process.getOutputStream());
    	            os.writeBytes("reboot recovery\n");
    	            os.writeBytes("exit\n");
    	            os.flush();
    	        } catch (IOException e) {
                    Toast.makeText(UpdateChecker.this, "Error: Reboot failed!", Toast.LENGTH_LONG).show();
    	            e.printStackTrace();
    	    }
      }
      public void delfile2 (final View view) {
         	Intent i = getIntent();
        	String upgradefrom = i.getStringExtra("upgradefrom");
      	  File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + upgradefrom);
            @SuppressWarnings("unused")
      		boolean deleted = file.delete();
        }
      @SuppressWarnings("unused")
	public void createzip (final View view) {
         	Intent i = getIntent();
        	String upgradefrom = i.getStringExtra("upgradefrom");
        	String filename = i.getStringExtra("filename");
            Runtime rt = Runtime.getRuntime();
            Process proc;
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + upgradefrom);
            InputStream is2;
            try {
                is2 = new FileInputStream(f);
			try {
				proc = rt.exec("ls -all");

            proc = rt.exec("xdelta3 -d -s " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + upgradefrom + " " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".patch " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename + ".zip");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

       while ((line = br.readLine()) != null) {
         System.out.println(line);
       }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            } catch (FileNotFoundException ex) {
            }
      }
        @Override
        public boolean onCreateOptionsMenu (Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return super.onCreateOptionsMenu(menu);
        }  

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        	{
        	       switch (item.getItemId()) 
        	        {
        	        case R.id.item2:
        	            Intent intent = new Intent(this, Preferences.class);
        	            startActivity(intent);
        	            return true;
        	        case android.R.id.home: 
        	            onBackPressed();
        	            break;
        	        default:
        	}
        	}
            if(item.getItemId() == R.id.item1){
            	startActivity (new Intent (this,SplashScreen.class));
                Toast.makeText(UpdateChecker.this, getString(R.string.updated), Toast.LENGTH_LONG).show();
                finish();
                return true;
                }
            return super.onOptionsItemSelected(item);
        }
}
