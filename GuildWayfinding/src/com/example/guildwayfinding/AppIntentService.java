package com.example.guildwayfinding;

import java.io.*;
import java.util.*;
import java.net.*;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class AppIntentService extends IntentService {

  /**
   * A constructor is required, and must call the super IntentService(String)
   * constructor with a name for the worker thread.
   */
  public AppIntentService() {
      super("AppIntentService");
  }

  /**
   * The IntentService calls this method from the default worker thread with
   * the intent that started the service. When this method returns, IntentService
   * stops the service, as appropriate.
   */
  @Override
  protected void onHandleIntent(Intent intent) {
		Log.i("AppIntentService", "Started");
		start();
  }
  
  public void start() {
	try {
		//Socket socket = server.accept();
		Socket socket = new Socket("10.20.158.160",8000);
		
		
		PrintWriter outputStream = new PrintWriter(socket.getOutputStream());
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		Log.i("AppIntentService", "Connected");
	
		
		while(true){
			String action = inputStream.readLine();
			
			DBHelper db = new DBHelper(this);
			
			Log.i("AppIntentService", action);
			
			String[] split = action.split("---");
			String method = split[0];
			
	
			//Handle request
			if (method.equals("LIST")) {		
				Log.i("AppIntentService", "List Command Received");
				List<String> list = db.getStaffsIdsNames();
				Iterator<String> it = list.iterator();
				
				while(it.hasNext()){
					String next = it.next();
					outputStream.println(next);
					//Log.i("AppIntentService", next);
				}
				
				outputStream.println("/DONE");
				outputStream.flush();
				
				if (outputStream.checkError())
					Log.i("AppIntentService", "Error");
	
			} else if(method.equals("DETAILS")){
				Log.i("AppIntentService", "Details Command Received");
				String id = split[1];
				Staff s = db.getStaff(Integer.parseInt(id));
				
				outputStream.println(id+"&"+s.getName()+"&"+s.getRoom()+"&"+s.getFaculty()+"&"+s.getTelephone()+"&"+s.getEmail()+"&"+s.mon+"&"+s.tues+"&"+s.wed+"&"+s.thurs+"&"+s.fri);
				outputStream.flush();
	
	
			} else if (method.equals("EDIT")){
				Log.i("AppIntentService", "Edit Command Received");
				String[] parts = split[1].split("&", -1);
				db.editStaff(Integer.parseInt(parts[0]),parts[1],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8],parts[9],parts[10]);
				
			} else if (method.equals("ADD")){
				
				Log.i("AppIntentService", "Add Command Received");
				String[] parts = split[1].split("&", -1);
				db.addStaff(parts[0],Integer.parseInt(parts[1]),parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8],parts[9]);
			} else if (method.equals("DELETE")){
					
				Log.i("AppIntentService", "delete Command Received");
				db.deleteStaff(Integer.parseInt(split[1]));
			}
			db.close();
		}
	} catch (Exception e) {
		e.printStackTrace();
		Log.i("AppIntentService", "Error connecting to Server, Retrying in 10secs");
		
		//retry after 10 secs
		new Timer().schedule(new TimerTask() {          
		    @Override
		    public void run() {
		        start();      
		    }
		}, 10000);
	}
  }
}