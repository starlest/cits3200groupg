package com.example.prototype;

public class Staff {
	  private long id;
	  private String name, faculty, email;

	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
	  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }
	  
	  public String getFaculty() {
		  return faculty;
	  }
	  
	  public void setFaculty(String faculty) {
		  this.faculty = faculty;
	  }

	  public String getEmail() {
		  return email;
	  }
	  
	  public void setEmail(String email) {
		  this.email = email;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return name + " " + faculty + " " + email;
	  }
}
