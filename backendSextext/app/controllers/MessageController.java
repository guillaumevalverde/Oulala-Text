package controllers;

import static play.libs.Json.toJson;

import gcm.CopyOfGCMMessengerBis;
import gcm.GCMMessenger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import models.Message;
import models.Picture;
import models.User;
import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import Error.ShindigError;
import Util.Settings;
import Util.Strings;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class MessageController extends Controller {

    public static final String USER_ID = "userID";

	
	public static Result getMessageContent(String text_id, String userId, String password) {
		Logger.info("text: "+text_id+", userid:"+userId+ "password: "+password);
		final User user = Application.authenticateUser(userId,password);
		long id = Long.parseLong(text_id);
		if (user == null) {
			Logger.info("user null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		Logger.info("getImage " + id );

		try {
			final Message mess = Message.getMessageById(id);
			
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
									CopyOfGCMMessengerBis.sendReplyMessage(partner.registrationId, mess.idOnPhone);
							}
						}
					}, Akka.system().dispatcher());
			ObjectNode result = Json.newObject();
			result.put("text", mess.message);
			result.put("idserver", mess.id);
			return ok(toJson(result));
			
		} catch (Exception e) {
			ShindigError error = new ShindigError(e.getMessage());
			return badRequest(toJson(error));
		}
	}
	
	public static Result postMessageContent(){
		
		Logger.info("Post postImage");
		String userId=null;
		String password=null;
		String mess=null;
		String idOnPHone = null;
		
		play.mvc.Http.MultipartFormData body = request().body()
				.asMultipartFormData();

		System.out.println("body" + body);
		System.out.println("body files " + body.getFiles());
		System.out.println("body size" + body.getFiles().size());

		Map<String, String[]> bodyRequest = body.asFormUrlEncoded();
		String[] temp = bodyRequest.get("userId");
		String[] temp2 = bodyRequest.get("text");
		String[] temp3 = bodyRequest.get("idOnPhone");
		String[] pwdP = bodyRequest.get("password");
	
		Logger.info(" saveSimplePicture body" + bodyRequest.toString());
		String directoryName = "";
		if (temp != null && temp.length >=1)
			userId = temp[0];
		if (pwdP != null && pwdP.length>=1)
			password = pwdP[0];
		if (temp2 != null && temp2.length >=1)
			mess = temp2[0];
		if (temp3 != null && temp3.length >=1)
			idOnPHone = temp3[0];
		
		final User user = Application.authenticateUser(userId,password);
		
		if (user == null) {
			Logger.info("user null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		
		
	if (userId == null || password == null || mess ==null || idOnPHone==null) {
		ShindigError error = new ShindigError(Strings.ERROR_PARAM);
		return badRequest(toJson(error));
	}
	

		try {
			final Message messModel = new Message(mess, user);
			messModel.idOnPhone = idOnPHone;
			messModel.save();

			// sending the post through gcm
			Akka.system()
					.scheduler()
					.scheduleOnce(
							Duration.create(0, TimeUnit.MILLISECONDS),
							new Runnable() {
								public void run() {
									System.out.println("akka task");
									
									if (!user.partner_ID.isEmpty()) {
										System.out
												.println("sending post through gcm");
										CopyOfGCMMessengerBis.sendMessage(user, messModel);
									}
								}
							}, Akka.system().dispatcher());

			// sending back the timestamp
			ObjectNode result = Json.newObject();
			result.put("idserver", messModel.id);
			return ok(toJson(result));
			
		} catch (ShindigError e) {
			Logger.info("reeeerererr");
			return badRequest(toJson(e.getErrorMessage()));
		}
	
	}
}
