package com.cascadia.hidenseek.network;

import java.util.List;

import org.json.JSONException;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class GetPlayerListRequest extends NetworkRequest {

	/**
	 * Request a list of players for the given match. Calls the onComplete
	 * method with the updated match passed in when complete. Calls onException
	 * in case of IOException or JSONException
	 */
	public void DoRequest(Match match) {
		Request r = new Request();
		m = match;
		r.url = baseUrl + "matches/" + m.GetId() + "/players/";
		r.type = RequestType.GET;
		doRequest(r);
	}
	
	protected abstract void onComplete(Match match);
	
	@Override
	protected final void processPostExecute(String s) {
		try {
			m.players = Player.ParseToList(s, m); 
		} catch(JSONException e) {
			onException(e);
		}
		onComplete(m);
	}
	
	private Match m;
}
