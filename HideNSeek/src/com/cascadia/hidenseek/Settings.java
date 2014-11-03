package com.cascadia.hidenseek;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {
	Button submit, exit;
	String username, counttime, seektime; 
	EditText userinput, userctime, userstime; 
	SharedPreferences sh_Pref; 
	Editor toEdit; 
	
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.settings); 
		initSettings();
		getInit(); 	
	} 
	
	//Get any stored preferences and put them in the fields when form is loaded
	private void initSettings(){
		username = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Username","");
		EditText uName = (EditText)findViewById(R.id.prefPlayerNameInput);
		uName.setText(username);
		
		counttime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Counttime", "");
		EditText cTime = (EditText)findViewById(R.id.prefCountTimeInput);
		cTime.setText(counttime);
		
		seektime = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Seektime", "");
		EditText sTime = (EditText)findViewById(R.id.prefSeekTimeInput);
		sTime.setText(seektime);
	}
	
	public void getInit() { 
		submit = (Button) findViewById(R.id.submit); 
		exit = (Button) findViewById(R.id.exit); 
		userinput = (EditText) findViewById(R.id.prefPlayerNameInput); 
		userctime = (EditText) findViewById(R.id.prefCountTimeInput);
		userstime = (EditText) findViewById(R.id.prefSeekTimeInput);
		
		submit.setOnClickListener(this); 
		exit.setOnClickListener(this); } 
	
	public void sharedPreferences() { 
		sh_Pref = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE); 
		toEdit = sh_Pref.edit(); 
		toEdit.putString("Username", username);
		toEdit.putString("Counttime", counttime);
		toEdit.putString("Seektime", seektime);
		toEdit.commit(); } 
	
	@Override 
	public void onClick(View currentButton) { 
		switch (currentButton.getId()) { 
			case R.id.submit: 
				username = userinput.getText().toString(); 
				counttime = userctime.getText().toString();
				seektime = userstime.getText().toString();
				sharedPreferences(); 
				Toast.makeText(this, "Details are saved", 20).show(); 
				break; 
			case R.id.exit: 
				finish(); 
		} 
	} 
}