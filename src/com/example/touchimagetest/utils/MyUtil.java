package com.example.touchimagetest.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

public class MyUtil {
	public static void hideKeyboard(Context context) {
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				((Activity) context).findViewById(android.R.id.content)
						.getWindowToken(), 0);
	}
	
	/**
	 * @param context
	 * @return 获取正在运行的所有activity
	 */
	public static List<RunningTaskInfo> getActivities(Context context){
		ActivityManager actManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = actManager.getRunningTasks(100);
		return list;
	}
	/**
	 * @param context
	 * @param view
	 *            showKeyboard view
	 */
	public static void showKeyboard(Context context, View view) {
		view.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * @param context
	 * @param view
	 *            showKeyboard view
	 */
	public static boolean keyboardIsShowing(Context context) {
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return inputManager.isActive();
	}
	

	/**
	 * @param bm
	 * @param orientationDegree
	 * @return bitmap旋转
	 */
	public static Bitmap RotationBitmap(Bitmap bm, int orientationDegree,BitmapFactory.Options options) {
		
		Matrix matrix = new Matrix();
		//旋转图片 动作  
		matrix.setRotate(orientationDegree);
		// 创建新的图片  
		Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0,  
				options.outWidth, options.outHeight, matrix, false);  
		return bm1;
	}
	/**
	 * @param bm
	 * @param orientationDegree
	 * @return bitmap旋转
	 */
	public static Bitmap RotationBitmap(Bitmap bm, final int orientationDegree) {

		Matrix matrix = new Matrix();
		//旋转图片 动作  
        matrix.postRotate(orientationDegree);//setRotate 方法在小米3上闪退
        	// 创建新的图片  
        	Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0,  
        			bm.getWidth(), bm.getHeight(), matrix, false);  
        	bm.recycle();
        	return bm1;
	}
	
	/**
	 * 
	 * @param context
	 * @return 得到屏幕高度
	 */
	public static int getPhoneHeigh(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeigh = dm.heightPixels;
		return screenHeigh;
	}
	
	/**
	 * 
	 * @param context
	 * @return 得到屏幕宽度
	 */
	public static int getPhoneWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeigh = dm.widthPixels;
		return screenHeigh;
	}

	public static int getDegrees(int cameraId){
		int result = -1;
		CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        String manufacturer = android.os.Build.MANUFACTURER;
        if("Meitu".equals(manufacturer)){
        	int degrees =-1;
        	if(90 == cameraInfo.orientation){
        		degrees=270;
        	}else if(270 == cameraInfo.orientation){
        		degrees=90;
        	}
        	if(degrees!=-1){
        		result = degrees;
        	}
        }else{
        	result = cameraInfo.orientation;
        }
        return result;
	}
	
	
	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap getScreenShot(RelativeLayout waterPhoto) {
		// View是你需要截图的View
		View view = waterPhoto;
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		/*
		 * // 获取状态栏高度 Rect frame = new Rect();
		 * activity.getWindow().getDecorView(
		 * ).getWindowVisibleDisplayFrame(frame); int statusBarHeight =
		 * frame.top; System.out.println(statusBarHeight);
		 */

		/*
		 * // 获取屏幕长和高 int width =
		 * activity.getWindowManager().getDefaultDisplay().getWidth(); int
		 * height = activity.getWindowManager().getDefaultDisplay()
		 * .getHeight();
		 */

		// 获取长和高
		int width = view.getWidth();
		int height = view.getHeight();

		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
		view.destroyDrawingCache();
		return b;
	}
	
	
	/**
	 * 将bitmap写到指定路径
	 * 
	 * @param filePath
	 * @param bitmap
	 * @throws IOException
	 */
	public static void writeBitmap(String filePath, Bitmap bitmap)
			throws IOException {
		File file = new File(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);// 将图片压缩到流中
		bos.flush();// 输出
		bos.close();// 关闭
		bitmap.recycle();
		bitmap = null;
	}
}
