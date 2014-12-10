package com.cascadia.hidenseek;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.GetMatchListRequest;

public class SelectMatch extends Activity {

	ListView l;
	
	public static String selectedMatch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_match);

		l = (ListView) findViewById(R.id.configPlayerList);
		
		
		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				selectedMatch = l.getItemAtPosition(arg2).toString();
				finish();
			}
		});
		initList();

	}
	
	/**
	 * initList creates a list of all the matches that are in a pending state
	 */
	private void initList() {
		GetMatchListRequest request = new GetMatchListRequest() {
			
			@Override
			protected void onException(Exception e) { }		
			
			@Override
			protected void onComplete(List<Match> matches) {
				//Gets the list of matches and puts in listview
				ArrayList<String> gameTitles = new ArrayList<String>();
				for(Match m : matches) {
					if(m.GetStatus() == Status.Pending) {
						String title = m.GetId() + " - " + m.GetName();
						gameTitles.add(title);
					}
				}
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectMatch.this,android.R.layout.simple_list_item_single_choice, gameTitles);
				l.setAdapter(arrayAdapter);				
			}
		};
		request.DoRequest();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_match, menu);
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
