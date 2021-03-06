package com.example.guildwayfinding;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {


  private static final String DATABASE_NAME = "guild.db";
  private static final int DATABASE_VERSION = 13;

  // Database creation sql statement
  private static final String CREATE_STAFF = "CREATE TABLE STAFF " +
			 "(ID INTEGER PRIMARY KEY," +
				" NAME TEXT NOT NULL, " +
				 " ROOM INT NOT NULL, " +
				 " FACULTY TEXT NOT NULL, " +
				 " TELEPHONE TEXT, " +
				 " EMAIL TEXT," +
				 " MONDAY TEXT," +
				 " TUESDAY TEXT," +
				 " WEDNESDAY TEXT," +
				 " THURSDAY TEXT," +
				 " FRIDAY TEXT);";

private static final String CREATE_ROOM = " CREATE TABLE ROOM (ID INTEGER PRIMARY KEY, DESCRIPTION TEXT);";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(CREATE_STAFF);
    database.execSQL(CREATE_ROOM);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(DBHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS STAFF");
    db.execSQL("DROP TABLE IF EXISTS ROOM");
    onCreate(db);
  }

  public void addStaff(String name, int room, String faculty, String telephone, String email, String Mon, String Tues, String Wed, String Thurs, String Fri)
  {
	  SQLiteDatabase db = this.getWritableDatabase();
	  String sql = "INSERT INTO STAFF (ID,NAME,ROOM,FACULTY,TELEPHONE,EMAIL,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY) " +
				 "VALUES (NULL,'" + name + "', " + room + ", '" + faculty + "', '" + telephone + "', '" + email + "', '" + Mon + "', '" + Tues + "', '" + Wed + "', '" + Thurs + "', '" + Fri + "');";
	  db.execSQL(sql);
  }

  public void addRoom (int id, String description)
  {
      SQLiteDatabase db = this.getWritableDatabase();
	  String sql = "INSERT INTO ROOM (ID, DESCRIPTION) " +
				 "VALUES (" + id + ", '" + description + "');";
	  db.execSQL(sql);
  }

  public void editStaff(int id, String name, String room, String faculty, String telephone, String email, String Mon, String Tues, String Wed, String Thurs, String Fri)
  {
      SQLiteDatabase db = this.getWritableDatabase();
      if (name != null)
      {
          String sql = 
        		  "UPDATE STAFF SET NAME = '" + name + "', "
        		  	+ "ROOM = "+room+","
        		  	+ "FACULTY = '" + faculty + "', "
        		  	+ "TELEPHONE = '" + telephone + "', "
        		  	+ "EMAIL = '" + email + "', "
        		  	+ "MONDAY = '" + Mon + "', "
        		  	+ "TUESDAY = '" + Tues + "', "
        		  	+ "WEDNESDAY = '" + Wed + "', "
        		  	+ "THURSDAY = '" + Thurs + "', "
        		  	+ "FRIDAY = '" + Fri + "' "
        		  	+ "WHERE ID = " + id +";";
          db.execSQL(sql);
      }

  }
  public List<String> getStaffsIdsNames()
  {
	  List<String> l = new ArrayList<String>();
	  SQLiteDatabase db = this.getReadableDatabase();
	  String sql = "SELECT id,NAME FROM STAFF ORDER BY NAME;";
	  Cursor c = db.rawQuery(sql, null);
	  if( c != null && c.moveToFirst() ) {
		  while (!c.isAfterLast()) {
			  l.add(c.getString(1) + "," + c.getString(0));
			  c.moveToNext();
		  }
		  c.close();
	  }
	  c.close();
	  return l;
  }

  public List<String> getFacultyNames()
  {
	  List<String> l = new ArrayList<String>();
	  SQLiteDatabase db = this.getReadableDatabase();
	  String sql = "SELECT DISTINCT FACULTY FROM STAFF;";
	  Cursor c = db.rawQuery(sql, null);
	  if( c != null && c.moveToFirst() ) {
		  while (!c.isAfterLast()) {
			  l.add(c.getString(0));
			  c.moveToNext();
		  }
		  c.close();
	  }
	  c.close();
	  return l;
  }

  public List<String> getFacultyStaffsIdsNames(String faculty)
  {
	  List<String> l = new ArrayList<String>();
	  SQLiteDatabase db = this.getReadableDatabase();
	  String sql = "SELECT id, NAME FROM STAFF WHERE FACULTY = '" + faculty + "';";
	  Cursor c = db.rawQuery(sql, null);
	  if( c != null && c.moveToFirst() ) {
		  while (!c.isAfterLast()) {
			  l.add(c.getString(1) + "," + c.getString(0));
			  c.moveToNext();
		  }
		  c.close();
	  }
	  return l;
  }

  public Staff getStaff(int id) {
	  Staff s = null;
	  SQLiteDatabase db = this.getReadableDatabase();
	  String sql = "SELECT * FROM STAFF WHERE id =  '" + id + "';";
	  Cursor c = db.rawQuery(sql, null);

	  if( c != null && c.moveToFirst() ) {

		  s = new Staff(c.getString(1), c.getInt(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));

		  c.close();
	  }

	  return s;
  }

}
