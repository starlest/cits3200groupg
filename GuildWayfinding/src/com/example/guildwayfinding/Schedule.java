package com.example.guildwayfinding;

public class Schedule {
	private String mon, tue, wed, thu, fri, sat, sun;
	
	public Schedule(String mon, String tue, String wed, String thu, String fri, String sat, String sun) {
		this.mon = mon;
		this.tue = tue;
		this.wed = wed;
		this.thu = thu;
		this.fri = fri;
		this.sat = sat;
		this.sun = sun;
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
	
	public String getSat() {
		return sat;
	}
	
	public String getSun() {
		return sun;
	}
}
