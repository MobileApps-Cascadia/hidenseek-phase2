package com.cascadia.hidenseek.network;

import com.cascadia.hidenseek.network.NetworkBase.RequestType;

public class Request {

	public RequestType type = RequestType.GET;
	public String url;
	public String jsonArgs;

}
