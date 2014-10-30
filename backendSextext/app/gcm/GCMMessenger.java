package gcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

import models.Picture;
import models.User;

import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;

import Util.Settings;

public class GCMMessenger {
	public static final String KEY_ACTION = "action";
	public final static String TYPE = "type";
	public final static String TIME_STAMP = "timestamp";
	
	// GENERAL
	public final static String USERNAME = "username";
	public final static String USER = "user";

	// POSTS
	public final static String POST = "post";
	public final static String POST_TYPE = "post_type";
	public static final String POSTKEY = "postkey";
	public static final String POST_TIMESTAMP = "timestamp";
	
	// STATUS
	public static final String STATUS = "status";
	public static final String TEXT = "text";

	// PICTURE
	public static final String PICTURE = "picture";
	public static final String IMAGEURL = "image_url";
	
	// EVENT UPDATE
	public final static String EVENT_UPDATE = "event_update";
	private static final String PARTNERID = "partner_Id";
	private static final String PAIRING_OK = "pairing_ok";

		public static void sendMessage(User user, models.Message status) throws IOException {
		org.json.JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		cred = new org.json.JSONObject();
		data = new JSONObject();
		// popolating registration id's
		User partner = User.getUserByID(user.partner_ID);
		
		if (partner!=null) {
			try {
				ids.put(partner.registrationId);
				
				data.put(TYPE, POST);
				data.put(POST_TIMESTAMP, status.timestamp);
				data.put(POST_TYPE, STATUS);
				data.put(TEXT, status.message);
				data.put(USERNAME, user.userID);
				
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
				sendToGCM(cred);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public static void sendPicture(User user, Picture picture) throws IOException {
		JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		cred = new org.json.JSONObject();
		data = new JSONObject();
		// popolating registration id's
		User partner = User.getUserByID(user.partner_ID);
		
		if (partner!=null) {
			try {
				ids.put(partner.registrationId);
				data.put(TYPE, POST);
				data.put(POST_TYPE, PICTURE);
				data.put(POST_TIMESTAMP, picture.timestamp);
				data.put(IMAGEURL, picture.url);
				data.put(USERNAME, user.userID);
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	public static void sendToGCM(org.json.JSONObject message) {
		Logger.info("in sendToGCM");
		
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(Settings.GCMSERVER);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key=" + Settings.GCM_KEY);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.getOutputStream().write(message.toString().getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			conn.connect();
			int HttpResult = conn.getResponseCode();
			System.out.println("result from the server " + HttpResult);

			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
			System.out.println(">> getting response from gcm server " + result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	static public void sendPostsToUser(User user, Event event)
			throws IOException {
		org.json.JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		ids.put(user.registrationId);
		List<Post> posts = Post.getPostsByEventId(event.id);
		for (Post post : posts) {
			System.out.println("sending post >>" + post.postKey);
			cred = new org.json.JSONObject();
			data = new JSONObject();
			try {
				data.put(EVENT_SECUREKEY, post.event.secureKey);
				data.put(TYPE, POST);
				data.put(POSTKEY, post.postKey);
				data.put(POST_TIMESTAMP, post.timestamp);
				data.put(POST_EMOTION, post.emotion);
				data.put(USERNAME, post.creator.username);
				data.put(USER, post.creator.name);
				if (post.type == Post.TYPE_STATUS) {
					models.Status status = (models.Status) post;
					data.put(POST_TYPE, STATUS);
					data.put(TEXT, status.status);
				} else if (post.type == Post.TYPE_IMAGE) {
					models.Picture picture = (models.Picture) post;
					data.put(POST_TYPE, PICTURE);
					data.put(IMAGEURL, picture.url);
					data.put(CAPTION, picture.name);
				}
				else if (post.type == Post.TYPE_GIF){
					models.GifAnim gifAnim = (models.GifAnim) post;
					data.put(POST_TYPE, ANIMGIF);
					data.put(IMAGEURL, gifAnim.url);
					data.put(CAPTION, gifAnim.name);
					data.put(POST_NUMIM, gifAnim.numimage);
				}
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
*/


	public static void sendPartnerPiaring(User partner, String userID) {
		System.out.println("sending data " );
		Logger.info("sendPartnerPiaring 1");
		
		org.json.JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		Logger.info("sendPartnerPiaring 2");
		
		cred = new org.json.JSONObject();
		data = new JSONObject();
		// popolating registration id's
		Logger.info("sendPartnerPiaring 3");
		
		if (partner!=null) {
			try {
				Logger.info("sendPartnerPiaring 4");
				
				ids.put(partner.registrationId);
				data.put(KEY_ACTION, PAIRING_OK);
				data.put(PARTNERID, userID);
				
				cred.put("data", data);
				cred.put("registration_ids", partner.userID);
				System.out.println("sending data " + cred.toString());
				Logger.info("sendPartnerPiaring 5");
				
				sendToGCM(cred);
				Logger.info("sendPartnerPiaring 6");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Logger.error("error json "+e.getMessage());
				e.printStackTrace();
			}
		}
		
	}


	public static void test(User user2, String url_pic) {
		Logger.info("test ingcm : "+user2.userID+" "+url_pic);
		
		
	}

}
