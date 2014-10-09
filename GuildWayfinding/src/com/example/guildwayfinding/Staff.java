package com.example.guildwayfinding;

public class Staff {
	private String name;
	private int room;
	private String faculty;
	private String telephone;
	private String email;
	private String mon;
	private String tues;
	private String wed;
	private String thurs;
	private String fri;

	
	public Staff(String name, int room, String faculty, String telephone, String email, String Mon, String Tues, String Wed, String Thurs, String Fri) 
	{
		this.name = name;
		this.room = room;
		this.faculty = faculty;
		this.telephone = telephone;
		this.email = email;
		this.mon = Mon;
		this.tues = Tues;
		this.wed = Wed;
		this.thurs = Thurs;
		this.fri = Fri;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRoom() {
		return room;
	}
	
	public String getFaculty() {
		return faculty;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getMon() {
		return mon;
	}
	
	public String getTues() {
		return tues;
	}
	
	public String getWed() {
		return wed;
	}
	
	public String getThurs() {
		return thurs;
	}
	
	public String getFri() {
		return fri;
	}
}
