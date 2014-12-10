package edu.cascadia.hidenseek.network;

import edu.cascadia.hidenseek.Player;
import edu.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PutPlayingRequest extends NetworkRequest {

	public void DoRequest(Player p) {
		Request r = new Request();
		r.url = baseUrl + "players/" + p.GetId() + "/playing/";
		r.type = RequestType.PUT_NoArgs;
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete() { }
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete();
	}
}
