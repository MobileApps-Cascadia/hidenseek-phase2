package edu.cascadia.hidenseek;

import java.util.List;
import java.util.zip.Inflater;



import com.cascadia.hidenseek.R;
import com.cascadia.hidenseek.network.PutStatusRequest;

import edu.cascadia.hidenseek.Player.Status;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
		final TextView txtTitle = (TextView) rowView.findViewById(R.id.player_name);
		Button btn=(Button) rowView.findViewById(R.id.btn_found);
		txtTitle.setText(web[position]);
		
		 btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String playerStr = txtTitle.getText().toString();
					int sIndex = playerStr.indexOf(',');
					String playerID = playerStr.substring(0, sIndex);
					Player p = null;
					List<Player> players = LoginManager.GetMatch().players;
				    for (int i=0; i< players.size(); i++)
				    {
				    	 if(players.get(i).GetId() == Integer.parseInt(playerID)){
				    	     p = players.get(i);
				    	 }
				    }
				    if (p!=null && p.GetStatus() == Status.Hiding)
				    {
				    	Dialog d = new Dialog(context);
						d.setTitle("Player " + p.GetName() + " Found!");
						d.show(); 
				    }
				   else
					   return;
				    	
				    p.SetStatus(Status.Spotted);
				    PutStatusRequest pp = new PutStatusRequest() {
						
						@Override
						protected void onException(Exception e) {
							e.printStackTrace();
						}
						@SuppressWarnings("unused")
						protected void onComplete(Player p) {
							Dialog d = new Dialog(context);
							d.setTitle("Player " + p.GetName() + " Found!");
							d.show(); 
						}
											
					};
					pp.DoRequest(p);			
					
				}
			});		
		
		return rowView;
	}
}
	
	
	