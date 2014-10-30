package models;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import Error.ShindigError;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@DiscriminatorValue("1")
public class Message extends Post {
	 @Id
	 @Constraints.Min(10)
	 @GeneratedValue
	 public Long id;
	 
	 @Constraints.Required
	 public String message;
	 
	 
	 
	 public Message(String status, User user) throws ShindigError{
		 super(user);
		 this.message = status;
	 }
	 
	 public static Finder<Long, Message> find = new Finder<Long, Message>(Long.class,
			 Message.class);
	 
	 public static Message getMessageById(long id) {
			Message mess = find.where().eq("id", id).findUnique();
			return mess;
	}


	@Override
	public int getType() {
		return Post.TYPE_MESSAGE;
	}

}
