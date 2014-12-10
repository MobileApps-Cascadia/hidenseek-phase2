package com.cascadia.hidenseek.network;

import org.json.JSONException;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PutGpsRequest extends NetworkRequest {

	public void DoRequest(Player p) {
		if(p.GetLocation() == null) {
			onException(new NullPointerException("Null location in PutGpsRequest"));
			return;
		}
		Request r = new Request();
		r.url = baseUrl + "players/" + p.GetId() + "/gps/";
		r.type = RequestType.PUT;
		try {
			r.jsonArgs = p.LocationToJSON();
		} catch(JSONException e) {
			onException(e);
		}
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete() { }
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete();
	}

}
