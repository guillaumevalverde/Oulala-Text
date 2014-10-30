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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.foxycode.testapp.R;

/**
 * Created by gve on 19/09/2014.
 */

public class ImageViewWithTriangle extends ImageView {

     int colorcircle = Color.BLUE;
    Canvas mCanvas;
    Bitmap output;
    final Paint paint = new Paint();
    Path triangle = null;
    public final int wtriangle = 20;
    Direction dd = Direction.WEST;

    public ImageViewWithTriangle(Context context) {
        super(context);
    }

    public ImageViewWithTriangle(Context context, AttributeSet attrs) {

        this(context, attrs,R.attr.triangleImageViewStyle);
    }

    public ImageViewWithTriangle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ImageViewWithTriangle, defStyle, 0);
        Log.v("bb", "try set up");

        if(attributes.getBoolean(R.styleable.ImageViewWithTriangle_triangle_way, false)) {
            dd = Direction.EAST;
            Log.v("bb", "east set up");
        }
        else dd=Direction.WEST;
    }

    public enum Direction {
        EAST, WEST;
    }

    private Path getEquilateralTriangle1( int width,Direction direction) {
        Log.i("Sample", "inside getEqui");
        Point p1 = new Point();

        if(direction==Direction.WEST){
            p1.x = width;
            p1.y = 0;
        }
        else{
            p1.x = this.getWidth()-width;
            p1.y = 0;
        }

        Point p2 = null, p3 = null;

        if (direction == Direction.WEST) {
            p2 = new Point(p1.x, p1.y + width);
            p3 = new Point(0, p1.y+(width/2));
        } else if (direction == Direction.EAST) {
            p2 = new Point(p1.x, p1.y +width);
            p3 = new Point(p1.x + width, p1.y+(width/2) );
        }

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.v("triangle", "w "+w+",h "+h+"oldw "+oldw+",oldh "+oldh);
        if (w != oldw || h != oldh) {
            output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(output);
        }

    }

    private void setUPPaint(){

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));

    }

    @Override
    protected void onDraw(Canvas canvas) {

//        Log.v("triangle","ondraw");

        Drawable drawable = getDrawable();

        if (drawable == null || getWidth() == 0 || getHeight() == 0) {
            Log.v("triangle","ondraw return");
            return;
        }



    try {
        //Log.v("triangle","ondraw in try ");

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        //Log.v("triangle", "on draw bitmap w "+b.getWidth()+",h "+b.getHeight()+"this oldw "+this.getWidth()+",oldh "+this.getHeight());

        canvas.drawBitmap(getCroppedBitmap(b), 0, 0, null);
       // Log.v("triangle","ondraw fin in try ");

    }catch(Exception e){
        Log.v("triangle", "on draw super()");

        //Log.e("TAG","error: "+e.getMessage());

       // super.onDraw(canvas);
    }

      //  Log.v("triangle", " canvas w "+canvas.getWidth()+ " h: "+canvas.getHeight());

  //      Log.v("triangle", "fin on draw ");
    }

    public void  changeDirection(Direction d){
        dd=d;
        Log.v("triangle", "direction " +dd);
        this.invalidate();

    }

    public  Bitmap getCroppedBitmap(Bitmap bmp) {

        if (mCanvas==null) {
            setUPPaint();
            output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(output);

        }

     //   Log.v("triangle", "getCropped canvas w "+mCanvas.getWidth()+ " h: "+mCanvas.getHeight());



        Bitmap sbmp;

        if (bmp.getWidth() != getWidth() || bmp.getHeight() != getHeight()) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            //float factor = smallest / Math.min(getWidth(),getHeight());
            float factor = bmp.getWidth() / this.getWidth();

            sbmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth() / factor), (int)(bmp.getHeight() / factor), false);
           // sbmp = Bitmap.createScaledBitmap(bmp, this.getWidth(), this.getHeight(), false);

        } else {
            sbmp = bmp;
        }
        sbmp = bmp;
      //  Log.v("triangle", "getCropped sbmp w "+sbmp.getWidth()+ " h: "+sbmp.getHeight());


        Rect dest = new Rect(0,0, mCanvas.getWidth(), mCanvas.getHeight());

        int x,width,y,height;
        if(sbmp.getWidth()>mCanvas.getWidth()){
            x=(sbmp.getWidth()-mCanvas.getWidth())/2;
            width =mCanvas.getWidth();
        }
        else{
            x=0;
            width = sbmp.getWidth();
        }
        if(sbmp.getHeight()>mCanvas.getHeight()){
            y=(sbmp.getHeight()-mCanvas.getHeight())/2;
            height =mCanvas.getHeight();
        }
        else{
            y=0;
            height = sbmp.getHeight();
        }

        Rect src = new Rect(x,y,x+width,y+height);
       // Log.v("triangle", "rect sbmp x "+x+ " y: "+y+" width: "+width+" height:"+height);



        triangle = getEquilateralTriangle1(wtriangle,dd);
        mCanvas.drawARGB(0, 0, 0, 0);

        if(dd==Direction.WEST)
            mCanvas.drawRect(wtriangle, 0, wtriangle+mCanvas.getWidth(), mCanvas.getHeight(), paint);
        else
            mCanvas.drawRect(0, 0, -wtriangle + mCanvas.getWidth(), mCanvas.getHeight(), paint);

        paint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(triangle, paint);



        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        mCanvas.drawBitmap(sbmp, src, dest, paint);


        return output;
    }

}