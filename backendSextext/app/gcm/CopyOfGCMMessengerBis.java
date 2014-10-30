package gcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Settings;

import models.Message;
import models.Picture;
import models.User;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

public class CopyOfGCMMessengerBis {
	
	public static final String KEY_ACTION = "action";
	private static final String PARTNERID = "partner_Id";
	private static final String PAIRING_OK = "pairing_ok";
	private static final String IMAGE_ID = "id";
	// PICTURE
	public static final String PICTURE = "picture";

	public final static String POST_TYPE = "post_type";
	private static final String TEXT = "text";
	private static final String TEXT_ID = "id";
	private static final String REPLY = "reply";
	private static final String KEY_TYPE = "type_action";
	private static final String ID = "id";
	
	public static void test(User partner, String userID) {
		Logger.info("test ingcm : "+partner.registrationId+" "+userID);
		
		org.json.JSONObject cred = new org.json.JSONObject();;
		org.json.JSONObject data = new org.json.JSONObject();;
		org.json.JSONArray ids = new org.json.JSONArray();
		
		if (partner!=null) {
			try {
				data.put(KEY_ACTION, PAIRING_OK);
				data.put(PARTNERID, userID);
				ids.put( partner.registrationId);
				cred.put("data", data);
				cred.put("registration_ids",ids);;
				System.out.println("sending data " + cred.toString());
				sendToGCM(cred);
				
			} catch (JSONException e) {
				Logger.error("error json "+e.getMessage());
				
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * create the imageContent json to be send through GCM
	 * @param user
	 * @param picture
	 * @throws IOException
	 */
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
				data.put(KEY_ACTION, PICTURE);
				data.put(IMAGE_ID, picture.id);
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
			
				System.out.println("sending data " + data.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * create the messageContent json to be send through GCM
	 * @param user
	 * @param messModel
	 */
	public static void sendMessage(User user, Message messModel) {
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
				data.put(KEY_ACTION, TEXT);
				data.put(TEXT_ID, messModel.id);
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
			
				System.out.println("sending data " + data.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * create the reply message for the feedback
	 * @param id
	 * @param message
	 */
	public static void sendReplyMessage(String id,  String idContent) {
		JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		cred = new org.json.JSONObject();
		data = new JSONObject();
		// popolating registration id's
		
			try {
				ids.put(id);
				data.put(KEY_ACTION, REPLY);
				data.put(ID,idContent);
				
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
			
				System.out.println("sending data " + data.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	
	/**
	 * Send to gcm the message
	 * @param idForFeedBacl if this id is not null, we send a push to tell him the meessage to the partner has been received
	 * @param message
	 */
	public static void sendToGCM(final org.json.JSONObject message) {
		Logger.info("in sendToGCM infos: "+message.toString().getBytes());
		
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
			int intHttpResult = conn.getResponseCode();
			System.out.println("result from the server " + intHttpResult);
			
			if(intHttpResult >= 502 && intHttpResult <=599){
				System.out.println("needs to retry in 3s " + intHttpResult);
				Akka.system()
				.scheduler()
				.scheduleOnce(Duration.create(3000, TimeUnit.MILLISECONDS),
						new Runnable() {
							public void run() {
								GCMMessenger.sendToGCM(message);
							}
						}, Akka.system().dispatcher());
				
			}
			else{
				rd = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
				System.out.println(">> getting response from gcm server " + result);
			
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendDeleteContent(String registrationId, String where) {
		JSONObject cred;
		org.json.JSONObject data;
		org.json.JSONArray ids = new org.json.JSONArray();
		cred = new org.json.JSONObject();
		data = new JSONObject();
		// popolating registration id's
		
			try {
				ids.put(registrationId);
				data.put(KEY_ACTION, "delete");
				data.put("where",where);
				
				cred.put("data", data);
				cred.put("registration_ids", ids);
				System.out.println("sending data " + cred.toString());
			
				System.out.println("sending data " + data.toString());
				sendToGCM(cred);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	

	

	
	
}
