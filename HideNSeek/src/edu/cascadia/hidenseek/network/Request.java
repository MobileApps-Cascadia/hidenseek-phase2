package edu.cascadia.hidenseek.network;

import edu.cascadia.hidenseek.network.NetworkBase.RequestType;

public class Request {

	public RequestType type = RequestType.GET;
	public String url;
	public String jsonArgs;

}
