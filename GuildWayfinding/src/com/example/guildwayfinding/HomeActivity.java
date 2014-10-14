package com.example.guildwayfinding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;


public class HomeActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle("Home");
        
        Intent intent = new Intent(this, AppIntentService.class);
        startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	/** Called when the user clicks the search staff button */
	public void search_staff_button_message(View view) {
		Intent intent = new Intent(this, SearchStaffActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the search faculty button */
	public void search_faculty_button_message(View view) {
		Intent intent = new Intent(this, SearchFacultyActivity.class);
		startActivity(intent);
	}
}