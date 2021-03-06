package com.useless.tpds;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Register extends UserAuth implements OnClickListener {
	private Editable realname;
	private Button buttonRegisterLogin, buttonLoginExisting;
	private EditText editTextRealname, editTextUsername, editTextPassword, editTextPassword2;
	
	@Override
	//constructor called when login created
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.register_title);
		
		savePassword = (CheckBox) findViewById(R.id.savePassword);
		
		editTextRealname = (EditText) findViewById(R.id.editTextRealname);
		editTextRealname.setSelectAllOnFocus(true);
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		editTextUsername.setSelectAllOnFocus(true);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextPassword.setSelectAllOnFocus(true);
		editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
		editTextPassword2.setSelectAllOnFocus(true);
		
		buttonRegisterLogin = (Button) findViewById(R.id.buttonRegisterLogin);
		buttonRegisterLogin.setOnClickListener(this);
		buttonLoginExisting = (Button) findViewById(R.id.buttonLoginExisting);
		buttonLoginExisting.setOnClickListener(this);
	}

	@Override
	//called when click listener triggers
	public void onClick(View v) {
		if(v == buttonRegisterLogin) { //login button pressed
			//get values from text boxes
			realname = editTextRealname.getText();
			username = editTextUsername.getText();
			password = editTextPassword.getText();
			Editable password2 = editTextPassword2.getText();
			
			if(!String.valueOf(password).equals(String.valueOf(password2))) {
				//passwords do not match
				alert(getBaseContext().getString(R.string.register_nomatch));
				editTextPassword.setText("");
				editTextPassword2.setText("");
				editTextPassword.requestFocus();
				return;
			} else if(password.length() < minPasswordLength) {
				//password too short
				alert(getBaseContext().getString(R.string.register_tooshort));
				editTextPassword.setText("");
				editTextPassword2.setText("");
				editTextPassword.requestFocus();
				return;
			} else if(!username.toString().matches("\\w+")) {
				alert(getBaseContext().getString(R.string.register_username_illegal_chars));
				editTextUsername.setText("");
				editTextUsername.requestFocus();
			} else if(!realname.toString().matches("[\\w\\ ]+")) {
				alert(getBaseContext().getString(R.string.register_realname_illegal_chars));
				editTextRealname.setText("");
				editTextRealname.requestFocus();
			} else {
				ProgressDialog progress = ProgressDialog.show(this, "Please Wait", "Registering User...", true);
				
				//build query and get JSON
				String requestUrl = "http://snarti.nu/?data=user&action=register";
				requestUrl += "&user=" + String.valueOf(username);
				requestUrl += "&password=" + md5(password);
				requestUrl += "&realname=" + String.valueOf(realname).replace(" ", "%20");
				JSONObject result = Database.get(requestUrl);
				
				if(result != null) {//JSON obtained
					if(result.has("error")) {//error registering user
						progress.dismiss();
						try {
							alert(result.getString("error"));
						} catch(Exception e) {
							alert(getString(R.string.register_error_generic));
						}
						editTextUsername.setText("");
						editTextPassword.setText("");
						editTextPassword2.setText("");
						editTextUsername.requestFocus();
					} else if(result.has("token")) {//token obtained, login successful
						activeUser = buildBundle(result);
						
						//save token for future use
						if(savePassword.isChecked()) {
							storeToken();
				        }
						
						setResult(LOGIN_SUCCESSFUL,new Intent().putExtras(activeUser));
						progress.dismiss();
						finish();
					}
				}
			}
		} else if(v == buttonLoginExisting) {
			setResult(LOGIN_UNSUCCESSFUL);
			finish();
		}
	}
}
