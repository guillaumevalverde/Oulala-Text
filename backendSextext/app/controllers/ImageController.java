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

public class ImageController extends Controller {

    public static final String USER_ID = "userID";

    /**
	 * sends the image representing an event the user needs to have been invited
	 * in order to have the right in the image.
	 * 
	 * @param file
	 *            >>>>>>> shindig_rest
	 * @return
	 */
	public static Result getImage(String pic_url, String userId, String password) {
		Logger.info("picurl: "+pic_url+", userid:"+userId+ "password: "+password);
		User user = Application.authenticateUser(userId,password);
		if (user == null) {
			Logger.info("user null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		Logger.info("getImage " + pic_url );

		try {
			File filet = new File(Settings.PICTURE_DIRECTORY
					+ pic_url);
			return ok(filet);
		} catch (Exception e) {
			ShindigError error = new ShindigError(e.getMessage());
			return badRequest(toJson(error));
		}
	}
	
	public static Result getImageContent(String pic_id, String userId, String password) {
		Logger.info("picurl: "+pic_id+", userid:"+userId+ "password: "+password);
		final User user = Application.authenticateUser(userId,password);
		long id = Long.parseLong(pic_id);
		if (user == null) {
			Logger.info("user null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		Logger.info("getImage " + id );

		try {
			final Picture pic = Picture.getPictureById(id);
			File filet = new File(Settings.PICTURE_DIRECTORY
					+ pic.url);
			Logger.info("getImage " + pic.url );
			
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
												.println("sending post through gcm!!");
										User partner = User.getUserByID(user.partner_ID);
										if(partner!=null)
											CopyOfGCMMessengerBis.sendReplyMessage(partner.registrationId, pic.idOnPhone);
									}
								}
							}, Akka.system().dispatcher());


			return ok(filet);
		} catch (Exception e) {
			ShindigError error = new ShindigError(e.getMessage());
			return badRequest(toJson(error));
		}
	}
	
	public static Result postImage(){
		
		Logger.info("Post postImage");
		String userId=null;
		String password=null;
		String url_pic=null;
		String idOnPhone=null;
		
		play.mvc.Http.MultipartFormData body = request().body()
				.asMultipartFormData();

		// FilePart picture = body.getFile("picture");
		FilePart picture = null;
		System.out.println("body" + body);
		System.out.println("body files " + body.getFiles());
		System.out.println("body size" + body.getFiles().size());

		Map<String, String[]> bodyRequest = body.asFormUrlEncoded();
		String[] temp = bodyRequest.get("userId");
		String[] temp2 = bodyRequest.get("idOnPhone");
		String[] pwdP = bodyRequest.get("password");
	
		Logger.info(" saveSimplePicture body" + bodyRequest.toString());
		String directoryName = "";
		if (temp != null && temp.length >=1)
			userId = temp[0];
		if (pwdP != null && pwdP.length>=1)
			password = pwdP[0];
		if (temp2 != null && temp2.length >=1)
			idOnPhone = temp2[0];
		
		final User user = Application.authenticateUser(userId,password);
		if (user == null) {
			Logger.info("user null");
			ShindigError error = new ShindigError(Strings.ERROR_LOGIN);
			return badRequest(toJson(error));
		}
		
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
			Date date = new Date();
			//String newName = UUID.randomUUID().toString();
			File dest = new File(Settings.PICTURE_DIRECTORY + directoryName
					+ "/imgencrypt_" +date.getTime()+ "." + extension[1]);
			String finalName ="imgencrypt_" +date.getTime()+ "." + extension[1];
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

		try {
			final Picture pictureDB = new Picture(url_pic, user);
			pictureDB.idOnPhone = idOnPhone;
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
											CopyOfGCMMessengerBis.sendPicture(user, pictureDB);
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
			result.put("idserver", pictureDB.id);
			return ok(toJson(result));
			
		} catch (ShindigError e) {
			Logger.info("reeeerererr");
			return badRequest(toJson(e.getErrorMessage()));
		}
	
	}
}
