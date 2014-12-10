package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.network.NetworkBase.RequestType;

import edu.cascadia.hidenseek.Match;

public class PutStopRequest extends NetworkRequest{
	public void DoRequest(Match toStop) {
		m = toStop;
		Request r = new Request();
		r.url = baseUrl + "matches/" + toStop.GetId() + "/stop/";
		r.type = RequestType.PUT;
		r.jsonArgs = m.ToJSONStart();
		doRequest(r);
	}
	protected void onComplete(Match match) { }
	@Override
	protected void onException(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processPostExecute(String s) {
		// TODO Auto-generated method stub
		m.stopMatch();
		onComplete(m);
		
	}
	private Match m;

}
