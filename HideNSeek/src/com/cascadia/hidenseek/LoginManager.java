package com.cascadia.hidenseek;

import com.cascadia.hidenseek.Match.MatchType;

import android.content.Intent;

public class LoginManager {

	public LoginManager() {	} 
	
	public static Match ValidateHostLogin(String matchName, String password, int matchType ) {
		if(matchType == 0) {
			m = new Match(matchName, password, MatchType.HideNSeek);
		} else {
			m = new Match(matchName, password, MatchType.Sandbox);
		}
		isHost = true;
		return m;
	}
	
	public static void ValidateJoinLogin(Player p) {
		playerMe = p;
		m = p.GetAssociatedMatch();
		isHost = false;
	}
	
	public static void SetMatch(Match match) {
		m = match;
	}
	
	public static Match GetMatch() {
		return m;
	}
	
	public static Player playerMe;
	private static Match m;
	public static boolean isHost;

}
