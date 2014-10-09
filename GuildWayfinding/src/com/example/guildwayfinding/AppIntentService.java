package com.example.guildwayfinding;

import java.io.*;
import java.util.*;
import java.net.*;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class AppIntentService extends IntentService {
	private ServerSocket server;

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
	try {
		Log.i("AppIntentService", "Started");
		server = new ServerSocket(4000); //app port
		run();
	}  catch (Exception e) {
		e.printStackTrace();
	}
  }
  public void run() {
	try {
		Socket socket = server.accept();
		
		Log.i("AppIntentService", "Connection-Received");
		
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		
		ObjectOutputStream outputStream = new ObjectOutputStream(os);
		ObjectInputStream inputStream = new ObjectInputStream(is);
	
	
		DBHelper db = new DBHelper(this);
	
		String action = inputStream.readUTF();//what we receive
		String[] split = action.split(" ");
		String method = split[0];
		

		//Handle request
		if (method.equals("LIST")) {		
			Log.i("AppIntentService", "List Command Received");
			List<String> list = db.getStaffsIdsNames();
			Iterator<String> it = list.iterator();
			
			while(it.hasNext()){
				outputStream.writeUTF(it.next());
			}
			
			outputStream.writeUTF("/DONE");
			outputStream.flush();


		} else if(method.equals("DETAILS")){
			Log.i("AppIntentService", "Details Command Received id:"+split[1]);
			String id = split[1];
			Staff s = db.getStaff(Integer.parseInt(id));
			
			outputStream.writeUTF(id+","+s.getName()+","+s.getRoom()+","+s.getFaculty()+","+s.getTelephone()+","+s.getEmail());
			Log.i("AppIntentService", "sent: "+id+","+s.getName()+","+s.getRoom()+","+s.getFaculty()+","+s.getTelephone()+","+s.getEmail());
			
			outputStream.writeUTF("/DONE");
			outputStream.flush();


		} else if (method.equals("EDIT")){
		
			String[] parts = split[1].split(",");
			//db.editStaff(Integer.parseInt(parts[0]),parts[1],parts[2],parts[3],parts[4],parts[5],parts[6]);
			
		}
		run();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
  }
}