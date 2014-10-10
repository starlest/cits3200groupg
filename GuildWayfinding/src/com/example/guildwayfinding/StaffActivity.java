package com.example.guildwayfinding;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class StaffActivity extends Activity {

	List<String> list = new ArrayList<String>();
	RelativeLayout.LayoutParams lp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Back To Home");
        
		Intent intent = getIntent();
		int staffId = intent.getIntExtra(SearchStaffActivity.STAFF_MESSAGE, -1);
		
		DBHelper d = new DBHelper(this);
		
		if (staffId == -2) {	
			String faculty = intent.getStringExtra(SearchFacultyActivity.FACULTY_MESSAGE);
			
			setContentView(R.layout.activity_staff);
			
			TextView nameView = (TextView) findViewById(R.id.name);
		    nameView.setText(faculty);
			 
			TextView info = (TextView) findViewById(R.id.info);
			info.setText("\nFaculty: " + " ");
			info.append("\nTelephone: " + " ");
			info.append("\nEmail: " + " ");
			info.append("\nSchedule:");
			
			info.append("\nMon - \t " + " "
			+ "\nTue - \t " + " "
			+ "\nWed - \t " + " "
			+ "\nThu - \t " + " "
			+ "\nFri - \t " + " ");
			
			String fileName = "android.resource://"+  getPackageName() + "/raw/vp8";
			VideoView vv = (VideoView) findViewById(R.id.video);
			vv.setVideoURI(Uri.parse(fileName));
			vv.start();
			vv.setOnPreparedListener(new OnPreparedListener() {
			    @Override
			    public void onPrepared(MediaPlayer mp) {
			        mp.setLooping(true);
			    }
			});
		}
		
		else {
			d.getReadableDatabase();
			Staff s = d.getStaff(staffId);
			d.close();
		
			setContentView(R.layout.activity_staff);
		
			TextView nameView = (TextView) findViewById(R.id.name);
			nameView.setText(s.getName());
		 
			TextView info = (TextView) findViewById(R.id.info);
			info.setText("\nFaculty: " + s.getFaculty());
			info.append("\nTelephone: " + s.getTelephone());
			info.append("\nEmail: " + s.getEmail());
			info.append("\nSchedule:");
		
			info.append("\nMon - \t " + s.getMon()
					+ "\nTue - \t " + s.getTues()
					+ "\nWed - \t " + s.getWed()
					+ "\nThu - \t " + s.getThurs()
					+ "\nFri - \t " + s.getFri());
		
			String fileName = "android.resource://"+  getPackageName() + "/raw/vp8";
			VideoView vv = (VideoView) findViewById(R.id.video);
			vv.setVideoURI(Uri.parse(fileName));
			vv.start();
			vv.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.setLooping(true);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.staff, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
