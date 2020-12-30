package com.km.cameraapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // put high quality images in respective folders i.e. in xxhdi or xxxhdpi .if kept in drawable only it will
        //give out of memory exception

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static Bitmap decodeSampledBitmapFrompath(Resources res, String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);

    }
    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) throws IOException {
        InputStream input=null;

        input = context.getContentResolver().openInputStream(uri);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeStream(input, null, options);


        if(input!=null){
            input.close();
        }
        input = context.getContentResolver().openInputStream(uri);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeStream(input, null, options);

        if(input!=null){
            input.close();
        }
        return bitmap;

    }
    public static Bitmap decodeSampledBitmapFromURL(Context context, String url, int reqWidth, int reqHeight) throws IOException {
        InputStream input=null;

        URL urlobject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlobject.openConnection();
        connection.setDoInput(true);
        connection.connect();
        input = connection.getInputStream();

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeStream(input, null, options);


        if(input!=null){
            input.close();
        }

        connection = (HttpURLConnection) urlobject.openConnection();
        connection.setDoInput(true);
        connection.connect();
        input = connection.getInputStream();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeStream(input, null, options);

        if(input!=null){
            input.close();
        }
        return bitmap;

    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (((halfHeight / inSampleSize) >= reqHeight) && ((halfWidth / inSampleSize) >= reqWidth)) {

                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        bitmap.recycle();
        return data;
    }

    public static Bitmap getBitmapFromView( View v){
        Bitmap capture=Bitmap.createBitmap(v.getWidth(),v.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas captureCanvas=new Canvas(capture);
        v.draw(captureCanvas);

        return capture;
    }

    //font  = R.font.myfont
    //Typeface typeface = ResourcesCompat.getFont(context,  R.font.myfont);
    public static Bitmap getBitmapForString( String text, int aFontSize, float alpha, Typeface typeface){
        if(alpha<0){
            alpha=0;
        }
        if(alpha>1){
            alpha=1;
        }
        Paint textPaint = new Paint();
        textPaint.setTextSize(aFontSize);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setARGB((int)(255*alpha), 255, 255, 255);
        // If a hinting is available on the platform you are developing, you should enable it (uncomment the line below).
        //textPaint.setHinting(Paint.HINTING_ON);
        textPaint.setSubpixelText(true);
        //textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));

        if(true) {
            textPaint.setTypeface(typeface);
        }

        float realTextWidth = textPaint.measureText(text);
        // Creates a new mutable bitmap, with 128px of width and height
        int bitmapWidth = (int)(realTextWidth + 2.0f);
        int bitmapHeight = (int)aFontSize + 2;
        //int bitmapWidth = (int)(2*realTextWidth);
        //int bitmapHeight = (int)aFontSize*2;
        Bitmap textBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        textBitmap.eraseColor(Color.argb(0, 255, 255, 255));
        // Creates a new canvas that will draw into a bitmap instead of rendering into the screen
        Canvas bitmapCanvas = new Canvas(textBitmap);
        // Set start drawing position to [1, base_line_position]
        // The base_line_position may vary from one font to another but it usually is equal to 75% of font size (height).
        bitmapCanvas.drawText(text, 1, 1.0f + aFontSize * 0.9f, textPaint);
        //bitmapCanvas.drawText(text, bitmapWidth/2-realTextWidth/2, bitmapHeight/2+(aFontSize * 0.5f*0.9f), textPaint);

        //textBitmap.recycle();
        return textBitmap;
    }
}
