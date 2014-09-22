package com.example.guildwayfinding;

public class Staff {
	private String name;
	private int room;
	private String faculty;
	private Schedule schedule;
	private String telephone;
	private String email;

	
	public Staff(String name, int room, String faculty, Schedule schedule, String telephone, String email) 
	{
		this.name = name;
		this.room = room;
		this.faculty = faculty;
		this.schedule = schedule;
		this.telephone = telephone;
		this.email = email;
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
	
	public Schedule getSchedule() {
		return schedule;
	}
	
	public String getTelephone() {
		return telephone;
	}
	
	public String getEmail() {
		return email;
	}
}
