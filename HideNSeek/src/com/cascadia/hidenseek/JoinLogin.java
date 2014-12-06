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
import android.widget.ImageView;

import com.cascadia.hidenseek.network.GetMatchRequest;
import com.cascadia.hidenseek.network.PostPlayerRequest;

public class JoinLogin extends Activity {

	String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_login);
		initSettings();
		
		ImageView pwHelp = (ImageView) findViewById(R.id.JoinPasswordHelp);
		pwHelp.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				HelpDialog helpDialog = new HelpDialog("Get the passworrd from the Match Host/Hostess.", "Password" );
				helpDialog.show(getFragmentManager(), "Help");
			}
		});
		ImageView matchHelp = (ImageView) findViewById(R.id.selectMatchHelp);
		matchHelp.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				HelpDialog helpDialog = new HelpDialog("Select a match to join.  The game will soon start! Hide in a safe place!", "Select a Match.");
				helpDialog.show(getFragmentManager(), "Help");
			}
		});
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
		String intString = SelectMatch.selectedMatch.trim().replaceFirst(" - .*", "");
		int matchId = Integer.parseInt(intString);
		if (LoginManager.GetMatch()!= null && LoginManager.GetMatch().GetId() == matchId)
		{
			HelpDialog helpDialog = new HelpDialog("You can't join the match you already in.", "Join a Match.");
			helpDialog.show(getFragmentManager(), "Help");
			return;
		}
		GetMatchRequest gmRequest = new GetMatchRequest() {
			
			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
			}
			
			@Override
			protected void onComplete(Match match) {
				EditText mPassword = (EditText) findViewById(R.id.JoinPasswordInput);
				EditText pName = (EditText) findViewById(R.id.PlayerNameInput);
				String pwInput = mPassword.getText().toString();
				String pwJason = match.GetPassword();
				if (!pwInput.contains(pwJason) || !pwJason.contains(pwInput))
				{
					String noMatch = mPassword.getText().toString() + " -- " + match.GetPassword();
					HelpDialog helpDialog = new HelpDialog("Enter the correct password for the match! Ask your host!"+ noMatch, "Password" );
					helpDialog.show(getFragmentManager(), "Help");
					return;
				}
				String playerName = pName.getText().toString();
				if (playerName.isEmpty() || playerName.trim().length() <2)
				{					
					HelpDialog helpDialog = new HelpDialog("Player name must at least 2 characters and not blanks!", "Player Name" );
					helpDialog.show(getFragmentManager(), "Help");
					return;
				}
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
		EditText uName = (EditText)findViewById(R.id.PlayerNameInput);
		uName.setText(username);
	}
}
