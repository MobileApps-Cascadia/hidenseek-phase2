package com.cascadia.hidenseek;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>{
	
	private final Activity context;
	private final String[] web;
	public CustomList(Activity context, String[] web) {
		super(context, R.layout.list_single, web);
		this.context = context;
		this.web = web;
	}
	//Add roles here
	String[] radio = {
			"Seek", "Hide", "Supervise"
	};
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		//Create listview
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.list_single, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		
		//TEMP: Get match type from db
		String matchType = "sandbox";
		
		//Code to add radio buttons for hide'n'seek game
		if(matchType == "hide-n-seek"){
			final RadioButton[] rb = new RadioButton[3];
			RadioGroup rg = (RadioGroup) rowView.findViewById(R.id.radioGroup1);
			rg.setOrientation(RadioGroup.HORIZONTAL);
			for (int i = 0; i < 3; i++) {
				rb[i] = new RadioButton(context);
			    rg.addView(rb[i]);
			    rb[i].setText(radio[i]);
			   }
		}
		
		txtTitle.setText(web[position]);
	
		return rowView;
	}
}




