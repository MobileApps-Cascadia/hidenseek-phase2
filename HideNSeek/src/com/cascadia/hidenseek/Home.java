package com.cascadia.hidenseek;

import java.util.List;

import Helper.NoteItem;
import Helper.NotesDataSource;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class Home extends Activity {

	
	private NotesDataSource datasource;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		
		//pass in the current instance using this
		datasource = new NotesDataSource(this);
		List<NoteItem> notes = datasource.findAll();
		NoteItem note = notes.get(0);
		//set text of object and pass into the update and then saves to persistent storage
		note.setText("Updated");
		
		
		
		//adding the note object and saving it persistently
		datasource.update(note);
		
		
		notes = datasource.findAll();
		note = notes.get(0);
		
		
		//create log class for debugging based on the note from the Notes class
		Log.i("NOTES", note.getKey() + ": " + note.getKey());
	
		
		//User clicked Host Match button
        ImageButton btnHost = (ImageButton) findViewById(R.id.btnHostHome);
        btnHost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(Home.this, HostLogin.class);
    			startActivity(intent);
            }
        });
        //User clicked Join Match button
        ImageButton btnJoin = (ImageButton) findViewById(R.id.btnJoinHome);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
    			Intent intent = new Intent(Home.this, JoinLogin.class);
    			startActivity(intent);
            }
        });
        //User clicked Settings button
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Settings.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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

}
