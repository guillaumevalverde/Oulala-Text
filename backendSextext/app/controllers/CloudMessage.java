package controllers;

import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import actions.Authenticate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Authenticate
public class CloudMessage extends Controller {


	public static User getSesssionUser() {
		String user_ID = session().get(Application.USER_ID);
		Logger.info("get user with User_ID: " + user_ID);
		return User.find.where().eq("userID", user_ID).findUnique();
	}
	
	public static Result registerGCM()  {
		Logger.info("register GCM");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		if (json == null) {
			return badRequest("Expecting Json data");
		}
		String registrationId = json.findPath("GCM_ID").asText();
		User user = getSesssionUser();
		user.registrationId = registrationId;
		System.out.println("admin controller -> registering userid"+registrationId);
		user.save();
		ObjectNode result = Json.newObject();
		result.put("result", "200");
		return ok(result);
	}

}
