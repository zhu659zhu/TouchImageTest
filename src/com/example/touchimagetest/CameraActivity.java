package com.example.touchimagetest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.touchimagetest.utils.FileUtils;
import com.example.touchimagetest.utils.MyUtil;

public class CameraActivity extends Activity implements
		OnClickListener
// , AutoFocusCallback
{
    private Preview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    // The first rear facing camera
    int defaultCameraId;
    
    //新增变量   on 2016.4.11
    String MarkSrc = "";  //MainActivity传来的水印地址变量
    

	private ImageView photoImage;
	
//	private String openType;
//	private String chatType = "chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.takephoto_main);
        
        mPreview = (Preview) findViewById(R.id.sfv_camera);

        numberOfCameras = Camera.getNumberOfCameras();

        CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                int orientation = cameraInfo.orientation;
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    defaultCameraId = i;
                }
            }
        initView();
        
        String photoPath = getIntent().getStringExtra("PhotoPath");
    	if(photoPath != null){
    		MarkSrc=photoPath;
    	}
    	else{
    		MarkSrc="";
    	}
    		
        

    }
    
    public byte[] Bitmap2Bytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
         return baos.toByteArray();
     }
    
    private void initView() {
    	changgeImage = (ImageView) findViewById(R.id.iv_title_change_camera);
		changgeImage.setOnClickListener(this);
		photoImage = (ImageView) findViewById(R.id.iv_camera_take_photo);
		photoImage.setOnClickListener(this);
		flashLED = (ImageView) findViewById(R.id.iv_title_flash_led);
		flashLED.setOnClickListener(this);
		/**
		 * 旋转图标
		 */
		changgeImage.setImageBitmap(MyUtil.RotationBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rv), 270));
		photoImage.setImageBitmap(MyUtil.RotationBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 270));
		flashLED.setImageBitmap(MyUtil.RotationBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 270));
	}

	// 创建jpeg图片回调数据对象
	PictureCallback jpeg = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
				new PictureTask().execute(data);
		}

	};

	Dialog dialog;
	private ImageView changgeImage;
	private ImageView flashLED;
	class PictureTask extends AsyncTask<byte[], Integer, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ShowCustomDialog.getInstance().loadingDialog(
					CameraActivity.this, "");
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(byte[]... params) {
			// TODO Auto-generated method stub

			byte[] data = params[0];
			
			SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyyMMddhhmmss");
			String time  =    sDateFormat.format(new  java.util.Date());
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			
			
			String picPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/Pre" + time +".jpg";
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/");
			if(!file.exists()){
				file.mkdirs();
			}
			boolean b = FileUtils.writeFile(picPath, new ByteArrayInputStream(
					data));
			if (b) {
				b = false;
				FileOutputStream outputStream = null;
				try {
//					boolean front = (cameraCurrentlyLocked == Camera.CameraInfo.CAMERA_FACING_FRONT);
					
			        int degrees = MyUtil.getDegrees(cameraCurrentlyLocked);
			        if(degrees !=-1){
			        	bitmap = MyUtil.RotationBitmap(bitmap,degrees);
			        }
			        
			        if(MarkSrc!="")
			        {
			        	//自由水印
				        //FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/"+MarkSrc);
						//Bitmap frontpic  =BitmapFactory.decodeStream(fis);
						//bitmap=testSketch.OverLay(bitmap,frontpic);
			        	
			        	//水印文字
			        	//bitmap=testSketch.WordLay(bitmap,0);
			        	//现在改为了之后添加水印
			        }
			        
					outputStream = new FileOutputStream(picPath);
					b = bitmap.compress(CompressFormat.JPEG, 60, outputStream);
					outputStream.flush();
					outputStream.close();
				} catch (Exception e) {
					b = false;
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e1) {
							outputStream = null;
						}
					}
				}
				if (b) {
					return picPath;
				}
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog != null) {
				dialog.dismiss();
			}
			if (result != null) {
				
				Intent intent = new Intent(CameraActivity.this, MainActivity.class);
				intent.putExtra("PhotoPath", result);
				startActivity(intent);
				
			}
				
		}
	}
	
    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
        try
        {
	        Camera.Parameters mParameters=mCamera.getParameters();
			mParameters.setPictureSize(1600, 1200);
			mCamera.setParameters(mParameters);
        }
        catch(Exception e){Toast.makeText(CameraActivity.this, "相机分辨率调节失败...", Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_camera_take_photo:
			photoImage.setEnabled(false);
			try {
				mCamera.takePicture(new ShutterCallback() {
					public void onShutter() {
					}
				}, null, jpeg);
			} catch (Exception e) {
				
				photoImage.setEnabled(true);
			}
			break;
		case R.id.iv_title_change_camera:
		      switchCamera();
			break;
		case R.id.iv_title_flash_led:
			switchLED();
			
			Camera.Parameters mParameters=mCamera.getParameters();
			List sizeList = mParameters.getSupportedPictureSizes();
			Iterator itor1= sizeList.iterator();
			while(itor1.hasNext())
			{
				Camera.Size cur = (Camera.Size)itor1.next();
				String str = cur.height+"x"+cur.width+":";
				Toast.makeText(CameraActivity.this, str, Toast.LENGTH_SHORT).show();
			}
			
			
			break;
		default:
			break;
		}
	}
    /**
     * 切换后置摄像头闪光灯
     */
	private void switchLED() {
		// TODO Auto-generated method stub
		if(mCamera!=null){
			  Parameters parameter = mCamera.getParameters();
			  String mode = parameter.getFlashMode();
			  if(Parameters.FLASH_MODE_ON.equals(mode)){
				  parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			  }else if(Parameters.FLASH_MODE_TORCH.equals(mode)){
				  parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			  }else{
				  parameter.setFlashMode(Parameters.FLASH_MODE_ON);
			  }
              mCamera.setParameters(parameter);
		}
	}

	private void switchCamera() {
		// check for availability of multiple cameras
		if (numberOfCameras == 1) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("您的设备只有1个摄像头")
		           .setNeutralButton("Close", null);
		    AlertDialog alert = builder.create();
		    alert.show();
		    return;
		}

		// OK, we have multiple cameras.
		// Release this camera -> cameraCurrentlyLocked
		if (mCamera != null) {
		    mCamera.stopPreview();
		    mPreview.setCamera(null);
		    mCamera.release();
		    mCamera = null;
		}

		// Acquire the next camera and request Preview to reconfigure
		// parameters.
		mCamera = Camera
		        .open((cameraCurrentlyLocked + 1) % numberOfCameras);
		cameraCurrentlyLocked = (cameraCurrentlyLocked + 1)
		        % numberOfCameras;
		mPreview.switchCamera(mCamera);

		// Start the preview
		mCamera.startPreview();
	}
}
