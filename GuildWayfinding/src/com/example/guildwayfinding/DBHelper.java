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
  private static final int DATABASE_VERSION = 10;

  // Database creation sql statement
  private static final String CREATE_STAFF = "CREATE TABLE STAFF " +
			 "(ID INTEGER PRIMARY KEY," +
				" NAME TEXT NOT NULL, " +
				 " ROOM INT SECONDARY KEY NOT NULL, " +
				 " FACULTY TEXT NOT NULL, " +
				 " SCHEDULE INT SECONDARY KEY NOT NULL, " +
				 " TELEPHONE TEXT, " +
				 " EMAIL TEXT);";

  private static final String CREATE_SCHEDULE = "CREATE TABLE SCHEDULE " +
			"(ID INT PRIMARY KEY NOT NULL, " +
			" MONDAY TEXT NOT NULL, " +
			 " TUESDAY TEXT NOT NULL, " +
			 " WEDNESDAY TEXT NOT NULL, " +
			 " THURSDAY TEXT NOT NULL, " +
			 " FRIDAY TEXT NOT NULL); ";

private static final String CREATE_ROOM = " CREATE TABLE ROOM (ID INTEGER PRIMARY KEY, DESCRIPTION TEXT);";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(CREATE_STAFF);
    database.execSQL(CREATE_SCHEDULE);
    database.execSQL(CREATE_ROOM);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(DBHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS STAFF");
    db.execSQL("DROP TABLE IF EXISTS SCHEDULE");
    db.execSQL("DROP TABLE IF EXISTS ROOM");
    onCreate(db);
  }

  public void addStaff(String name, int room, String faculty, int schedule, String telephone, String email)
  {
	  SQLiteDatabase db = this.getWritableDatabase();
	  String sql = "INSERT INTO STAFF (ID,NAME,ROOM,FACULTY,SCHEDULE,TELEPHONE,EMAIL) " +
				 "VALUES (NULL,'" + name + "', " + room + ", '" + faculty + "', " + schedule + ", '" + telephone + "', '" + email + "');";
	  db.execSQL(sql);
  }

  public void addSchedule(int id, String monday, String tuesday, String wednesday, String thursday, String friday)
  {
	  SQLiteDatabase db = this.getWritableDatabase();
	  String sql = "INSERT INTO SCHEDULE (ID, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY) " +
				 "VALUES (" + id + ", '" + monday + "', '" + tuesday + "', '" + wednesday +
				 "', '" + thursday + "', '" + friday + "');";
	  db.execSQL(sql);
  }

  public void addRoom (int id, String description)
  {
      SQLiteDatabase db = this.getWritableDatabase();
	  String sql = "INSERT INTO ROOM (ID, DESCRIPTION) " +
				 "VALUES (" + id + ", '" + description + "');";
	  db.execSQL(sql);
  }

  public void editStaff(int id, String name, String room, String faculty, int schedule, String telephone, String email)
  {
      SQLiteDatabase db = this.getWritableDatabase();
      if (name != null)
      {
          String sql = "UPDATE STAFF SET NAME = '" + name + "' WHERE ID = " + id +";";
          db.execSQL(sql);
      }

      if (room != null)
      {
          String sql = "UPDATE STAFF SET ROOM = " + room + " WHERE ID = " + id +";";
          db.execSQL(sql);
      }

      if (faculty != null)
      {
          String sql = "UPDATE STAFF SET FACULTY = '" + faculty + "' WHERE ID = " + id +";";
          db.execSQL(sql);
      }

      if (schedule != -1)
      {
          String sql = "UPDATE STAFF SET SCHEDULE = " + schedule + " WHERE ID = " + id +";";
          db.execSQL(sql);
      }

      if (telephone != null)
      {
          String sql = "UPDATE STAFF SET TELEPHONE = '" + telephone + "' WHERE ID = " + id +";";
          db.execSQL(sql);
      }

      if (email != null)
      {
          String sql = "UPDATE STAFF SET EMAI; = '" + email + "' WHERE ID = " + id +";";
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
		  Schedule sche = null;
		  String sql1 = "SELECT * FROM SCHEDULE WHERE ID =  '" + c.getInt(4) + "';";
		  Cursor c1 = db.rawQuery(sql1, null);
		  if( c1 != null && c1.moveToFirst() ) {
			  sche = new Schedule(c1.getString(1), c1.getString(2), c1.getString(3),
					  c1.getString(4), c1.getString(5));
			  c1.close();
		  }

		  s = new Staff(c.getString(1), c.getInt(2), c.getString(3), sche, c.getString(5), c.getString(6));

		  c.close();
	  }

	  return s;
  }

}
