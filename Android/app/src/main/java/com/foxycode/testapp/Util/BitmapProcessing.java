package com.foxycode.testapp.Util;

/**
 * Created by gve on 11/09/2014.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class BitmapProcessing {

    private static String LOG_TAG = "BitmapProcessing";

    /**
     * The function creates a bitmap subsampled (and well oriented) according to
     * the required dimensions. This method allows to load a smaller version of
     * the file into the memory.
     *
     * @param imagePath
     *            : Path of the image to process
     * @param reqWidth
     *            : Width wanted
     * @param reqHeight
     *            Height wanted
     * @return Bitmap rescaled
     */

    public static Bitmap createAdaptedBitmap(String imagePath, int orientation,
                                             int reqWidth, int reqHeight) {

        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d("SIGNUP", "ORIENTATIONNNN: 90");
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d("SIGNUP", "ORIENTATIONNNN: 180");
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d("SIGNUP", "ORIENTATIONNNN: 270");
                matrix.postRotate(-90);
                break;
            default:
                orientation=0;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;


        Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
        Log.v("BITMAP", "aget sample width: "+bitmap.getWidth()+", height: "+bitmap.getHeight() );
        if (bitmap != null) {
            if (orientation > 0)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


        } else {
            return null;
        }
        return bitmap;
    }

    public static Bitmap createAdaptedBitmap( String imagePath, int orientation) {
        Log.v("TEST","create Bitmap:  "+ imagePath);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if(bitmap==null)
        {
            Log.v("TEST","bmp null");
            return null;
        }

        Matrix matrix = new Matrix();


        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d("SIGNUP", "ORIENTATIONNNN: 90");
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d("SIGNUP", "ORIENTATIONNNN: 180");
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d("SIGNUP", "ORIENTATIONNNN: 270");
                matrix.postRotate(-90);
                break;
            default:
                orientation=0;
        }
        if (orientation > 0)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

        return bitmap;

    }
    public static Bitmap createAdaptedBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);




        bitmap= Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);

        return bitmap;
    }
    /**
     * Basic function for getting the orienation information of a particular
     * image file
     *
     * @param imagePath
     * @return
     * @throws IOException
     */
    public static int getImageOrientation(String imagePath) throws IOException {
        ExifInterface exif;
        exif = new ExifInterface(imagePath);
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
    }

    /**
     * The function computes the
     *
     * @param options
     *            : The BitmapFactory options obtained before end on the
     *            targeted bitmap
     * @param reqWidth
     *            : Width wanted
     * @param reqHeight
     *            Height wanted
     * @return Int: InSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.v("BITMAP", "before sample width: "+width+", height: "+height );

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }



    public static void saveImageOnDisk(Bitmap bitMapToShare) {
        File myDir = new File(Environment.getExternalStorageDirectory()
                + "/Saved_images");
        myDir.mkdirs();
        String fname = "Image_profile.jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitMapToShare.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveImageOnDisk(Bitmap bitMapToShare,String path) {

        File file = new File(path);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitMapToShare.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
