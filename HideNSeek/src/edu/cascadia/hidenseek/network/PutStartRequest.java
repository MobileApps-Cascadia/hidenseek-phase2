package edu.cascadia.hidenseek.network;

import edu.cascadia.hidenseek.Match;
import edu.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PutStartRequest extends NetworkRequest {

	public void DoRequest(Match toStart) {
		m = toStart;
		Request r = new Request();
		r.url = baseUrl + "matches/" + toStart.GetId() + "/start/";
		r.type = RequestType.PUT;
		r.jsonArgs = m.ToJSONStart();
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete(Match match) { }
	
	@Override
	protected final void processPostExecute(String s) {
		m.StartMatch();
		onComplete(m);
	}
	
	private Match m;

}
