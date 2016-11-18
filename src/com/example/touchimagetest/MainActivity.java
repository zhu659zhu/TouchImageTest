package com.example.touchimagetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touchimagetest.WaterImageView.OnDeleteClick;
import com.example.touchimagetest.logic.ImgFileListActivity;
import com.example.touchimagetest.utils.MyUtil;

public class MainActivity extends Activity  {

//自定义变量
private Button openImageBn;           //打开图片
private Bitmap bmp;                         //原始图片
private TextView pathText;                //路径TextView
private String path;                           //存储图片路径
private ImageView imageShow;         //显示图片
private final int IMAGE_CODE = 0;   //打开图片
//触屏缩放图片
private static final int NONE = 0;     //初始状态
private static final int DRAG = 1;      //拖动
private static final int ZOOM = 2;     //缩放
private int mode = NONE;                 //当前事件
private float oldDist;
private PointF startPoint = new PointF();
private PointF middlePoint = new PointF();
private Matrix matrix = new Matrix();
private Matrix savedMatrix = new Matrix();

//新增按钮
private Button wordAddBn;              //添加文字
private Button changeImageBn;       //缩放图片
private Button drawImageBn;           //绘制图片
//图片处理时显示备份
private Bitmap alteredBitmap;          //图片
private Canvas canvas;                    //画布
private Paint paint;                          //画刷
private RelativeLayout layout;
//标识变量  1-显示图片 2-添加文字 3-缩放图片 4-画图
private int flagOnTouch = 0;         

//新增的变量******************** on 2016.3.30
String SavePath = Environment.getExternalStorageDirectory()+"/aPic/";

private int mPosition = 0;  //添加的元素计数
private RelativeLayout waterLayout;
private EditText FrontText;//水印文字编辑框
private int FrontTextSize=100;//水印大小
private int FrontTextAlpha=125;//水印透明度(0-255)
private String FrontTextFont="宋体";
private int FrontTextColor = Color.BLUE;
private String photoPath ="";  //相机加载的图片地址

private int TempletNum = 0;

private ProgressDialog mDialog; //水印图像处理中...
private Bitmap mSourceBitmap;
private Bitmap waterBitmap;
private Bitmap mConvertedBitmap;
private int PicPara=10;//滤镜处理参数   10效果较好
private int PicSrc= R.drawable.m;

ArrayList<String> listfile=new ArrayList<String>();

ProgressDialog proDia;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    openImageBn = (Button) findViewById(R.id.button1);
    //pathText = (TextView) findViewById(R.id.textView1);
    imageShow = (ImageView) findViewById(R.id.imageView1);
    FrontText = (EditText) findViewById(R.id.editText2);
    
    waterLayout = (RelativeLayout)findViewById(R.id.Content_Layout);

//    Bitmap b1= BitmapFactory.decodeResource(getResources(), R.drawable.t1).copy(Bitmap.Config.ARGB_8888, true);
//    Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.t2).copy(Bitmap.Config.ARGB_8888, true);
//    imageShow.setImageBitmap(testSketch.OverLay(b1, b2));
    


	Bundle bundle= getIntent().getExtras();
    if (bundle!=null) {
		if (bundle.getStringArrayList("files")!=null) {
			listfile= bundle.getStringArrayList("files");
					CreateLoading();
					new Thread(new Runnable() {
						@Override
						public void run() {
							for(int i = 0;i<listfile.size();i++)
							{
								try
								{
									String MoreMarkSrc = getIntent().getStringExtra("MoreMarkSrc");
									FileInputStream fis = new FileInputStream(listfile.get(i));
									Log.i("*********1",listfile.get(i));
									Bitmap bitmap =BitmapFactory.decodeStream(fis).copy(Bitmap.Config.ARGB_8888, true);
									
									//Bitmap bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.a);
									Log.i("*********2",Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/"+MoreMarkSrc);
									FileInputStream markfis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/"+MoreMarkSrc);
									Bitmap frontpic = BitmapFactory.decodeStream(markfis).copy(Bitmap.Config.ARGB_8888, true);
									//Bitmap frontpic=BitmapFactory.decodeResource(getResources(), R.drawable.u);
									bitmap=testSketch.OverLay(bitmap,frontpic);
									Log.i("*********5",listfile.get(i));
									String strFileName = listfile.get(i).substring(listfile.get(i).lastIndexOf("/"),listfile.get(i).length());
									Log.i("*********6",strFileName);
									File destDir = new File(SavePath);
						            if (!destDir.exists())
						            {
						                destDir.mkdirs();
						            }
						            File imageFile = new File(SavePath+strFileName);
						            //imageFile.createNewFile();
						            FileOutputStream fos = new FileOutputStream(imageFile);
						            bitmap.compress(CompressFormat.PNG, 50, fos);
						            fos.flush();
						            fos.close();
						            proDia.dismiss();//隐藏对话框
						            
								}
								catch(Exception e){}
							}
						}
					}).start();
					
		            
				
		}
	}
    
    
    photoPath = getIntent().getStringExtra("PhotoPath");
	if(photoPath != null){
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		imageShow.setImageBitmap(bitmap);
		flagOnTouch = 3;    
		TimeMark();
		//imageShow.setOnClickListener(this);
	}
	
	
    
    //打开图片
    openImageBn.setOnClickListener(new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		Intent intent = new Intent(Intent.ACTION_PICK, 
    				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    		startActivityForResult(intent, IMAGE_CODE);
    	}
    });
    
	//触屏缩放图片监听 注:XML中修改android:scaleType="matrix"
	imageShow.setOnTouchListener(new OnTouchListener() {
		
		//设置两个点 按下坐标(downx, downy)和抬起坐标(upx, upy)
		float downx = 0;
		float downy = 0;
		float upx = 0;
		float upy = 0;
		//触摸事件
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView view = (ImageView) v;
			
			//图片缩放
			if(flagOnTouch == 3)  {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: //手指按下
					savedMatrix.set(matrix);
					startPoint.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event); //如果两点距离大于10 多点模式
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(middlePoint, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) { //拖动
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
					} else if (mode == ZOOM) { //缩放
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
						}
					}
					break;
				} //end switch
				view.setImageMatrix(matrix);
				return true;
			}
			//显示图片
			if(flagOnTouch == 1) {
				return true;
			}
			//图片文字添加
			else if(flagOnTouch == 2) {
				return true;
	    	}
			//绘制图像
			else if(flagOnTouch == 4) {					
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downx = event.getX();
						downy = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						upx = event.getX();
						upy = event.getY();
						canvas.drawLine(downx, downy, upx, upy, paint);
						imageShow.invalidate();
						downx = upx;
						downy = upy;
						break;
					case MotionEvent.ACTION_UP:
						upx = event.getX();
						upy = event.getY();
						canvas.drawLine(downx, downy, upx, upy, paint);
						imageShow.invalidate();
						break;
					case MotionEvent.ACTION_CANCEL:
						break;
					default:
						break;
					}
					return true;
			}
	    	else {
	    		return false;
	    	}
		}  //end  onTouch
		//两点距离
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
		//两点中点
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
	});

	//缩放图片 点击按钮"缩放"
	changeImageBn = (Button) findViewById(R.id.button3);
	changeImageBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("选择一个要添加的印章");
            //    指定下拉列表的显示数据
            final String[] cities = {"测试1", "测试2", "测试3", "测试4", "测试5"};
            //    设置一个下拉的列表选择项
            builder.setItems(cities, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                	
                	switch (which)
                	{
	                	case 0:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.a);
	                		break;
	                	case 1:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.h);
	                		break;
	                	case 2:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.k);
	                		break;
	                	case 3:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.m);
	                		break;
	                	case 4:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.u);
            				break;
	                	default:mSourceBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.m);
	                		break;
                	}
                	
                	
                	new ConvertTask().execute(new Integer[] { 3, PicPara });
                	Toast.makeText(MainActivity.this, "选择的为：" + cities[which], Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
			
            //mSourceBitmap=BitmapFactory.decodeResource(getResources(), PicSrc);
			//new ConvertTask().execute(new Integer[] { 3, PicPara });
			
//			if(flagOnTouch == 3)
//			{
//				flagOnTouch = 1;
//			}
//			else
//			{
//				flagOnTouch = 3;
//			}
		}
	});
	
	//绘制画图 点击按钮"绘制"
	drawImageBn = (Button) findViewById(R.id.button4);
	drawImageBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				flagOnTouch = 4;
				//画图 图片移动至(0,0) 否则绘图线与手指存在误差
				matrix = new Matrix();
				matrix.postTranslate(0, 0);
				imageShow.setImageMatrix(matrix);
				imageShow.setImageBitmap(alteredBitmap); 
		        canvas.drawBitmap(bmp, matrix, paint);  
			}
			catch(Exception e)
			{
				Toast.makeText(MainActivity.this, "请先选择图片~", Toast.LENGTH_SHORT).show();
			}
		}
	 });

	//添加水印文字 
	wordAddBn = (Button) findViewById(R.id.button2);
	layout = (RelativeLayout) findViewById(R.id.Content_Layout);
	wordAddBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				if( FrontText.getText().toString().length()==0)
				{
					FrontText.setText("空文字...");
				}
				
				flagOnTouch = 3;
				//添加文字
				Bitmap bmpTemp = Bitmap.createBitmap(FrontText.getText().toString().length()*FrontTextSize+10, FrontTextSize+10, Config.ARGB_8888);  
		        Canvas cv = new Canvas(bmpTemp);  
		        Paint p = new Paint();  
		        Typeface font = Typeface.create(FrontTextFont, Typeface.BOLD);  
		        p.setColor(FrontTextColor);  
		        p.setAlpha(FrontTextAlpha);
		        p.setTypeface(font);
		        p.setTextSize(FrontTextSize);  
		        cv.drawText(FrontText.getText().toString(), 0, 100, p);  
		        cv.save(Canvas.ALL_SAVE_FLAG);  

				mPosition = mPosition + 1;
				WaterImageView imageView = new WaterImageView(MainActivity.this, 
						bmpTemp, ondeleteClick);
				imageView.setmPosition(mPosition);
				imageView.setId(mPosition);
				imageView.setIsClick(1);
				
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				param.setMargins(10, 0, 0, 10);
				imageView.setLayoutParams(param);
				
				waterLayout.addView(imageView, param);
				
				cv.restore();
			}
			catch(Exception e)
			{
				Toast.makeText(MainActivity.this, "请先选择图片~", Toast.LENGTH_SHORT).show();
			}
		}
	});

    
    if (savedInstanceState == null) {
        getFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();
    }
    
    
    
    
}

	public void MultiPicMarks(View v)
	{
		//添加印章
		//mSourceBitmap= testSketch.OverLay(BitmapFactory.decodeResource(getResources(), R.drawable.m),BitmapFactory.decodeResource(getResources(), R.drawable.h));
		//mSourceBitmap= testSketch.OverLay(mSourceBitmap, BitmapFactory.decodeResource(getResources(), R.drawable.k));
		//new ConvertTask().execute(new Integer[] { 3, PicPara });
		
		//添加图片
		waterBitmap= testSketch.OverLay(BitmapFactory.decodeResource(getResources(), R.drawable.m),BitmapFactory.decodeResource(getResources(), R.drawable.h));
		waterBitmap=testSketch.OverLay(waterBitmap, BitmapFactory.decodeResource(getResources(), R.drawable.k));
		
		mPosition = mPosition + 1;
		WaterImageView imageView = new WaterImageView(MainActivity.this, 
				waterBitmap, ondeleteClick);
		imageView.setmPosition(mPosition);

		imageView.setId(mPosition);
		imageView.setIsClick(1);
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		param.setMargins(10, 0, 0, 10);
		imageView.setLayoutParams(param);
		
		waterLayout.addView(imageView, param);
		
	}

	//打开图片
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		 super.onActivityResult(requestCode, resultCode, data);  
		    if(resultCode==RESULT_OK && requestCode==IMAGE_CODE) {  
		    	Uri imageFileUri = data.getData();
		    	DisplayMetrics dm = new DisplayMetrics();
		    	getWindowManager().getDefaultDisplay().getMetrics(dm);  
		        int width = dm.widthPixels;    //手机屏幕水平分辨率   
		        int height = dm.heightPixels;  //手机屏幕垂直分辨率
		        try {  
		        	//标识变量=1 图片显示
    	        	flagOnTouch = 3;  
		            //载入图片尺寸大小没载入图片本身 true
		            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
		            bmpFactoryOptions.inJustDecodeBounds = true;  
		            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);  
		            int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);  //outHeight图像高
		            int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);     //outWidth图像宽
		            //inSampleSize表示图片占原图比例 =1表示原图
		            if(heightRatio>1&&widthRatio>1) {  
		                if(heightRatio>widthRatio) {  
		                    bmpFactoryOptions.inSampleSize = heightRatio;  
		                }  
		                else {  
		                    bmpFactoryOptions.inSampleSize = widthRatio;  
		                }  
		            }  
	                //图像真正解码 false
		            bmpFactoryOptions.inJustDecodeBounds = false;                 
		            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);    
		            //imageShow.setImageBitmap(bmp);
		            //显示文件路径
		            /*String[] filePathColumn= {MediaStore.Images.Media.DATA};
		            Cursor cursor = getContentResolver().query(imageFileUri, filePathColumn, null, null, null);
		            cursor.moveToFirst(); //将光标移至开头
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]); //获得用户选择的图片的索引值
		            path = cursor.getString(columnIndex);
		            cursor.close();
		            pathText.setText("path="+path);*/
		          //加载备份图片
		            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
							.getHeight(), bmp.getConfig());
					canvas = new Canvas(alteredBitmap);  //画布
					paint = new Paint(); //画刷
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					paint.setTextSize(30);
					paint.setTypeface(Typeface.DEFAULT_BOLD);  //无线粗体
					matrix = new Matrix();
					canvas.drawBitmap(bmp, matrix, paint);
					imageShow.setImageBitmap(alteredBitmap);
		            
		        } catch(FileNotFoundException e) { 
		            e.printStackTrace();  
		        }  
		    }  //end if
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
   
	
    public static Bitmap convertViewToBitmap(View view)
    {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }    

    // first SDCard is in the device, if yes, the pic will be stored in the SDCard, folder "HaHa_Picture"
    // second if SDCard not exist, the picture will be stored in /data/data/HaHa_Picture
    // file will be named by the customer
    public void saveImage(String strFileName) //保存图片函数
    {
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	
        //Bitmap bitmap = convertViewToBitmap(imageShow);
        try
        {
            File destDir = new File(SavePath);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            File imageFile = new File(SavePath+strFileName);
            //imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void SavePic(View v)  //保存图片按钮响应
    {   
    	CancelSelect();
    	SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyyMMddhhmmss");
		String datestr  =    sDateFormat.format(new  java.util.Date());
		String strFileName = datestr+".png";
        saveImage(strFileName);
		Toast.makeText(MainActivity.this, "saved in " + SavePath + strFileName, Toast.LENGTH_SHORT).show();

    }
    
    public void MoreMark(View v)  //批量水印按钮响应
    {   
    	CancelSelect();
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	//之前用来滤掉背景
//    	int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
//        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// 获得图片的ARGB值  
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //获取红通道的值
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //获取红绿道的值
//	        int blue = (argb[i] & 0x000000ff);                //获取蓝绿道的值
//	        if (red == 239 && green == 223 && blue == 223)
//        		argb[i] = (0x00FFFFFF);  
//        }
//        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
//        
        try
        {
            File destDir = new File(SavePath);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyyMMddhhmmss");
    		String datestr  =    sDateFormat.format(new  java.util.Date());
    		String strFileName = datestr+".png";
    		
            File imageFile = new File(SavePath+strFileName);  //此处保存的是临时图片
            //imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
        	
            
        	Intent intent = new Intent(MainActivity.this, ImgFileListActivity.class);
        	intent.putExtra("MoreMarkSrc",strFileName);
    		startActivity(intent);
    		
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public Bitmap CutBitmap(Bitmap bitmap)
    {
    	int firstx=0,firsty=0,cutwidth=bitmap.getWidth(),cutheight=bitmap.getHeight();
    	int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
    	bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// 获得图片的ARGB值
    	//裁剪为最大矩形
    	//获取第一个有效点
    	for(int i =0;i<argb.length;i++)
    	{
    		if((argb[i]|0x00FFFFFF)!= 0x00FFFFFF)
    		{
    			firstx=i/bitmap.getWidth();
    			break;
    		}
    	}
    	
    	
    	//获取最后一个有效点
    	for(int i =argb.length-1;i>0;i--)
    	{
    		if((argb[i]|0x00FFFFFF)!= 0x00FFFFFF)
    		{
    			cutwidth=i/bitmap.getWidth()-firstx;
    			break;
    		}
    	}
    	
    	FindY:for(int j=0;j<bitmap.getWidth();j++)
    	{
	    	for(int i=0;i<bitmap.getHeight();i++)
	    	{
	    		if((argb[j+i*bitmap.getWidth()]|0x00FFFFFF)!= 0x00FFFFFF)
	    		{
	    			firsty=j;
	    			break FindY;
	    		}
	    	}
    	}
    	FindH:for(int j=bitmap.getWidth()-1;j>0;j--)
    	{
	    	for(int i=0;i<bitmap.getHeight();i++)
	    	{
	    		if((argb[j+i*bitmap.getWidth()]|0x00FFFFFF)!= 0x00FFFFFF)
	    		{
	    			cutheight=j-firsty;
	    			break FindH;
	    		}
	    	}
    	}

    	try{
    		Bitmap temp = Bitmap.createBitmap(bitmap,firsty,firstx,cutheight,cutwidth);
    		return temp;
    	}
    	catch(Exception e)
    	{
    		return bitmap;
    	}
    	
    	
    }
    
    
    public void SvTemplet(View v)
    {
    	CancelSelect();
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	
    	bitmap=CutBitmap(bitmap);
    	
    	//之前过滤粉红色背景用
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //获取红通道的值
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //获取红绿道的值
//	        int blue = (argb[i] & 0x000000ff);                //获取蓝绿道的值
//	        if (red == 239 && green == 223 && blue == 223)
//        		argb[i] = (0x00FFFFFF);  
//        }
//        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
        
        try
        {
            File destDir = new File(SavePath);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            TempletNum++;
    		String strFileName = "Templet"+String.valueOf(TempletNum)+".png";
    		
            File imageFile = new File(SavePath+strFileName);
            //imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
            Toast.makeText(MainActivity.this, "已成功保存到模板"+String.valueOf(TempletNum)+"...", Toast.LENGTH_SHORT).show();
            
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void LdTemplet(View v)
    {

	    	
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("选择一个要添加的模板");
            //    指定下拉列表的显示数据
            final String[] cities = new String[TempletNum];
            for(int i =0;i<TempletNum;i++)
            {
            	cities[i]=String.valueOf(i+1);
            }
            	
            //    设置一个下拉的列表选择项
            builder.setItems(cities, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                	try
                	{
	                	FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aPic/"+"Templet"+String.valueOf(which+1)+".png");
	        	    	waterBitmap=BitmapFactory.decodeStream(fis);
	        	    	mPosition = mPosition + 1;
	        			WaterImageView imageView = new WaterImageView(MainActivity.this, 
	        					waterBitmap, ondeleteClick);
	        			imageView.setmPosition(mPosition);
	        	
	        			imageView.setId(mPosition);
	        			imageView.setIsClick(1);
	        			
	        			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	        			param.setMargins(10, 0, 0, 10);
	        			imageView.setLayoutParams(param);
	        			
	        			waterLayout.addView(imageView, param);
        			
                	}
                	catch(Exception e){}

                }
            });
            builder.show();

    }
    
    
    
    public void MarkPic(View v)//水印相机按钮响应
    {
    	/*
    	 //此处之前是跟批量水印同样原理，现在改为添加水印文字
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	
    	//之前用来滤掉背景
//    	int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
//        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// 获得图片的ARGB值  
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //获取红通道的值
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //获取红绿道的值
//	        int blue = (argb[i] & 0x000000ff);                //获取蓝绿道的值
//	        if (red == 239 && green == 223 && blue == 223)
//        		argb[i] = (0x00FFFFFF);  
//        }
//        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
        
        try
        {
            File destDir = new File(SavePath);
            if (!destDir.exists())
            {
                destDir.mkdirs();
            }
            SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyyMMddhhmmss");
    		String datestr  =    sDateFormat.format(new  java.util.Date());
    		String strFileName = datestr+".png";
    		
            File imageFile = new File(SavePath+strFileName);
            //imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
            */
        	Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        	//intent.putExtra("PhotoPath",strFileName);//如果加图片水印需要，传入文件名
        	intent.putExtra("PhotoPath","");//此处改为空，无需传值加文字水印
    		startActivity(intent);
            
//        }
//        catch (Exception e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    	

    }
    
    
    public void TakePhoto(View v)  //拍照按钮响应
    {   
    	Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		startActivity(intent);
    	
    }
    

    
    public void AddPic(View v)   //添加图片水印按钮响应
    {   
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择一个要添加的水印");
        //    指定下拉列表的显示数据
        final String[] watermarks = {"测试1", "测试2", "测试3", "测试4", "测试5"};
        //    设置一个下拉的列表选择项
        builder.setItems(watermarks, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            	switch (which)
            	{
                	case 0:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.a);
                		break;
                	case 1:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.h);
                		break;
                	case 2:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.k);
                		break;
                	case 3:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.m);
                		break;
                	case 4:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.u);
            			break;
                	default:waterBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.m);
                		break;
            	}
            	mPosition = mPosition + 1;
        		WaterImageView imageView = new WaterImageView(MainActivity.this, 
        				waterBitmap, ondeleteClick);
        		imageView.setmPosition(mPosition);

        		imageView.setId(mPosition);
        		imageView.setIsClick(1);
        		
        		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        		param.setMargins(10, 0, 0, 10);
        		imageView.setLayoutParams(param);
        		
        		waterLayout.addView(imageView, param);
            	Toast.makeText(MainActivity.this, "选择的为：" + watermarks[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
		
    }
    
	public OnDeleteClick ondeleteClick = new OnDeleteClick() {
		@Override
		public void delteClick(int position) {
			int count = waterLayout.getChildCount();
			for(int i = 0 ; i<count ; i++){
				View view = waterLayout.getChildAt(i);
				int id = view.getId();
				if(position == id){
					waterLayout.removeViewAt(i);
					return;
				}
			}
		}

		@Override
		public void selectedClick(int position) {
			int count = waterLayout.getChildCount();
			for(int i = 0 ; i<count ; i++){
				View view =  waterLayout.getChildAt(i);
				int id = view.getId();
				//0 代表要添加水印的图片
				if(i != 0){
					WaterImageView view2 = (WaterImageView) view;
					if(position == id){
						//1表示需要画边框，以及两个按钮图片
						view2.setIsClick(1-view2.getIsClick());
						
					}else{
						view2.setIsClick(0);
					}
					view2.invalidate();
				}
			}
		}
	};
	
	
	
	private class ConvertTask extends AsyncTask<Integer, Void, Bitmap> {

		@Override
		protected void onPostExecute(Bitmap result) {
			mDialog.dismiss();
			if (result != null) {//结果处理函数
				//mConvertedBitmap = result;
				//imageShow.setImageBitmap(result);
				
				Bitmap waterBitmap = result;
				mPosition = mPosition + 1;
				WaterImageView imageView = new WaterImageView(MainActivity.this, 
						waterBitmap, ondeleteClick);
				imageView.setmPosition(mPosition);
				imageView.setId(mPosition);
				imageView.setIsClick(1);
				
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				param.setMargins(10, 0, 0, 10);
				imageView.setLayoutParams(param);
				
				waterLayout.addView(imageView, param);
				
			}
			
		}

		@Override
		protected void onPreExecute() {
			if (mDialog == null) {
				mDialog = new ProgressDialog(MainActivity.this);
			}
			mDialog.show();
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			//int type = params[0];
			int r = params[1];
			if (mSourceBitmap == null) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) imageShow
						.getDrawable();
				mSourceBitmap = bitmapDrawable.getBitmap();
			} else if (mConvertedBitmap != null) {
				mConvertedBitmap.recycle();
				mConvertedBitmap = null;
			}

			Bitmap result = null;
			result = testSketch.testGaussBlur(mSourceBitmap, r, r / 3);
			
			return result;
		}

	}

    public void CreateLoading()
    {
    	//创建我们的进度条
    	proDia=new ProgressDialog(MainActivity.this);
    	proDia.setTitle("图片处理中");
    	proDia.setMessage("请耐心等待...");
    	proDia.setCanceledOnTouchOutside(false);
    	proDia.onStart();
    	proDia.show();
    }
    
    public void CancelSelect()//隐藏所有元素的编辑框
    {
		int count = waterLayout.getChildCount();
		for(int i = 0 ; i<count ; i++){
			View view =  waterLayout.getChildAt(i);
			int id = view.getId();
			//0 代表要添加水印的图片
			if(i != 0){
				WaterImageView view2 = (WaterImageView) view;
				if(false){
					//1表示需要画边框，以及两个按钮图片
					view2.setIsClick(1-view2.getIsClick());
					
				}else{
					view2.setIsClick(0);
				}
				view2.invalidate();
			}
		}
    }
    
    
    public void TimeMark()
    {
    	SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		String datestr  =    sDateFormat.format(new  java.util.Date());
    	Bitmap bmpTemp = Bitmap.createBitmap(datestr.length()*FrontTextSize/2+200, FrontTextSize+10, Config.ARGB_8888);  
        Canvas cv = new Canvas(bmpTemp);  
        Paint p = new Paint();  
        Typeface font = Typeface.create(FrontTextFont, Typeface.BOLD);  
        p.setColor(Color.BLUE);  
        p.setAlpha(FrontTextAlpha);
        p.setTypeface(font);
        p.setTextSize(FrontTextSize);  
        cv.drawText(datestr, 0, 100, p);  
        cv.save(Canvas.ALL_SAVE_FLAG);  

		mPosition = mPosition + 1;
		WaterImageView imageView = new WaterImageView(MainActivity.this, 
				bmpTemp, ondeleteClick);
		imageView.setmPosition(mPosition);
		imageView.setId(mPosition);
		imageView.setIsClick(1);
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		param.setMargins(10, 0, 0, 10);
		imageView.setLayoutParams(param);
		
		waterLayout.addView(imageView, param);
		
		cv.restore();
    }
    
	
}
