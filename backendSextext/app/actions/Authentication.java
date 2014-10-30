package actions;

import models.User;
import play.Logger;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import controllers.Application;

/**
 * @author Guillaume Valverde
 */
public class Authentication extends Action<Authenticate>
{
    @Override
    public Promise<SimpleResult> call(Http.Context context) throws Throwable
    {
        Logger.info("Authentication process "+context.session().toString());
        String userId = context.session().get(Application.USER_ID);
        Logger.info("userId "+userId);
        if(userId == null ||  (User.getUserByID(userId)) == null){
			Logger.info("User needs to be logged");
			System.out.println("User needs to log");
			return F.Promise.pure((SimpleResult) unauthorized("unauthorized"));
		}
		else
			return delegate.call(context);
    }
}