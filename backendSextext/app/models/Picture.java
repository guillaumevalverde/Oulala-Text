package models;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import Error.ShindigError;

import play.Logger;

@Entity 
@DiscriminatorValue("2")
public class Picture extends Post{

	 @Id
	 @GeneratedValue
	 public Long id;
	 
	 
	 
	 public String url;
	 
	 /**
	  * 
	  * @param name
	  * @param contentType
	  * @param url
	 * @throws ShindigError 
	  */
	 public Picture( String url, User user) throws ShindigError{
		 super(user);
		 this.url = url;
	 }
	 
	 public int getType(){
		 return Post.TYPE_IMAGE;
	 }
	 
	 public static Finder<Long, Picture> find = new Finder<Long, Picture>(Long.class,
				Picture.class);
	 
	 public static Picture getPictureById(long id) {
			Picture pic = find.where().eq("id", id).findUnique();
			return pic;
	}
	 
	 
	 public static void rotateBitmap(int deg,String path){
	
		 
			Logger.info("TRY rotateImage:"+deg+" path: "+path);
			 try {
				File file = new File(path);
				BufferedImage imgSource = null;
			    imgSource = ImageIO.read(file);
				int w = imgSource.getWidth(null), h = imgSource.getHeight(null);
				int neww,newh;
				double degree=0;
				switch(deg){
				 	case 3:
				 		degree = 180;
				 		neww = w;
				 		newh=h;
				 		break;
				 	case 6:	
				 		degree = 90;
				 		neww=h;
				 		newh=w;
				 		break;
				 	default:
				 		return;
				 }
				 
			     BufferedImage imgtransform  =new BufferedImage(neww, newh, imgSource.getType());
			     Graphics2D g = imgtransform.createGraphics();
		
			     g.translate((neww-w)/2, (newh-h)/2);
			     g.rotate(Math.toRadians(degree), w/2, h/2);
			     g.drawImage(imgSource, null, 0, 0);
			     g.dispose();
			 
			   //  BufferedImage imgtransform = rotateImage2(imgSource, degree);
			     String filename = file.getName();
			     String[] ext=filename.split("\\.");
			     
			     Logger.info("name:"+filename+ " ");
			     Logger.info(file.getName()+ " "+ext[0]);
			     ImageIO.write(imgtransform, ext[1], file);
			     Logger.info("image has been transformed");
			     
			}
			 catch (IOException e) {
			 }
	 } 
	 public static void rotateBitmapTest(int deg,String path,String pathR){
		Logger.info("TRY rotateImage:"+deg+" path: "+path);
		 try {
			File file = new File(path);
			BufferedImage imgSource = null;
		    imgSource = ImageIO.read(file);
			int w = imgSource.getWidth(null), h = imgSource.getHeight(null);
			int neww,newh;
			double degree=0;
			switch(deg){
			 	case 3:
			 		degree = 180;
			 		neww = w;
			 		newh=h;
			 		break;
			 	case 6:	
			 		degree = 90;
			 		neww=h;
			 		newh=w;
			 		break;
			 	default:
			 		return;
			 }
			 
		     BufferedImage imgtransform  =new BufferedImage(neww, newh, imgSource.getType());
		     Graphics2D g = imgtransform.createGraphics();
	
		     g.translate((neww-w)/2, (newh-h)/2);
		     g.rotate(Math.toRadians(degree), w/2, h/2);
		     g.drawImage(imgSource, null, 0, 0);
		     g.dispose();
		 
		   //  BufferedImage imgtransform = rotateImage2(imgSource, degree);
		     String filename = file.getName();
		     String[] ext=filename.split("\\.");
		     
		     Logger.info("name:"+filename+ " ");
		     Logger.info(file.getName()+ " "+ext[0]);
		     File fileR = new File(pathR);
		     ImageIO.write(imgtransform, ext[1], fileR);
		     Logger.info("image has been transformed");
		     
		}
		 catch (IOException e) {
		 }
	 }

}
