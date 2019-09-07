package com.example.firebasetest1.RestClient.Model;

public class HouseCreation {
	private User user;
	private House house;
	private State state;
	private Suburb suburb;
	public HouseCreation() {
		
	}
	public HouseCreation(User user, House house, State state, Suburb suburb) {
		super();
		this.user = user;
		this.house = house;
		this.state = state;
		this.suburb = suburb;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Suburb getSuburb() {
		return suburb;
	}
	public void setSuburb(Suburb suburb) {
		this.suburb = suburb;
	}
	
	
}
