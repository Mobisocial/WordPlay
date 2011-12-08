package edu.stanford.mobisocial.games.wordplay.players;

import mobisocial.socialkit.musubi.User;

public class Player {
	private String name;
	private String id;
	private int score;
	
	public Player(String name) {
		this.name = name;
		this.score = 0;
		id = "player";
	}
	
	public void setPlayer(User user) {
		this.name = user.getName();
		this.id = user.getId();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String iName) {
		this.name = iName;
	}
	
	public String getShortName() {
		if (name.length() <= 8) {
			return name;
		}
		else {
			return name.substring(0, 8);
		}
	}
	
	public int getScore() {
		return score;
	}
	
	public void incrementScore(int add) {
		score += add;
	}
	
	public void setScore(int total) {
		score = total;
	}

}
