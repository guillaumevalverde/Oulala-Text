package controllers;

import static play.libs.Json.toJson;

import gcm.CopyOfGCMMessengerBis;
import gcm.GCMMessenger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import models.Picture;
import models.User;



import Util.Strings;

import actions.Authenticate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData.FilePart;
import scala.concurrent.duration.Duration;
import security.HighLevelOfSecurity;
import security.PasswordHash;

import Error.ShindigError;
import Util.Settings;
import views.html.*;

public class Application extends Controller {

    public static final String USER_ID = "userID";

	public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
	
	
	/**
	 * Call when a user open a session
	 * @return
	 */
	
	public static Result pairWithPartner(){
		
		JsonNode json = request().body().asJson();
		Logger.info("Sign pairWithPartner "+json.toString());
		
		String join_pwd = json.findPath("join_pwd").asText();
		String userId = json.findPath("userId").asText();
		String pwd = json.findPath("password").asText();


		if (join_pwd ==  null) {
			ShindigError error = new ShindigError(Strings.ERROR_PARAM);
			return badRequest(toJson(error));
		}
		Logger.info("ask for User");
		final User user = authenticateUser(userId, pwd);//CloudMessage.getSesssionUser();
		
		if(user==null){
			Logger.info("user is null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		else{
			
			user.join_pwd=null;
			user.save();
			final User partner = User.getUserpartner(join_pwd);
			
			ObjectNode result = Json.newObject();
			
			result.put("result", "200");
			if(partner == null){
				result.put("paired", "wait");
				user.join_pwd=join_pwd;
				Logger.info("join_pwd /"+join_pwd+"/ length: "+join_pwd.length());
				
				user.save();
			}	
			else{
				result.put("paired","ok");
				partner.partner_ID = user.userID;
				user.partner_ID = partner.userID;
				result.put("partner_Id",partner.userID);
				partner.save();
				user.save();
				
				Akka.system()
				.scheduler()
				.scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS),
						new Runnable() {
							public void run() {
								System.out.println("akka task");
								
									System.out
											.println("sending post through gcm");
									GCMMessenger.sendPartnerPiaring(partner, user.userID);
							}
						}, Akka.system().dispatcher());
			}
			
			
			Logger.info("result: "+result.toString());
			return ok(result);
			}
	}
		
	
	
	/**
	 * Call when a user open a session
	 * @return
	 */
	public static Result loginUser(){
		
		JsonNode json = request().body().asJson();
		Logger.info("Sign In User "+json.toString());
		
		String userId = json.findPath("userId").asText();
		String pwd = json.findPath("password").asText();

		if (userId == null || pwd == null) {
			ShindigError error = new ShindigError(Strings.ERROR_PARAM);
			return badRequest(toJson(error));
		}

		User user = User.getUserByID(userId);
		if(user==null){
			System.out.println("User not find for this email"+ userId);
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		else{
			/**
			 	Check the Password, if good save it in the session
			**/		
		
			try {
				String pwdHash = PasswordHash.createHash(pwd.toCharArray(), user.salt);
				if(pwdHash.contentEquals(user.password)){
					session().put(USER_ID, userId);
					ObjectNode result = Json.newObject();
					result.put("message", "logged in");
					return ok(result);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			System.out.println("User not find for this ID"+ userId);
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
	}
		
	public static User authenticateUser(String userId, String pwd){
		if (userId == null || pwd == null) {
			Logger.info("authenticate params null");
			return null;
		}
		Logger.info("authenticate userId " +userId+", pwd: "+pwd);
		
		User user = User.getUserByID(userId);
		if(user==null){

			Logger.info("authenticate no user");
			return null;
		}
		else{
			/**
			 	Check the Password, if good save it in the session
			**/		
		
			try {
				String pwdHash = PasswordHash.createHash(pwd.toCharArray(), user.salt);
				Logger.info("compare pwd: pwdhash: "+pwdHash+", user.pwd: "+user.password+", salt"+user.salt);
				
				if(pwdHash.contentEquals(user.password)){
					return user;
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				
				
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			System.out.println("User not find for this ID"+ userId);

			Logger.info("authenticate not right passswrod");
			return null;
		}
		
	}
	
		/**
		 * Post to sign in a User, the user is saved,
		 * @return
		 */
		public static Result signInUser() {
			JsonNode json = request().body().asJson();
			Logger.info("Sign In User "+json.toString());
			
			String userId = json.findPath("userId").asText();
			String password = json.findPath("password").asText();

			if (userId == null || password == null) {
				ShindigError error = new ShindigError(Strings.ERROR_PARAM);
				return badRequest(toJson(error));
			}

			Logger.info("userId "+ userId);
		
			User user;
			try {
				user = User.getEventByUserId(userId);
				
				if (user==null){
					user = new User(userId, "", password);
				}
				Logger.info("has created user");
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				ShindigError error = new ShindigError(Strings.ERROR_HASHING);
				return internalServerError(toJson(error));
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
				ShindigError error = new ShindigError(Strings.ERROR_HASHING);
				return internalServerError(toJson(error));
			}
		
			user.save();
			return ok(json);
		}
		
		/**
		 * create a post of type Message to be send to the partner
		 * 
		 * @return
		 */
		public static Result saveMessage() {
			
			JsonNode json = request().body().asJson();
			System.out.println(json);
			String userId, password, message;
			if (json == null) {
				ShindigError error = new ShindigError("Invalid json");
				return badRequest(toJson(error));
				// return badRequest("Expecting Json data");
			} else {
				message = json.findPath("message").asText();
				userId = json.findPath("userId").asText();
				password = json.findPath("password").asText();
			}
			if (message.isEmpty() || userId.isEmpty() || password.isEmpty()) {
				ShindigError error = new ShindigError("Missing parameter [eventId]");
				return badRequest(toJson(error));
			}
			
			final User user = User.getUserByID(userId);
			long timestamp;
			System.out.println("STATUS message " + message);
			
			try {
				final models.Message messageM = new models.Message(message, user);
				timestamp = messageM.timestamp;
				messageM.save();

				Akka.system()
						.scheduler()
						.scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS),
								new Runnable() {
									public void run() {
										try {

											System.out.println("akka task");
											
											if (!user.partner_ID.isEmpty()) {
												System.out
														.println("sending post through gcm");
												GCMMessenger.sendMessage(user, messageM);
											}

										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										// file.delete();
									}
								}, Akka.system().dispatcher());
				} catch (ShindigError e) {
				return badRequest(toJson(e.getErrorMessage()));
			}

			ObjectNode result = Json.newObject();
			result.put("time_stamp", timestamp);
			result.put("message", "post posted");
			return ok(toJson(result));
		}
		
		/**
		 * sends the image representing a user
		 * 
		 * @param secureKey
		 * @return
		 */
		public static Result getImageUser(String name) {
			Logger.info("getImageUser : " + name);
			File file;
			try {
				file = new File(Settings.PICTURE_DIRECTORY + name);
				if (file == null || !file.exists()) {
					file = new File(Settings.PICTURE_DIRECTORY
							+ "genericAvatar.jpg");
				}
			} catch (Exception e) {
				file = new File(Settings.PICTURE_DIRECTORY + "genericAvatar.jpg");
			}
			return ok(file);
		}
		
		/**
		 * Save a new profile picture and return the url
		 * 
		 * @return
		 */
		public static Result saveProfilePicture() {

			User user = CloudMessage.getSesssionUser();
			String pic = null;
			play.mvc.Http.MultipartFormData body = request().body()
					.asMultipartFormData();

			// FilePart picture = body.getFile("picture");
			FilePart picture = null;
			for(FilePart f:body.getFiles()){
				Logger.info("fffff: "+f.toString());
				Logger.info("getfilename "+f.getFilename());
				Logger.info("getfilename "+f.getContentType());
				Logger.info("getfilename "+f.getKey());
			}
			
			if (body.getFiles().size() > 0) {
				picture = body.getFiles().get(0);
				Logger.info("picture: "+picture.toString());
			} else {
				ShindigError error = new ShindigError("Missing file");
				return badRequest(toJson(error));
				// return badRequest("Missing file");
			}
			Map<String, String[]> bodyRequest = body.asFormUrlEncoded();
			String[] temp = bodyRequest.get("caption");
			String[] orientationP = bodyRequest.get("orientation");

			Logger.info(" saveSimplePicture body" + bodyRequest.toString()+ " "+orientationP.length);

			String caption = null;
			int degree = 0;
			if (temp != null)
				caption = temp[0];
			if (orientationP != null && orientationP.length >= 1)
				degree = Integer.valueOf(orientationP[0]);

			Logger.info("orientation: "+orientationP[0]);
			Logger.info("getDegree: "+degree);
			// body.getFiles().get(arg0)
			System.out.println("fileName " + picture.getFilename());
			System.out.println("content Type" + picture.getContentType());
			String url = "error";
			// body.
			
			if (picture != null) {
				String contentType = picture.getContentType();
				String extension[] = contentType.split("/");
				
				File file = picture.getFile();
				File fileDir = new File(Settings.PICTURE_DIRECTORY + user.userID);
				for (File f : fileDir.listFiles())
					f.delete();

				user.url_pic = user.userID + "/" + UUID.randomUUID().toString() + "."
						+ extension[1];
				System.out.println("url" + user.url_pic);

				
				File dest = new File(Settings.PICTURE_DIRECTORY + user.url_pic);
				file.renameTo(dest);
				Picture.rotateBitmap(degree, dest.getAbsolutePath());

				System.out.println("destination path:" + dest.getAbsolutePath());
				System.out.println("destination name:" + dest.getName()
						+ "caption " + caption);

			}
			/*
			 * else { ShindigError error= new ShindigError("Missing file"); return
			 * badRequest(toJson(error)); }
			 */
			ObjectNode result = Json.newObject();
			result.put("url", toJson(user.url_pic));
			return ok(result);
			// return ok(toJson(pic));
		}
		
		public static Result savePicture() {
			Logger.info("Post Picture");
			String postKey = null;
			final User user = CloudMessage.getSesssionUser();
			String secureKey=null;
			int degree=0;
			
			play.mvc.Http.MultipartFormData body = request().body()
					.asMultipartFormData();

			// FilePart picture = body.getFile("picture");
			FilePart picture = null;
			System.out.println("body" + body);
			System.out.println("body files " + body.getFiles());
			System.out.println("body size" + body.getFiles().size());

			Map<String, String[]> bodyRequest = body.asFormUrlEncoded();
			String[] temp = bodyRequest.get("secureKey");
			String[] postKeyP = bodyRequest.get("postKey");
			String[] rotationP = bodyRequest.get("orientation");

			Logger.info(" saveSimplePicture body" + bodyRequest.toString());
			String directoryName = "";
			if (temp != null && temp.length >=1)
				secureKey = temp[0];
			if (postKeyP != null && postKeyP.length>=1)
				postKey = postKeyP[0];
			if (rotationP != null && rotationP.length >= 1)
				degree = Integer.valueOf(rotationP[0]);
			
			directoryName = secureKey;

			if (body.getFiles().size() > 0) {
				picture = body.getFiles().get(0);
			} else {
				ShindigError error = new ShindigError("Missing file");
				return badRequest(toJson(error));
			}

			Logger.info("directoryName " + directoryName);
			
			System.out.println("fileName " + picture.getFilename());
			System.out.println("content Type" + picture.getContentType());
			
			// body.
			if (picture != null) {
				// String fileName = picture.getFilename();
				String contentType = picture.getContentType();
				String extension[] = contentType.split("/");
				File file = picture.getFile();
				String newName = UUID.randomUUID().toString();
				File dest = new File(Settings.PICTURE_DIRECTORY + directoryName
						+ "/" + newName + "." + extension[1]);
				String finalName = newName + "." + extension[1];
				// picture

				File directory = new File(Settings.PICTURE_DIRECTORY
						+ directoryName);
				if (!directory.exists())
					directory.mkdirs();

				file.renameTo(dest);
				Picture.rotateBitmap(degree, dest.getAbsolutePath());

				System.out.println("destination pathcc:" + dest.getAbsolutePath());
				System.out.println("destination name:" + dest.getName());

				String url_pic = directoryName + "/" + finalName;
				Logger.info("dir" + directoryName);

				try {
					final Picture pictureDB = new Picture(
							url_pic, user);
					pictureDB.save();

					// sending the post through gcm
					Akka.system()
							.scheduler()
							.scheduleOnce(
									Duration.create(0, TimeUnit.MILLISECONDS),
									new Runnable() {
										public void run() {
											try {

												System.out.println("akka task");
												
												if (!user.partner_ID.isEmpty()) {
													System.out
															.println("sending post through gcm");
													GCMMessenger.sendPicture(user, pictureDB);
												}

											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											// file.delete();
										}
									}, Akka.system().dispatcher());

					// sending back the timestamp
					ObjectNode result = Json.newObject();
					result.put("url", pictureDB.url);
					result.put("time_stamp", pictureDB.timestamp);
					return ok(toJson(result));
					
				} catch (ShindigError e) {
					Logger.info("reeeerererr");
					return badRequest(toJson(e.getErrorMessage()));
				}
			}
			
			ShindigError error = new ShindigError("Missing file");
			return badRequest(toJson(error));

		}
		
		public static Result signInwithPicture() {
			Logger.info("Post signInwithPicture");
			String userId=null;
			String password=null;
			String url_pic=null;
			play.mvc.Http.MultipartFormData body = request().body()
					.asMultipartFormData();

			// FilePart picture = body.getFile("picture");
			FilePart picture = null;
			System.out.println("body" + body);
			System.out.println("body files " + body.getFiles());
			System.out.println("body size" + body.getFiles().size());

			Map<String, String[]> bodyRequest = body.asFormUrlEncoded();
			String[] temp = bodyRequest.get("userId");
			String[] pwdP = bodyRequest.get("password");
		
			Logger.info(" saveSimplePicture body" + bodyRequest.toString());
			String directoryName = "";
			if (temp != null && temp.length >=1)
			
				userId = temp[0];
			if (pwdP != null && pwdP.length>=1)
				password = pwdP[0];
			
			directoryName = "/"+userId;

			if (body.getFiles().size() > 0) {
				picture = body.getFiles().get(0);
			} else {
				ShindigError error = new ShindigError("Missing file");
				return badRequest(toJson(error));
			}

			Logger.info("directoryName " + directoryName);
			
			System.out.println("fileName " + picture.getFilename());
			System.out.println("content Type" + picture.getContentType());
			
			// body.
			if (picture != null) {
				String contentType = picture.getContentType();
				String extension[] = contentType.split("/");
				File file = picture.getFile();
				//String newName = UUID.randomUUID().toString();
				File dest = new File(Settings.PICTURE_DIRECTORY + directoryName
						+ "/profile" + "." + extension[1]);
				String finalName = "profile." + extension[1];
				// picture

				File directory = new File(Settings.PICTURE_DIRECTORY
						+ directoryName);
				if (!directory.exists())
					directory.mkdirs();

				file.renameTo(dest);
				//Picture.rotateBitmap(degree, dest.getAbsolutePath());

				System.out.println("destination pathcc:" + dest.getAbsolutePath());
				System.out.println("destination name:" + dest.getName());

				 url_pic = directoryName + "/" + finalName;
				Logger.info("dir" + directoryName);
			}	
			
			if (userId == null || password == null) {
				ShindigError error = new ShindigError(Strings.ERROR_PARAM);
				return badRequest(toJson(error));
			}

			Logger.info("userId "+ userId);
		
			User user;
			try {
				user = User.getEventByUserId(userId);
				
				if (user==null){
					user = new User(userId, url_pic, password);
				}
				Logger.info("has created user id:"+userId+", pwd:"+password);
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				ShindigError error = new ShindigError(Strings.ERROR_HASHING);
				return internalServerError(toJson(error));
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
				ShindigError error = new ShindigError(Strings.ERROR_HASHING);
				return internalServerError(toJson(error));
			}
		
			user.save();
			session().put(USER_ID, user.userID);
			ObjectNode results = Json.newObject();
			results.put("result", "200");
			return ok(toJson(results));
			
		}
		
		
		public static Result deleteContent(){
			
			JsonNode json = request().body().asJson();
			Logger.info("Sign pairWithPartner "+json.toString());
			
			String pwd = json.findPath("password").asText();
			String userId = json.findPath("userId").asText();
			final String where = json.findPath("where").asText();


			if (where ==  null || userId == null || pwd ==null) {
				ShindigError error = new ShindigError(Strings.ERROR_PARAM);
				return badRequest(toJson(error));
			}
			Logger.info("ask for User");
			final User user = authenticateUser(userId, pwd);//CloudMessage.getSesssionUser();
			
			if(user==null){
				Logger.info("user is null");
				ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
				return badRequest(toJson(error));
			}
			
			Akka.system()
			.scheduler()
			.scheduleOnce(
					Duration.create(0, TimeUnit.MILLISECONDS),
					new Runnable() {
						public void run() {
							System.out.println("akka task");
							
							if (!user.partner_ID.isEmpty()) {
								System.out
										.println("sending post through gcm!!");
								User partner = User.getUserByID(user.partner_ID);
								if(partner!=null)
									CopyOfGCMMessengerBis.sendDeleteContent(partner.registrationId, where);
							}
						}
					}, Akka.system().dispatcher());
			
			ObjectNode results = Json.newObject();
			results.put("result", "200");
			return ok(toJson(results));
		}
		
	
}
