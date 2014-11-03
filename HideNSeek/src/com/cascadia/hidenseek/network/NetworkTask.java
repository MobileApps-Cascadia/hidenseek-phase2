package com.cascadia.hidenseek.network;

import java.io.IOException;

import android.os.AsyncTask;

public abstract class NetworkTask extends AsyncTask<Request, Void, String> {

	@Override
	protected String doInBackground(Request ... arg0) {
		try {
			return nbase.Request(arg0[0]);
		} catch (IOException e) {
			if(requestCount < 3) {
				requestCount++;
				doInBackground(arg0);
			}
			exception = e;
			return null;
		}
	}
	
	private int requestCount = 0;
	protected Exception exception = null;
	protected static NetworkBase nbase = new NetworkBase();

}
