package com.example.guildwayfinding;

public class Staff {
	private String name;
	private String room;
	private String faculty;
	private String telephone;
	private String email;
	public String mon;
	public String tues;
	public String wed;
	public String thurs;
	public String fri;

	
	public Staff(String name, String room, String faculty, String telephone, String email, String Mon, String Tues, String Wed, String Thurs, String Fri) 
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
	
	public String getRoom() {
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
	
	public int[] getMon() {
		int[] availability = new int[9]; 
		String[] parts = mon.split(",");
		for (int i = 0; i < parts.length; i++) {
			int time = Integer.parseInt(parts[i]); 
			availability[(time - 800) / 100] = 1;
		}
		return availability;
	}
	
	public int[] getTues() {
		int[] availability = new int[9]; 
		String[] parts = tues.split(",");
		for (int i = 0; i < parts.length; i++) {
			int time = Integer.parseInt(parts[i]); 
			availability[(time - 800) / 100] = 1;
		}
		return availability;
	}
	
	public int[] getWed() {
		int[] availability = new int[9]; 
		String[] parts = wed.split(",");
		for (int i = 0; i < parts.length; i++) {
			int time = Integer.parseInt(parts[i]); 
			availability[(time - 800) / 100] = 1;
		}
		return availability;
	}
	
	public int[] getThurs() {
		int[] availability = new int[9]; 
		String[] parts = thurs.split(",");
		for (int i = 0; i < parts.length; i++) {
			int time = Integer.parseInt(parts[i]); 
			availability[(time - 800) / 100] = 1;
		}
		return availability;
	}
	
	public int[] getFri() {
		int[] availability = new int[9]; 
		String[] parts = fri.split(",");
		for (int i = 0; i < parts.length; i++) {
			int time = Integer.parseInt(parts[i]);
			availability[(time - 800) / 100] = 1;
		}
		return availability;
	}
}
