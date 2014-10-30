package com.foxycode.testapp.Util;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxycode.testapp.R;

/**
 * Created by gve on 19/09/2014.
 */

public class TextViewWithTriangle extends TextView {

     int colorcircle = getResources().getColor(R.color.cyan);//Color.BLUE;
    Direction dd;

    public TextViewWithTriangle(Context context) {
        super(context);

        trianglePaint = new Paint();
    }

    public TextViewWithTriangle(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.triangleTextViewStyle);

        trianglePaint = new Paint();
    }

    public TextViewWithTriangle(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithTriangle, defStyle, 0);
        Log.v("bb", "try set up");

        if(attributes.getBoolean(R.styleable.TextViewWithTriangle_isLeft, true)) {
            dd = Direction.WEST;
            Log.v("bb", "east set up");
        }
        else dd = Direction.EAST;


        trianglePaint = new Paint();
    }

    public void setDirection(boolean isLeft) {

        dd=(isLeft? Direction.WEST:Direction.EAST);
        this.invalidate();
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
        //Log.i("Sample", "inside getEqui");
        Point p1 = new Point();
        trianglePaint.setColor(colorcircle);

        if(direction== Direction.WEST){
            p1.x = 50;
            p1.y = 20;
        }
        else{
            p1.x = this.getWidth()-100;
            p1.y = 20;
        }

        Point p2 = null, p3 = null;

       if (direction == Direction.WEST) {
            p2 = new Point(p1.x, p1.y + (width/2));
            p3 = new Point(p1.x - width, p1.y);
        } else if (direction == Direction.EAST) {
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
        //Log.i("############################", "change path "+d);
        trianglePath = getEquilateralTriangle( 50, d);
        //if(d==Direction.EAST)
        //    trianglePaint.setColor(getResources().getColor(R.color.cyan));
        //this.setBackgroundColor(getResources().getColor(R.color.cyan));
        this.invalidate();

    }
    @Override
    protected void onDraw(Canvas canvas) {

        RectF rect = new RectF(50,0,this.getWidth()-100,this.getHeight());
        canvas.drawRoundRect(rect, 15, 15, trianglePaint);
        super.onDraw(canvas);

        //if(trianglePath==null)
            trianglePath = getEquilateralTriangle( 50, dd);
       // Log.i("############################", "inside ondraw");




        trianglePaint.setStyle(Paint.Style.FILL);

        canvas.drawPath(trianglePath, trianglePaint);

     }




    public void  changeColor(int c){
        colorcircle=c;
        this.invalidate();

    }



}