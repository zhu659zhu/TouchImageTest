package com.example.touchimagetest;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import com.example.touchimagetest.utils.Sketch;

public class testSketch {

	public static Bitmap testSingleGaussBlur(Bitmap src, int r, int fai) {
		int picHeight = src.getHeight();
		int picWidth = src.getWidth();

		int[] pixels = new int[picWidth * picHeight];
		src.getPixels(pixels, 0, picWidth, 0, 0, picWidth, picHeight);

		Sketch.gaussBlur(pixels, picWidth, picHeight, r, fai);
		Bitmap bitmap = Bitmap.createBitmap(pixels, picWidth, picHeight,
				Config.RGB_565);
		return bitmap;

	}

	public static Bitmap testGaussBlur(Bitmap src, int r, int fai) {

		int width = src.getWidth();
		int height = src.getHeight();
		
		int[] oldpixels=new int[src.getWidth() * src.getHeight()];
		src.getPixels(oldpixels, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
		
		int[] pixels = Sketch.discolor(src);
		int[] copixels = Sketch.simpleReverseColor(pixels);

		Sketch.simpleGaussBlur(copixels, width, height, r, fai);
		//Sketch.simpleColorDodge(pixels, copixels);

		Bitmap bitmap = Bitmap.createBitmap(pixels, width, height,
				Config.ARGB_8888);
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
        
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap  
  
                .getWidth(), bitmap.getHeight());// 获得图片的ARGB值  
  

        //number = number * 255 / 100;  
        for (int i = 0; i < argb.length; i++) {  
  
            if((oldpixels[i] & 0xff000000 >> 24)==0)
            {
            	argb[i] = oldpixels[i];
            }
            else
            {
	            //argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);  
	        	int COLOR_RANGE = 180;
	        	int red = (argb[i] & 0xff0000) >> 16;       //获取红通道的值
		        int green = (argb[i] & 0x00ff00) >> 8;     //获取红绿道的值
		        int blue = (argb[i] & 0x0000ff);                //获取蓝绿道的值
		        int tempargb = argb[i]>> 24;
		        if (red >= COLOR_RANGE && green >= COLOR_RANGE && blue >= COLOR_RANGE)
	        		argb[i] = (argb[i] & 0x00FFFFFF);  
	        	else
	        		argb[i] = argb[i] & 0xFF000000;
	        		//argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF); 
            }
        }  
  
        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap  
  
                .getHeight(), Config.ARGB_8888); 
        
		return bitmap;

	}

	public static Bitmap testDiscolor(Bitmap srcBitmap) {

		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();
		int[] pixels = Sketch.discolor(srcBitmap);
		Bitmap bitmap = Bitmap.createBitmap(pixels, width, height,
				Config.RGB_565);
		return bitmap;

	}

	public static Bitmap testReverseColor(Bitmap src) {

		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = Sketch.discolor(src);
		int[] reversels = Sketch.simpleReverseColor(pixels);
		Bitmap bitmap = Bitmap.createBitmap(reversels, width, height,
				Config.RGB_565);
		return bitmap;

	}

	public static Bitmap testColorDodge(Bitmap src) {

		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = Sketch.discolor(src);
		int[] mixels = Sketch.simpleReverseColor(pixels);
		Sketch.colorDodge(pixels, mixels);
		Bitmap bitmap = Bitmap.createBitmap(pixels, width, height,
				Config.RGB_565);
		return bitmap;

	}
	
	public static Bitmap OverLay(Bitmap bitmap1,Bitmap bitmap2) {

        // 防止出现Immutable bitmap passed to Canvas constructor错误   需要使用以下方法导入bitmap
		//BitmapFactory.decodeResource(getResources(), R.drawable.xiao).copy(Bitmap.Config.ARGB_8888, true);
		bitmap1=bitmap1.copy(Bitmap.Config.ARGB_8888, true);
		bitmap2=bitmap2.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap newBitmap = null;

        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();

//        int w = bitmap1.getWidth();
//        int h = bitmap1.getHeight();
//
//        int w_2 = bitmap2.getWidth();
//        int h_2 = bitmap2.getHeight();

        paint = new Paint();
        //canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,Math.abs(h - h_2) / 2, paint);
        canvas.drawBitmap(bitmap2, 0,0, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储新合成的图片
        //canvas.restore();

        return newBitmap;
    }

	
	public static Bitmap WordLay(Bitmap bitmap1,int loc) {

        // 防止出现Immutable bitmap passed to Canvas constructor错误
		bitmap1=bitmap1.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap newBitmap = null;
        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();
        paint = new Paint();
        paint.setColor(Color.BLUE);  
        paint.setTextSize(30);  
        if(loc==0)
        	canvas.drawText("测试水印文字".toString(), w-250, h-100, paint);
        else if(loc==1)
        	canvas.drawText("测试水印文字".toString(), 50, h-100, paint);
        else if(loc==2)
        	canvas.drawText("测试水印文字".toString(), 50, 100, paint);
        else if(loc==3)
        	canvas.drawText("测试水印文字".toString(), w-250, 100, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        return newBitmap;
    }
	
	
	
}
