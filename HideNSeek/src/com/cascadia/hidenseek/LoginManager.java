package com.cascadia.hidenseek;

import com.cascadia.hidenseek.Match.MatchType;
import com.cascadia.hidenseek.Player.Role;



public class LoginManager {
	public static Player playerMe;
	private static Match m;
	public static boolean isHost;
	static Role seek = Role.Seeker;
	
	
	
	

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
		if(isHost)
		{
			playerMe.SetRole(seek);
		}
		m = p.GetAssociatedMatch();
		isHost = false;
	}
	
	public static void SetMatch(Match match) {
		m = match;
	}
	
	public static Match GetMatch() {
		return m;
	}
	
	

}
