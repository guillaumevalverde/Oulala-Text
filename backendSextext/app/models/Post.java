package models;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import Error.ShindigError;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_type", discriminatorType = DiscriminatorType.INTEGER)
public abstract class Post extends Model {

	public static final int TYPE_MESSAGE =1;
	public static final int TYPE_IMAGE =2;
	public static final int TYPE_GIF =3;
	@Id
	@Constraints.Min(10)
	@GeneratedValue
	public Long id;
	
	public int sendingStatut;
	
	//date of posted
	public Long timestamp;
	
	public String idOnPhone;
	

	@OneToOne
	public User creator;
	
	
	 @Column(name="_type", insertable=false, updatable=false)
	 public int type;
	
	public abstract int getType();
	
	/**
	 * 
	 * @param type
	 * @param creator
	 * @param message
	 * @param event
	 * @throws ShindigError
	 */
	public Post( User creator) throws ShindigError{
		if(creator == null  ){
			throw new ShindigError(ShindigError.ERROR_POST); 
		}
		//postDate = new Date();
		timestamp=Calendar.getInstance().getTimeInMillis();
		this.creator = creator;
	}


	public static Finder<Long, Post> find = new Finder<Long, Post>(Long.class,
			Post.class);


	/**
	 * return a list of post given the id of the event and the date from where we want to look;
	 * @param id
	 * @param dateString
	 * @return
	 */
	public static Post getPost(Long id) {
		//List<Status> pposts =  Status.find.findList();
		//Logger.info("posts "+ pposts.toString());
		Post post =  find.where().eq("postKey", id).findUnique();
		return post;
	}
	
	/**
	 * return a list of post given the id of the event and the date from where we want to look;
	 * @param id
	 * @param dateString
	 * @return
	 */
	public static List<Post> getPost(Long id, Date datestart) {
		//List<Status> pposts =  Status.find.findList();
		//Logger.info("posts "+ pposts.toString());
		Date lastDayOfMonth= new Date();
		List<Post> posts;
		if(datestart==null){
			Logger.info("case where date is null");
			posts =  find.where().eq("event_id", id).findList();
			for(Post p: posts)
				Logger.info("type of post "+ p.getType());
		}
		else	
			posts =  find.where().eq("event_id", id).between("postDate", datestart, lastDayOfMonth).findList();
		
		return posts;
	}
	
	/**
	 * return a list of post given the id of the event and the date from where we want to look;
	 * @param id
	 * @param dateString
	 * @return
	 */
	public static List<Post> getPost(Long id, long date) {
		Date lastDayOfMonth= new Date();
		Date datestart = new Date(date);
		List<Post> posts;
		posts =  find.where().eq("event_id", id).between("postDate", datestart, lastDayOfMonth).findList();
		return posts;
	}

	
	 public static List<Post> getPostsByEventId(Long eventId){
		  List<Post> posts = find.where().eq("event_id", eventId).findList();
		  //find.where().eq("user_id", userId).
		  return posts;
	  }
	 


}
