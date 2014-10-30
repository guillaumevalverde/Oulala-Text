package controllers;

import static play.libs.Json.toJson;
import gcm.CopyOfGCMMessengerBis;
import gcm.GCMMessenger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

import models.Picture;
import models.User;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import Error.ShindigError;
import Util.Strings;
import actions.Authenticate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Testy extends Controller {


	
	public static Result sendGcmPairingConfirm()  {
		String userId = "2adfa";
		String pairedpwd = "FDjjSicVu9OqmPjRdVHluQ=";
		Logger.info("test sendGcmPairingConfirm GCM userID: "+userId+", pairedpwd: /"+pairedpwd+"/");
		
		
		final User u = User.getUserByID(userId);
		Logger.info("u: /"+u.join_pwd+"/");
		final User user = User.getUserpartner(pairedpwd);
		if(user == null){
			Logger.info("did not found user");
			ShindigError error = new ShindigError(Strings.INCORRECTUSERNAME);
			return badRequest(toJson(error));
		}
		else
			Logger.info("found user");
		
		User usertest = User.getUserByID("test01");
		if(usertest == null){
			try {
				usertest = new User("test01","/test01/profile.jpg","pwd01");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			usertest.save();
		}
		usertest.registrationId = user.registrationId;
		usertest.save();
		ObjectNode result = Json.newObject();
		result.put("result", "200");
		result.put("paired","ok");
		usertest.partner_ID = user.userID;
		user.partner_ID = usertest.userID;
		
		user.save();
		usertest.save();
		final String url_pic  = usertest.url_pic; 
		Akka.system()
		.scheduler()
		.scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS),
				new Runnable() {
					public void run() {
						System.out.println("akka task");
						
							System.out
									.println("sending post through gcm");
							CopyOfGCMMessengerBis.test(user,url_pic);
					}
				}, Akka.system().dispatcher());
		
		result.put("result", "200");
		return ok(result);
	}
	
	
	public static Result testId()  {
		long id = 10;
		Picture pic = Picture.getPictureById(id);
		if(pic==null)
			{Logger.info("did not found user");
			ShindigError error = new ShindigError(Strings.INCORRECTUSERNAME);
			return badRequest(toJson(error));
			}
		else{
			Logger.info("adress "+pic.url);
			
		ObjectNode result = Json.newObject();
		result.put("result", "200");
		result.put("paired","ok");
		
		result.put("result", "200");
		return ok(result);
		}
	}

}
