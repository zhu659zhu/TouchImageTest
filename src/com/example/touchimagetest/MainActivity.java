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

//�Զ������
private Button openImageBn;           //��ͼƬ
private Bitmap bmp;                         //ԭʼͼƬ
private TextView pathText;                //·��TextView
private String path;                           //�洢ͼƬ·��
private ImageView imageShow;         //��ʾͼƬ
private final int IMAGE_CODE = 0;   //��ͼƬ
//��������ͼƬ
private static final int NONE = 0;     //��ʼ״̬
private static final int DRAG = 1;      //�϶�
private static final int ZOOM = 2;     //����
private int mode = NONE;                 //��ǰ�¼�
private float oldDist;
private PointF startPoint = new PointF();
private PointF middlePoint = new PointF();
private Matrix matrix = new Matrix();
private Matrix savedMatrix = new Matrix();

//������ť
private Button wordAddBn;              //�������
private Button changeImageBn;       //����ͼƬ
private Button drawImageBn;           //����ͼƬ
//ͼƬ����ʱ��ʾ����
private Bitmap alteredBitmap;          //ͼƬ
private Canvas canvas;                    //����
private Paint paint;                          //��ˢ
private RelativeLayout layout;
//��ʶ����  1-��ʾͼƬ 2-������� 3-����ͼƬ 4-��ͼ
private int flagOnTouch = 0;         

//�����ı���******************** on 2016.3.30
String SavePath = Environment.getExternalStorageDirectory()+"/aPic/";

private int mPosition = 0;  //��ӵ�Ԫ�ؼ���
private RelativeLayout waterLayout;
private EditText FrontText;//ˮӡ���ֱ༭��
private int FrontTextSize=100;//ˮӡ��С
private int FrontTextAlpha=125;//ˮӡ͸����(0-255)
private String FrontTextFont="����";
private int FrontTextColor = Color.BLUE;
private String photoPath ="";  //������ص�ͼƬ��ַ

private int TempletNum = 0;

private ProgressDialog mDialog; //ˮӡͼ������...
private Bitmap mSourceBitmap;
private Bitmap waterBitmap;
private Bitmap mConvertedBitmap;
private int PicPara=10;//�˾��������   10Ч���Ϻ�
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
						            proDia.dismiss();//���ضԻ���
						            
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
	
	
    
    //��ͼƬ
    openImageBn.setOnClickListener(new OnClickListener() {
    	@Override
    	public void onClick(View v) {
    		Intent intent = new Intent(Intent.ACTION_PICK, 
    				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    		startActivityForResult(intent, IMAGE_CODE);
    	}
    });
    
	//��������ͼƬ���� ע:XML���޸�android:scaleType="matrix"
	imageShow.setOnTouchListener(new OnTouchListener() {
		
		//���������� ��������(downx, downy)��̧������(upx, upy)
		float downx = 0;
		float downy = 0;
		float upx = 0;
		float upy = 0;
		//�����¼�
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView view = (ImageView) v;
			
			//ͼƬ����
			if(flagOnTouch == 3)  {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: //��ָ����
					savedMatrix.set(matrix);
					startPoint.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event); //�������������10 ���ģʽ
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(middlePoint, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) { //�϶�
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
					} else if (mode == ZOOM) { //����
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
			//��ʾͼƬ
			if(flagOnTouch == 1) {
				return true;
			}
			//ͼƬ�������
			else if(flagOnTouch == 2) {
				return true;
	    	}
			//����ͼ��
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
		//�������
		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
		//�����е�
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
	});

	//����ͼƬ �����ť"����"
	changeImageBn = (Button) findViewById(R.id.button3);
	changeImageBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("ѡ��һ��Ҫ��ӵ�ӡ��");
            //    ָ�������б����ʾ����
            final String[] cities = {"����1", "����2", "����3", "����4", "����5"};
            //    ����һ���������б�ѡ����
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
                	Toast.makeText(MainActivity.this, "ѡ���Ϊ��" + cities[which], Toast.LENGTH_SHORT).show();
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
	
	//���ƻ�ͼ �����ť"����"
	drawImageBn = (Button) findViewById(R.id.button4);
	drawImageBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				flagOnTouch = 4;
				//��ͼ ͼƬ�ƶ���(0,0) �����ͼ������ָ�������
				matrix = new Matrix();
				matrix.postTranslate(0, 0);
				imageShow.setImageMatrix(matrix);
				imageShow.setImageBitmap(alteredBitmap); 
		        canvas.drawBitmap(bmp, matrix, paint);  
			}
			catch(Exception e)
			{
				Toast.makeText(MainActivity.this, "����ѡ��ͼƬ~", Toast.LENGTH_SHORT).show();
			}
		}
	 });

	//���ˮӡ���� 
	wordAddBn = (Button) findViewById(R.id.button2);
	layout = (RelativeLayout) findViewById(R.id.Content_Layout);
	wordAddBn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			try
			{
				if( FrontText.getText().toString().length()==0)
				{
					FrontText.setText("������...");
				}
				
				flagOnTouch = 3;
				//�������
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
				Toast.makeText(MainActivity.this, "����ѡ��ͼƬ~", Toast.LENGTH_SHORT).show();
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
		//���ӡ��
		//mSourceBitmap= testSketch.OverLay(BitmapFactory.decodeResource(getResources(), R.drawable.m),BitmapFactory.decodeResource(getResources(), R.drawable.h));
		//mSourceBitmap= testSketch.OverLay(mSourceBitmap, BitmapFactory.decodeResource(getResources(), R.drawable.k));
		//new ConvertTask().execute(new Integer[] { 3, PicPara });
		
		//���ͼƬ
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

	//��ͼƬ
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		 super.onActivityResult(requestCode, resultCode, data);  
		    if(resultCode==RESULT_OK && requestCode==IMAGE_CODE) {  
		    	Uri imageFileUri = data.getData();
		    	DisplayMetrics dm = new DisplayMetrics();
		    	getWindowManager().getDefaultDisplay().getMetrics(dm);  
		        int width = dm.widthPixels;    //�ֻ���Ļˮƽ�ֱ���   
		        int height = dm.heightPixels;  //�ֻ���Ļ��ֱ�ֱ���
		        try {  
		        	//��ʶ����=1 ͼƬ��ʾ
    	        	flagOnTouch = 3;  
		            //����ͼƬ�ߴ��Сû����ͼƬ���� true
		            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
		            bmpFactoryOptions.inJustDecodeBounds = true;  
		            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);  
		            int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);  //outHeightͼ���
		            int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);     //outWidthͼ���
		            //inSampleSize��ʾͼƬռԭͼ���� =1��ʾԭͼ
		            if(heightRatio>1&&widthRatio>1) {  
		                if(heightRatio>widthRatio) {  
		                    bmpFactoryOptions.inSampleSize = heightRatio;  
		                }  
		                else {  
		                    bmpFactoryOptions.inSampleSize = widthRatio;  
		                }  
		            }  
	                //ͼ���������� false
		            bmpFactoryOptions.inJustDecodeBounds = false;                 
		            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);    
		            //imageShow.setImageBitmap(bmp);
		            //��ʾ�ļ�·��
		            /*String[] filePathColumn= {MediaStore.Images.Media.DATA};
		            Cursor cursor = getContentResolver().query(imageFileUri, filePathColumn, null, null, null);
		            cursor.moveToFirst(); //�����������ͷ
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]); //����û�ѡ���ͼƬ������ֵ
		            path = cursor.getString(columnIndex);
		            cursor.close();
		            pathText.setText("path="+path);*/
		          //���ر���ͼƬ
		            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
							.getHeight(), bmp.getConfig());
					canvas = new Canvas(alteredBitmap);  //����
					paint = new Paint(); //��ˢ
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					paint.setTextSize(30);
					paint.setTypeface(Typeface.DEFAULT_BOLD);  //���ߴ���
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
    public void saveImage(String strFileName) //����ͼƬ����
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
    
    public void SavePic(View v)  //����ͼƬ��ť��Ӧ
    {   
    	CancelSelect();
    	SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyyMMddhhmmss");
		String datestr  =    sDateFormat.format(new  java.util.Date());
		String strFileName = datestr+".png";
        saveImage(strFileName);
		Toast.makeText(MainActivity.this, "saved in " + SavePath + strFileName, Toast.LENGTH_SHORT).show();

    }
    
    public void MoreMark(View v)  //����ˮӡ��ť��Ӧ
    {   
    	CancelSelect();
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	//֮ǰ�����˵�����
//    	int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
//        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// ���ͼƬ��ARGBֵ  
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //��ȡ��ͨ����ֵ
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //��ȡ���̵���ֵ
//	        int blue = (argb[i] & 0x000000ff);                //��ȡ���̵���ֵ
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
    		
            File imageFile = new File(SavePath+strFileName);  //�˴����������ʱͼƬ
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
    	bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// ���ͼƬ��ARGBֵ
    	//�ü�Ϊ������
    	//��ȡ��һ����Ч��
    	for(int i =0;i<argb.length;i++)
    	{
    		if((argb[i]|0x00FFFFFF)!= 0x00FFFFFF)
    		{
    			firstx=i/bitmap.getWidth();
    			break;
    		}
    	}
    	
    	
    	//��ȡ���һ����Ч��
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
    	
    	//֮ǰ���˷ۺ�ɫ������
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //��ȡ��ͨ����ֵ
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //��ȡ���̵���ֵ
//	        int blue = (argb[i] & 0x000000ff);                //��ȡ���̵���ֵ
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
            Toast.makeText(MainActivity.this, "�ѳɹ����浽ģ��"+String.valueOf(TempletNum)+"...", Toast.LENGTH_SHORT).show();
            
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
            builder.setTitle("ѡ��һ��Ҫ��ӵ�ģ��");
            //    ָ�������б����ʾ����
            final String[] cities = new String[TempletNum];
            for(int i =0;i<TempletNum;i++)
            {
            	cities[i]=String.valueOf(i+1);
            }
            	
            //    ����һ���������б�ѡ����
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
    
    
    
    public void MarkPic(View v)//ˮӡ�����ť��Ӧ
    {
    	/*
    	 //�˴�֮ǰ�Ǹ�����ˮӡͬ��ԭ�����ڸ�Ϊ���ˮӡ����
    	Bitmap bitmap = MyUtil.getScreenShot(waterLayout);
    	
    	//֮ǰ�����˵�����
//    	int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];  
//        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());// ���ͼƬ��ARGBֵ  
//        for (int i = 0; i < argb.length; i++) {  
//        	int red = (argb[i] & 0x00ff0000) >> 16;       //��ȡ��ͨ����ֵ
//	        int green = (argb[i] & 0x0000ff00) >> 8;     //��ȡ���̵���ֵ
//	        int blue = (argb[i] & 0x000000ff);                //��ȡ���̵���ֵ
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
        	//intent.putExtra("PhotoPath",strFileName);//�����ͼƬˮӡ��Ҫ�������ļ���
        	intent.putExtra("PhotoPath","");//�˴���Ϊ�գ����贫ֵ������ˮӡ
    		startActivity(intent);
            
//        }
//        catch (Exception e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    	

    }
    
    
    public void TakePhoto(View v)  //���հ�ť��Ӧ
    {   
    	Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		startActivity(intent);
    	
    }
    

    
    public void AddPic(View v)   //���ͼƬˮӡ��ť��Ӧ
    {   
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("ѡ��һ��Ҫ��ӵ�ˮӡ");
        //    ָ�������б����ʾ����
        final String[] watermarks = {"����1", "����2", "����3", "����4", "����5"};
        //    ����һ���������б�ѡ����
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
            	Toast.makeText(MainActivity.this, "ѡ���Ϊ��" + watermarks[which], Toast.LENGTH_SHORT).show();
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
				//0 ����Ҫ���ˮӡ��ͼƬ
				if(i != 0){
					WaterImageView view2 = (WaterImageView) view;
					if(position == id){
						//1��ʾ��Ҫ���߿��Լ�������ťͼƬ
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
			if (result != null) {//���������
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
    	//�������ǵĽ�����
    	proDia=new ProgressDialog(MainActivity.this);
    	proDia.setTitle("ͼƬ������");
    	proDia.setMessage("�����ĵȴ�...");
    	proDia.setCanceledOnTouchOutside(false);
    	proDia.onStart();
    	proDia.show();
    }
    
    public void CancelSelect()//��������Ԫ�صı༭��
    {
		int count = waterLayout.getChildCount();
		for(int i = 0 ; i<count ; i++){
			View view =  waterLayout.getChildAt(i);
			int id = view.getId();
			//0 ����Ҫ���ˮӡ��ͼƬ
			if(i != 0){
				WaterImageView view2 = (WaterImageView) view;
				if(false){
					//1��ʾ��Ҫ���߿��Լ�������ťͼƬ
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
    	SimpleDateFormat   sDateFormat    =   new    SimpleDateFormat("yyyy��MM��dd�� hh:mm:ss");
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
