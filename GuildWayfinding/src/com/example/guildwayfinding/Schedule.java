package com.example.guildwayfinding;

public class Schedule {
	private String mon, tue, wed, thu, fri;
	
	public Schedule(String mon, String tue, String wed, String thu, String fri) {
		this.mon = mon;
		this.tue = tue;
		this.wed = wed;
		this.thu = thu;
		this.fri = fri;
	}
	
	public String getMon() {
		return mon;
	}
	
	public String getTue() {
		return tue;
	}
	
	public String getWed() {
		return wed;
	}
	
	public String getThu() {
		return thu;
	}
	
	public String getFri() {
		return fri;
	}
}
