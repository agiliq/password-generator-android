package com.passgen.passwordgenrator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MPasswordActivity extends Activity {
	
    String mPassword;
    TextView mastermPassword;
    Button save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpassword);
        
        mastermPassword = (TextView) findViewById(R.id.textmastermpassword);
        save = (Button) findViewById(R.id.buttonsavemasterpassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mpassword, menu);
        return true;
    }
    
    public void saveMasterPassword(View view){
    	
     mPassword = mastermPassword.getText().toString();
     if(mPassword != "" || mPassword !=null)
     {
         Editor edit = MainActivity.preference.edit();
         edit.putString("masterpassword", mPassword);

         edit.commit();
         edit.apply();
         String str = MainActivity.preference.getString("masterpassword", null);
         Intent intent = new Intent(view.getContext(), MainActivity.class);
         startActivity(intent);
     }
     else
     {
    	 Toast.makeText(this.getApplicationContext(), "Enter master password", Toast.LENGTH_SHORT).show(); 
     }
    }
}
