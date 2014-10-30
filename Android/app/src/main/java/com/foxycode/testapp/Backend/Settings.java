package com.foxycode.testapp.Backend;

import android.os.Environment;


public  class Settings {

	private String userName ;
	private String password ;
	
	public String getPassword() {
		return password;
	}

	public static final Boolean IsUsingHttps = false;
	public static final Boolean logs= true;
	private String imageAbsolutePath; //= "/data/data/com.orange.labs.shindig/files/profilePicture.jpg";
	private static final String REST_PORT_HTTPS="9443";
	
	/**
	 *  GUILLAUME SETUP
	 */

/*	public static final String HOST="10.0.1.51";
	private static final String REST_PORT="9000";*/
	
	/**
	 *  TERESA SETUP
	 */



	private static final String HOST="192.168.0.8";
	private static final String REST_PORT="9000";


//	private static final String HOST="10.0.1.148";
//	private static final String REST_PORT="8080";



	/**
	 * SERVER SETUP
	 */

	
//	private static final String HOST="195.157.156.86";
//	private static final String REST_PORT="8080";
	
	/**
	 * INTEGRATION SERVER
	 */
	

//	private static final String HOST="195.157.156.88";
//	private static final String REST_PORT="8080";

	
	/**
	 * HOME SERVER
	 */


		
	public Settings() {}
	
	public static String getUrl(){
		if(IsUsingHttps){
			return "https://"+HOST+":"+REST_PORT_HTTPS;
		}
		else
			return "http://"+HOST+":"+REST_PORT;
	}

	
	public void setUserName(String userName) {
		this.userName = userName;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getImageAbsolutePath() {
		return imageAbsolutePath;
	}

	public void setImageAbsolutePath(String imageAbsolutePath) {
		this.imageAbsolutePath = imageAbsolutePath;//+Utils.postKeyGenerator();
	}

	public static String getTempFilePath() {
		return Environment.getExternalStorageDirectory()+"/Pictures/Shindig/tempfile.jpg";
	}
	public static String getTempFilePath(int i) {
		return Environment.getExternalStorageDirectory()+"/Pictures/Shindig/tempfile_"+i+".jpg";
	}

}
