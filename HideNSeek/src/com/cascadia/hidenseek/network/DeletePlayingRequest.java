package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Player;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class DeletePlayingRequest extends NetworkRequest {

	public void DoRequest(Player p) {
		Request r = new Request();
		r.url = baseUrl + "players/" + p.GetId() + "/playing/";
		r.type = RequestType.DELETE;
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete() { }
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete();
	}
}
