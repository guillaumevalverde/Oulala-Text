package com.foxycode.testapp.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by gve on 19/09/2014.
 */

public class ImageViewWithTriangleBis extends ImageView {

     int colorcircle = Color.BLUE;
    public final static String TAG = "ImageViewWithTriangle";


    public ImageViewWithTriangleBis(Context context) {
        super(context);
    }

    public ImageViewWithTriangleBis(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWithTriangleBis(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     *
     */
    Paint trianglePaint;
    /**
     *
     */
    Path trianglePath;



    private Path getEquilateralTriangle( int width, Direction direction) {
        Log.i("Sample", "inside getEqui");
        Point p1 = new Point();

        if(direction== Direction.EAST){
            p1.x = 50;
            p1.y = 0;
        }
        else{
            p1.x = this.getWidth()-50;
            p1.y = 0;
        }

        Point p2 = null, p3 = null;

       if (direction == Direction.EAST) {
            p2 = new Point(p1.x, p1.y + (width/2));
            p3 = new Point(p1.x - width, p1.y);
        } else if (direction == Direction.WEST) {
            p2 = new Point(p1.x, p1.y + (width/2));
            p3 = new Point(p1.x + width, p1.y );
        }

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }

    public enum Direction {
        EAST, WEST;
    }

    public void  changeDirection(Direction d){
        trianglePath = getEquilateralTriangle( 50, d);
        this.invalidate();

    }
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if(trianglePath==null)
            trianglePath = getEquilateralTriangle( 50, Direction.WEST);
        Log.i("############################", "inside ondraw");

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
       try {
           Bitmap b = ((BitmapDrawable) drawable).getBitmap();
           Bitmap cBitmap = b.copy(Config.ARGB_8888, true);
           Paint p = new Paint();
           if (cBitmap != null) {
               int clr = cBitmap.getPixel((int) 10, (int) 10);
               Log.v("pixel", Integer.toHexString(clr));
               p.setColor(clr);
           }


           p.setStyle(Paint.Style.FILL);

           canvas.drawPath(trianglePath, p);
       }catch(Exception e){
           Log.e(TAG,"error: "+e.getMessage());
       }
        /*
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
        */

    }




    public void  changeColor(int c){
        colorcircle=c;
        this.invalidate();

    }

    public  Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2 + 0.7f,
                radius / 2 + 0.7f, radius / 2 + 0.1f, paint);



        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        Paint paintC = new Paint();
        paintC.setColor(colorcircle);
        paintC.setStrokeWidth(14);
        paintC.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(radius / 2 + 0.7f,
                radius / 2 + 0.7f, radius / 2 -8+ 0.3f, paintC);

        return output;
    }

}