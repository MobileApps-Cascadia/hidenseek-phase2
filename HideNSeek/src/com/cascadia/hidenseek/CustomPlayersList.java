package com.cascadia.hidenseek;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CustomPlayersList extends ArrayAdapter<String>{

	private final Activity context;
	private final String[] web;
	public CustomPlayersList(Activity context, String[] web) {
		super(context, R.layout.list_current_player, web);
		this.context = context;
		this.web = web;
	}
	
	//Add roles here
	
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		//Create listview
		
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_current_player, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.player_name);
		Button btn=(Button) rowView.findViewById(R.id.btn_found);
		txtTitle.setText(web[position]);
		
		
		
		return rowView;
	}
}