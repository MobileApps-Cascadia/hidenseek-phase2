package com.cascadia.hidenseek;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.PostPlayerRequest;

public class JoinLogin extends Activity {

	String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_login);
		initSettings();
		
		ImageButton btnJoin = (ImageButton) findViewById(R.id.btnJoinJoin);
		btnJoin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				joinMatch();
			}
		});
		Button btnSelectMatch = (Button) findViewById(R.id.btnJoinSelectMatch);
		if(SelectMatch.selectedMatch != null) {
			btnSelectMatch.setText(SelectMatch.selectedMatch);
		}
		btnSelectMatch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(JoinLogin.this, SelectMatch.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Button btnSelectMatch = (Button) findViewById(R.id.btnJoinSelectMatch);
		if(SelectMatch.selectedMatch != null) {
			btnSelectMatch.setText(SelectMatch.selectedMatch);
		}
	}

	private void joinMatch() {
		if(SelectMatch.selectedMatch == null) {
			//Error!
			return;
		}
		String intString = SelectMatch.selectedMatch.replaceFirst(" - .*", "");
		int matchId = Integer.parseInt(intString);
		
		GetMatchRequest gmRequest = new GetMatchRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
			@Override
			protected void onComplete(Match match) {
				EditText mPassword = (EditText) findViewById(R.id.JoinPasswordInput);
				EditText pName = (EditText) findViewById(R.id.TextPlayerNameInput);
				Player p = new Player(pName.getText().toString(), match);
				PostPlayerRequest ppRequest = new PostPlayerRequest() {
					@Override
					protected void onException(Exception e) {
						// TODO Auto-generated method stub
						e.printStackTrace();
					}
					
					@Override
					protected void onComplete(Player p) {
						//TODO: don't keep going if the password was wrong.
						LoginManager.ValidateJoinLogin(p);
						Intent intent = new Intent(JoinLogin.this, HostConfig.class);
	        			startActivity(intent);
					}
				};
				ppRequest.DoRequest(p, mPassword.getText().toString());
			}
		};
		gmRequest.DoRequest(matchId);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Get any stored preferences and put them in the fields when form is loaded
	 */
	private void initSettings(){		
		username = getSharedPreferences("HideNSeek_shared_pref", MODE_PRIVATE).getString("Username","");
		EditText uName = (EditText)findViewById(R.id.TextPlayerNameInput);
		uName.setText(username);
	}
}
