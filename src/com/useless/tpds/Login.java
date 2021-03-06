package com.useless.tpds;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Login extends UserAuth implements OnClickListener {
	private Button buttonLogin, buttonRegister;
	private EditText editTextUsername, editTextPassword;
	
	@Override
	//constructor called when login created
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.login_title);
		
		savePassword = (CheckBox) findViewById(R.id.savePassword);
		
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextUsername.setSelectAllOnFocus(true);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setSelectAllOnFocus(true);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(this);
		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setOnClickListener(this);
	}

	@Override
	//called when click listener triggers
	public void onClick(View v) {
		if(v == buttonLogin) { //login button pressed
			ProgressDialog progress = ProgressDialog.show(this, "Please Wait", "Logging you in...", true);
			//get values from text boxes
			username = editTextUsername.getText();
			password = editTextPassword.getText();
			
			//build query and get JSON
			String requestUrl = "http://snarti.nu/?data=user&action=login";
			requestUrl += "&user=" + String.valueOf(username);
			requestUrl += "&password=" + md5(password);
			JSONObject result = Database.get(requestUrl);
			
			if(result != null && result.has("token")) {//token obtained, login successful
				activeUser = buildBundle(result);
				
				//save token for future use
				if(savePassword.isChecked()) {
					storeToken();
		        }
				
				setResult(LOGIN_SUCCESSFUL,new Intent().putExtras(activeUser));
				progress.dismiss();
				finish();
			} else {//else, invalid credentials
				progress.dismiss();
				//show message
				alert(getBaseContext().getString(R.string.login_invalid));
				
				//erase password, set focus
				editTextPassword.setText("");
				editTextPassword.requestFocus();
			}
		} else if(v == buttonRegister) { //register button pressed
			//set result to REGISTER_USER, parent opens Register activity
			setResult(REGISTER_USER);
			finish();
		}
	}
	
}
