package com.passgen.passwordgenrator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.flurry.android.FlurryAgent;
import com.google.ads.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	TextView masterPassword;
	TextView domainName;
	TextView generatedPassword;
	String mPassword, dName, gPassword;
	static SharedPreferences preference;
	Intent intent;
	DataAccess dataAccess;
	AdView adView;
 
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "SVGJBSZK9J7BBQ7Z99VX");
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        		AssetManager am = this.getAssets();
        		InputStream tldNameStream = am.open("tldNames");
     			dataAccess = new DataAccess(tldNameStream);
     		} catch (IOException e) {
     			e.printStackTrace();
     		}
        
        
        
        preference = getPreferences(MODE_PRIVATE);
        mPassword = preference.getString("masterpassword", null);
        
        intent = getIntent();
        if(intent != null)
        {
        	if(intent.getAction() != null)
        	{
        		if(intent.getAction().equals(Intent.ACTION_SEND))
        		{
        			String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        			dName = url;
        		}
        		else
        		{
        			dName = "";
        		}
        	}
        }

        setIntent(mPassword);
    }

    /***
     * Method to Set Intent
     * 
     * @param mPass
     */
    public void setIntent(String mPass)
    {
        
        if(mPass == null)
        {
        	intent = new Intent(this.getApplicationContext(), MPasswordActivity.class);
        	startActivity(intent);
        
        }else
        {
        	   // Look up the AdView as a resource and load a request.
            
        	setContentView(R.layout.activity_main);
        	adView = (AdView) findViewById(R.id.ad);
            masterPassword = (TextView) findViewById(R.id.textmasterpassword);
            masterPassword.setText(mPassword);
            domainName = (TextView) findViewById(R.id.textdomainname);
            domainName.setText(dName);
            generatedPassword = (TextView) findViewById(R.id.textgeneratedpassword);
            generatedPassword.setText(null);

        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    		case R.id.menumasterpassword:
    			changeMasterPassword();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	
    	}
    }
    

    
    /****
     * Menu method to go to Master Password activity to change Master Password
     */
    private void changeMasterPassword() {
    	
    	Intent intent = new Intent(this.getApplicationContext(), MPasswordActivity.class);
    	startActivity(intent);
		
	}

	/***
     * On Button Click generate Password
     * @param view
     */
    public void generatePassword(View view)
    {
    	if(domainName.getText().toString().equals(null) || domainName.getText().toString().equals(" "))
    	{
    		Toast.makeText(this.getApplicationContext(), "Enter Domain Name", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		dName = domainName.getText().toString();
    		if(!dName.startsWith("http://") && !dName.startsWith("https://") && !dName.startsWith("ftp://"))
    		{
    			dName = "http://"+ dName;
    		}
    		
    		if(generateMdPassword(mPassword, dName).equals("error"))
    		{
        		Toast.makeText(this.getApplicationContext(), "Invalid URl", Toast.LENGTH_SHORT).show();
    		}
    		else
    		{
    			gPassword = generateMdPassword(mPassword, dName);
    			generatedPassword.setText(gPassword);
    		}
    		
    	}
    }
    
    
    /***
     * Method to Generate Password using MD5 checksum.
     * Adding the domain name and the master password
     * @param mPassword
     * @param domainName
     * @return
     */
    
    private String generateMdPassword(String mPassword, String domainName)
    {
    	String obtainedPassword = null;
    	domainName = validateURL(domainName);
    	if(domainName.equals("error"))
    	{
    		Toast.makeText(this.getApplicationContext(), "Invalid URl", Toast.LENGTH_SHORT).show();
    	}else{
    		String newPassword = mPassword + domainName;
    		
    		byte[] newPasswordBArray = newPassword.getBytes();
    		try {
    			MessageDigest mDigest = MessageDigest.getInstance("MD5");
    			mDigest.update(newPasswordBArray);
    			byte[] pDigest = mDigest.digest();
			
    			StringBuffer hString = new StringBuffer();
			
    			for(int i=0; i< pDigest.length; i++)
    			{
    				hString.append(Integer.toHexString(0xFF & pDigest[i]));
    			}
    			obtainedPassword = hString.toString();
    			return obtainedPassword.substring(0, 8);
			   
    		} catch (NoSuchAlgorithmException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return domainName;
    	
    	
    }
    
    /***
     * 
     * Method to validate URL.
     * returns error when the URL is not valid(MalFormed)
     * @param domainName
     * @return
     * http://www.google.com
     * www.google.com
     * google.com
     */
    private String validateURL(String domainName)
    {
    	try {
			URL url = new URL(domainName);
			String host = url.getHost();
			String[] params = host.split("\\.");
			int paramsLength = params.length;
			if(params.length>1)
			{
				String query = params[paramsLength-2]+"."+params[paramsLength-1];
				String domain = "";
				if(dataAccess.searchTldNames(query))
				{
					domain = params[paramsLength-3]+"."+query;
				}else
				{
					domain = query;
				}
				return domain;
			}
			else
			{
				return "error";
			}
			
		} catch (MalformedURLException e) {
			
			return "error";
		}
    }
    
    /*
     * Method to Copy Password to Clipboard
     */
    public void copyPassword(View view)
    {
    	if(generatedPassword.getText() != null || generatedPassword.getText() != " ")
    	{
    		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    		ClipData clip = ClipData.newPlainText("password",generatedPassword.getText());
    		clipboard.setPrimaryClip(clip);
    		Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		Toast.makeText(getApplicationContext(), "Password not generated", Toast.LENGTH_SHORT).show();
    	}
    }
    
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adView.stopLoading();
		adView.destroy();
	}



	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
