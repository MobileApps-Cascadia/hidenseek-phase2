package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public abstract class PostMatchRequest extends NetworkRequest {

	public void DoRequest(Match toPost) {
		m = toPost;
		Request r = new Request();
		r.url = baseUrl + "matches/";
		r.type = RequestType.POST;
		r.jsonArgs = m.ToJSONPost();
		doRequest(r);
	}
	
	//To be overwritten
	protected void onComplete(Match match) { }
	
	@Override
	protected final void processPostExecute(String s) {
		try {
			m.ProcessPostResponse(s);
		} catch (NullPointerException e) {
			onException(e);
			return;
		}
		onComplete(m);
	}
	
	private Match m;
}
