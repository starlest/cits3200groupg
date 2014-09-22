package com.example.guildwayfinding;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.*;

public class DBHelper {
	
	public DBHelper()
	{
		AddPerson(1, "lucas", 1, "a", 2, "bar ",2, "bar",  "b"  );
		AddPerson(2, "jorge", 1, "a", 2, "bar ",2, "bar",  "b"  );
		AddPerson(3, "henrique", 1, "a", 2, "school ",2, "bar",  "b"  );
	}
	
	public static void AddPerson(int id, String nam, int age, String address, int room, String department, int schedule, String telephone, String email)
	{
		Connection c = null;
		 Statement stmt = null;
		 try {
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			 c.setAutoCommit(false);
			 System.out.println("Opened database successfully");
			 stmt = c.createStatement();
			 String sql = "INSERT INTO PERSON (ID,NAME,AGE,ADDRESS,ROOM,DEPARTMENT,SCHEDULE,TELEFONE,EMAIL) " +
					 "VALUES (" + id + ", '" + nam + "', " + age + ", '" + address  +
					 "', " + room + ", '" + department + "', " + schedule + ", '" + telephone + "', '" + email + "');";
			 stmt.executeUpdate(sql);
			 stmt.close();
			 c.commit();
			 c.close();
		 } 
		 catch ( Exception e ) {
			 System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			 System.exit(0);
		 }
		 System.out.println("Records created successfully");
	}
	
	public static void AddRoom(int id, String Name)
	{
		Connection c = null;
		 Statement stmt = null;
		 try {
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			 c.setAutoCommit(false);
			 System.out.println("Opened database successfully");
			 stmt = c.createStatement();
			 String sql = "INSERT INTO ROOM (ID, NAME) " +
					 "VALUES (" + id + ", '" + Name + "');";
			 System.out.println("1");
			 stmt.executeUpdate(sql);
			 System.out.println("1");
			 stmt.close();
			 c.commit();
			 c.close();
		 } 
		 catch ( Exception e ) {
			 System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			 System.exit(0);
		 }
		 System.out.println("Records created successfully");	
	}
	
	public static void addSchedule(int id, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday)
	{
		Connection c = null;
		 Statement stmt = null;
		 try {
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			 c.setAutoCommit(false);
			 System.out.println("Opened database successfully");
			 stmt = c.createStatement();
			 String sql = "INSERT INTO SQUEDULE (ID, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY) " +
					 "VALUES (" + id + ", " + monday + ", " + tuesday + ", " + thursday + 
					 ", " + wednesday + ", " + thursday + ", " + friday + ", "
					 + saturday + ", " + sunday + ");";
			 stmt.executeUpdate(sql);
			 stmt.close();
			 c.commit();
			 c.close();
		 } 
		 catch ( Exception e ) {
			 System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			 System.exit(0);
		 }
		 System.out.println("Records created successfully");
	}
	
	public static List<String> getStaffNames()
	{
		List<String> strlist = new ArrayList<String>();
		Connection c = null;
		 Statement stmt = null;
		 try {
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			 c.setAutoCommit(false);
			 System.out.println("Opened database successfully");
			 stmt = c.createStatement();
			 ResultSet rs = stmt.executeQuery( "SELECT NAME FROM PERSON;" );
			 while ( rs.next() ) {
				 String name = rs.getString("name");
				 strlist.add(name);
			 }
			 rs.close();
			 stmt.close();
			 c.close();
		 } 
		 catch ( Exception e ) {
			 System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			 System.exit(0);
		 }
		 System.out.println("Operation done successfully");
		 return strlist;

	}
	
	public static List<String> getDepartmentNames()
	{
		List<String> strlist = new ArrayList<String>();
		Connection c = null;
		 Statement stmt = null;
		 try {
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:DataBase.db");
			 c.setAutoCommit(false);
			 System.out.println("Opened database successfully");
			 stmt = c.createStatement();
			 ResultSet rs = stmt.executeQuery( "SELECT DISTINCT DEPARTMENT FROM PERSON;" );
			 while ( rs.next() ) {
				 String name = rs.getString("department");
				 strlist.add(name);
			 }
			 rs.close();
			 stmt.close();
			 c.close();
		 } 
		 catch ( Exception e ) {
			 System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			 System.exit(0);
		 }
		 System.out.println("Operation done successfully");
		 return strlist;
	}
}
