package com.example.guildwayfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class StaffActivity extends Activity {

	List<String> list = new ArrayList<String>();
	RelativeLayout.LayoutParams lp;
	private TimerTask t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Back To Home");
        
		Intent intent = getIntent();
		int staffId = intent.getIntExtra(SearchStaffActivity.STAFF_MESSAGE, -1);
		
		DBHelper d = new DBHelper(this);
		d.getReadableDatabase();
		
		if (staffId == -2) {	
			String faculty = intent.getStringExtra(SearchFacultyActivity.FACULTY_MESSAGE);
			
			setContentView(R.layout.activity_staff);
			
			TextView nameView = (TextView) findViewById(R.id.name);
		    nameView.setText(faculty);
			 
			TextView info = (TextView) findViewById(R.id.info);
			info.setText("\n\n\nArea: " + " ");
			info.append("\n\n\nRoom: " + d.getFacultyDirection(faculty));
			info.append("\n\n\nTelephone: " + " ");
			info.append("\n\n\nEmail: " + " ");
			info.append("\n\n\nSchedule:\n");
			
			String fileName = "android.resource://"+  getPackageName() + "/raw/a" + d.getFacultyDirection(faculty).toLowerCase();
			VideoView vv = (VideoView) findViewById(R.id.video);
			vv.setVideoURI(Uri.parse(fileName));
			vv.start();
			vv.setOnPreparedListener(new OnPreparedListener() {
			    @Override
			    public void onPrepared(MediaPlayer mp) {
			        mp.setLooping(true);
			    }
			});
			
			TextView floor = (TextView) findViewById(R.id.floor);
			String f = d.getFacultyDirection(faculty);
			if (f.substring(0, 1).equals("G")) f = "Ground Floor";
			else if (f.substring(0, 1).equals("1")) f = "First Floor";
			else f = "Second Floor";
			floor.setTextColor(Color.RED);
			floor.setText(f);
		}
		
		else {
			Staff s = d.getStaff(staffId);
		
			setContentView(R.layout.activity_staff);
		
			TextView nameView = (TextView) findViewById(R.id.name);
			nameView.setText(s.getName());
		 
			TextView info = (TextView) findViewById(R.id.info);
			info.setText("\n\n\nArea: " + s.getFaculty());
			info.append("\n\n\nRoom: " + s.getRoom());
			info.append("\n\n\nTelephone: " + s.getTelephone());
			info.append("\n\n\nEmail: " + s.getEmail());
			info.append("\n\n\nSchedule:");
			
			populateSchedule(s);
			
			String fileName = "android.resource://"+  getPackageName() + "/raw/a" + s.getRoom().toLowerCase();
			VideoView vv = (VideoView) findViewById(R.id.video);
			vv.setVideoURI(Uri.parse(fileName));
			vv.start();
			vv.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.setLooping(true);
				}
			});
			
			TextView floor = (TextView) findViewById(R.id.floor);
			String f = s.getRoom();
			if (f.substring(0, 1).equals("G")) f = "Ground Floor";
			else if (f.substring(0, 1).equals("1")) f = "First Floor";
			else f = "Second Floor";
			floor.setTextColor(Color.RED);
			floor.setText(f);
		}
		

		t = new TimerTask(){
		    public void run() { 
		    	Intent intent = new Intent(getBaseContext(), HomeActivity.class);
		        startActivity(intent);
		    }
		};
		
		new Timer().schedule(t, 45000);
		d.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.staff, menu);
		return true;
	}
	
	private void populateSchedule(Staff s) {
		int[] mon = s.getMon();
		for (int i = 0; i < mon.length; i++) {
			if (mon[i] == 1) {
				String time = "mon_" + (800 + i * 100);
				int ID = getResources().getIdentifier(time,
					    "id", getPackageName());
				TextView v = (TextView) findViewById(ID);
				v.setBackgroundColor(Color.GREEN);
			}
		}
		
		int[] tues = s.getTues();
		for (int i = 0; i < tues.length; i++) {
			if (tues[i] == 1) {
				String time = "tue_" + (800 + i * 100);
				int ID = getResources().getIdentifier(time,
					    "id", getPackageName());
				TextView v = (TextView) findViewById(ID);
				v.setBackgroundColor(Color.GREEN);
			}
		}
		
		int[] wed = s.getWed();
		for (int i = 0; i < wed.length; i++) {
			if (wed[i] == 1) {
				String time = "wed_" + (800 + i * 100);
				int ID = getResources().getIdentifier(time,
					    "id", getPackageName());
				TextView v = (TextView) findViewById(ID);
				v.setBackgroundColor(Color.GREEN);
			}
		}
		
		int[] thurs = s.getThurs();
		for (int i = 0; i < thurs.length; i++) {
			if (thurs[i] == 1) {
				String time = "thu_" + (800 + i * 100);
				int ID = getResources().getIdentifier(time,
					    "id", getPackageName());
				TextView v = (TextView) findViewById(ID);
				v.setBackgroundColor(Color.GREEN);
			}
		}
		
		int[] fri = s.getFri();
		for (int i = 0; i < fri.length; i++) {
			if (fri[i] == 1) {
				String time = "fri_" + (800 + i * 100);
				int ID = getResources().getIdentifier(time,
					    "id", getPackageName());
				TextView v = (TextView) findViewById(ID);
				v.setBackgroundColor(Color.GREEN);
			}
		}
	}
	
    @Override
    protected void onPause() {
    	this.onStop();
    }
    
    @Override
    protected void onStop() {
    	t.cancel();
    	super.onDestroy();
    }
}
