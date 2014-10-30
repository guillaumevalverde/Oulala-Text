package models;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.SecretKey;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Configuration;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import security.HighLevelOfSecurity;
import security.PasswordHash;


@Entity
public class User extends Model {

	 @Id
	 @Constraints.Min(10)
	 @GeneratedValue
	 public Long id;
	
	 @Constraints.Required
	 public String userID;
	
	 @JsonIgnore
	 public String password;
	 

	 @JsonIgnore
	 public String join_pwd;
	 
	 public String url_pic;
	  
	 public String partner_ID;
		
	 /**
	  * it is used to salt the password when saved in the database
	  */

	 @JsonIgnore
	 public String salt;
	 
	 @JsonIgnore
	 public String registrationId;
	 
	 public Long getId(){
		 return id;
	 }
	 
	 
	 @Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			User other = (User) obj;
			if (userID == null) {
				if (other.userID != null)
					return false;
			} else if (!userID.equals(other.userID) )
				return false;
			return true;
		}
	
	 /**
	  * The password is salted and saved as a hash
	  * @param username
	  * @param name
	  * @param email
	  * @param phoneNumber
	  * @param url
	  * @param password
	  * @throws NoSuchAlgorithmException
	  * @throws InvalidKeySpecException
	  */
	public User(String username, String url,String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
		super();
		Logger.info(username);
		Logger.info(url);
		this.userID = username;
		this.url_pic= url;
		if(this.salt==null)
			this.salt = PasswordHash.getSalt();
		
		if(pwd!=null){
	    	this.password = PasswordHash.createHash(pwd.toCharArray(), this.salt);
		}

		Logger.info("create user" +this.password+ "salt"+ this.salt);
		
	}
	
	
	  public static Finder<Long,User> find = new Finder<Long,User>(
	    Long.class, User.class
	  ); 
	
	  public static User getEventByUserId(String userName){
		  User user = find.where().eq("userID", userName).findUnique();
		  return user;
	  }
	  
	  public static User getUserByUserName(String userName){
		  User user = find.where().eq("username", userName).findUnique();
		  return user;
	  }
	  public static User getUserByID(String userID){
		  User user = find.where().eq("userID", userID).findUnique();
		  return user;
	  }

	  public static User getUserpartner(String join_pwd){
		  User user = find.where().eq("join_pwd", join_pwd).findUnique();
		  return user;
	  }
	

	private static Configuration getConfiguration() {
		return play.Play.application().configuration().getConfig("Ip.conf");
	}


	public void setPassword(String password2) throws NoSuchAlgorithmException, InvalidKeySpecException {
	    this.password = PasswordHash.createHash(password2.toCharArray(), salt);
	}


/*
	@Override
	public void delete() {
		// TODO Auto-generated method stub
		System.out.println("deleting user "+this.name);
		System.out.println("deleting user " +this.id);
		List<Event> events = Event.getEventByOrganizerId(this.id);
		if (events != null) {
			for (Event event : events) {
				//event.delete();
				System.out.println(">> deleting event "+ event.id);
				System.out.println(">> deleting event "+ event.name);
				event.delete();
				//Ebean.delete(event);
			}
		}
		super.delete();
	}

*/	

}
