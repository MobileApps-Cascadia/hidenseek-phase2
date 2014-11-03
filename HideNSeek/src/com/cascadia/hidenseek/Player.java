package com.cascadia.hidenseek;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.cascadia.hidenseek.Match.Status;
import com.cascadia.hidenseek.network.LocationParser;

public class Player {

	public enum Role {
		Hider,
		Seeker,
		Supervisor;
		
		public static Role Parse(String s) {
			if(s.equalsIgnoreCase("hider")){
				return Hider;
			} else if (s.equalsIgnoreCase("seeker")) {
				return Seeker;
			} else return Supervisor;
		}
		
		public String GetApiString() {
			switch(this) {
			case Hider:
				return "hider";
			case Seeker:
				return "seeker";
			default:
				return "admin";
			}
		}
	}
	
	public Player(String name, Match match) {
		this.name = name;
		this.associatedMatch = match;
	}

	public static List<Player> ParseToList(String jsonStr, Match associatedMatch)
			throws JSONException {
		List<Player> toReturn = new ArrayList<Player>();
		JSONArray jArray = new JSONObject(jsonStr).getJSONArray("players");
		for(int i = 0; i < jArray.length(); i++) {
			toReturn.add(parse(jArray.getJSONObject(i), associatedMatch));
		}
		return toReturn;
	}
	
	public String ToJSONPost(String password) throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("name", name);
		jObject.put("password", password);
		return jObject.toString();
	}
	
	public String RoleToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("role", role.GetApiString());
		return jObject.toString();
	}
	
	public String LocationToJSON() throws JSONException {
		JSONObject jObject = new JSONObject();
		jObject.put("gps", LocationParser.GetString(location));
		return jObject.toString();
	}
	
	public boolean ProcessPostResponse(String jsonStr) {
		try {
			playerId = new JSONObject(jsonStr).getInt("playerID");
			if(playerId == 0) {
				return false;
			}
			else return true;
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static Player parse(JSONObject jObject, Match associatedMatch)
			throws JSONException {
		Player toReturn = new Player(jObject.getString("name"), associatedMatch);
		toReturn.playerId = jObject.getInt("id");
		toReturn.role = Role.Parse(jObject.getString("role"));

		try {
			toReturn.location = LocationParser.Parse(jObject.getString("GPSLocation"));
			toReturn.lastUpdatedLocation = dateTimeFormat.parse(jObject.getString("lastUpdated"));
		} catch(JSONException e) {
		} catch(ParseException e) {
			//Assume that the exception means to leave the current values alone
		}
		return toReturn;
	}
	
	public String GetName() {
		return name;
	}
	
	public Location GetLocation() {
		return location;
	}
	
	public void SetLocation(Location l) {
		location = l;
	}
	
	public Date GetLastUpdatedLocation() {
		return lastUpdatedLocation;
	}
	
	public Role GetRole() {
		return role;
	}
	
	public void SetRole(Role r) {
		role = r;
	}
	
	public int GetId() {
		return playerId;
	}
	public Match GetAssociatedMatch() {
		return associatedMatch;
	}
	
	private Location location;
	private Date lastUpdatedLocation;
	private Match associatedMatch;
	private String name;
	private Role role;
	private int playerId = -1;
	
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
}
