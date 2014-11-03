package com.cascadia.hidenseek.network;

import java.util.List;

import com.cascadia.hidenseek.Match;
import com.cascadia.hidenseek.network.NetworkBase.RequestType;


public abstract class GetMatchListRequest extends NetworkRequest {
	
	public void DoRequest() {
		Request r = new Request();
		r.url = baseUrl + "matches/";
		r.type = RequestType.GET;
		doRequest(r);
	}
	
	protected abstract void onComplete(List<Match> matches);
	
	@Override
	protected final void processPostExecute(String s) {
		onComplete(Match.ParseToList(s));
	}

}
