package com.example.faceapp;
import android.app.Application;


public class setIP extends Application {
	private String serverIP;  
	
	public String getServerIP(){
		return serverIP;
	}
	
	public void setServerIP(String serverIP){
		this.serverIP = serverIP;
	}
}
