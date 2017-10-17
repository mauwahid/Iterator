
package me.umroh.iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.umroh.iterator.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class CameraPreview extends Activity implements SensorEventListener {
	
	private Preview mPreview; 
	private ImageView cameraPic;
	private TouchView touch;

	private boolean mAutoFocus = true;

	
	private SensorManager sensorMan;
	private Sensor sensor;
	private boolean mInitialized = false;
	private ReadingTranslate reTrans;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mLastZ = 0;
	private Rect rec = new Rect();
	
	protected ProgressDialog pd;
	private Terjemahan terjemahan;
		
	private final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/iterator/";
	private final String TAG = "Iterator";
	private final String lang = "ind";

	private int sHeight;
	private int sWidth;
	private boolean mInvalidate = false;
	private File mLocation = new File(Environment.getExternalStorageDirectory(),"iterator/test.jpg");
	private SharedPreferences sharePref;
	private String asal,tujuan;
	TessBaseAPI baseAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		
		dataPathSetting();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera_view); 

			sensorMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	
		// get the window width and height
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		sHeight = displaymetrics.heightPixels;
		sWidth = displaymetrics.widthPixels;
		//Log.i(TAG,"Window height: "+mScreenHeight);
		//Log.i(TAG,"Window Width: " + mScreenWidth);
		// I need to get the dimensions of this drawable
		Drawable mButtonDrawable = this.getResources().getDrawable(R.drawable.camera);

		//to hold my DownloadService intent
		
		cameraPic = (ImageView) findViewById(R.id.startcamerapreview);
		
		LayoutParams lp;
		lp = new LayoutParams(cameraPic.getLayoutParams());
		lp.setMargins((int)((double)sWidth*.85),
				(int)((double)sHeight*.30) ,
				(int)((double)sWidth*.85)+mButtonDrawable.getMinimumWidth(), 
				(int)((double)sHeight*.30)+mButtonDrawable.getMinimumHeight());
		cameraPic.setLayoutParams(lp);
		rec.set((int)((double)sWidth*.85),
				(int)((double)sHeight*.10) ,
				(int)((double)sWidth*.85)+mButtonDrawable.getMinimumWidth(), 
				(int)((double)sHeight*.70)+mButtonDrawable.getMinimumHeight());
		
		mButtonDrawable = null;
		cameraPic.setOnClickListener(previewListener);
		cameraPic.setOnLongClickListener(longListener);
	//	mTakePicture.setOnLongClickListener((OnLongClickListener) previewListener);
		// get our Views from the XML layout
		mPreview = (Preview) findViewById(R.id.preview);
		touch = (TouchView) findViewById(R.id.left_top_view);
		touch.setRec(rec);
	}


	public Double[] getRatio(){
		Size s = mPreview.getCameraParameters().getPreviewSize();
		double heightRatio = (double)s.height/(double)sHeight;
		double widthRatio = (double)s.width/(double)sWidth;
		Double[] ratio = {heightRatio,widthRatio};
		return ratio;
	}


	private OnClickListener previewListener = new OnClickListener() {

	
		public void onClick(View v) {
			Double[] ratio = getRatio();
			int left = (int) (ratio[1]*(double)touch.getmLeftTopPosX());
			// 0 is height
			int top = (int) (ratio[0]*(double)touch.getmLeftTopPosY());

			int right = (int)(ratio[1]*(double)touch.getmRightBottomPosX());

			int bottom = (int)(ratio[0]*(double)touch.getmRightBottomPosY());
			
			reTrans = new ReadingTranslate();
			reTrans.execute(new Integer[]{left,top,right,bottom});
			
			
			}	   
	};

	private OnLongClickListener longListener = new OnLongClickListener() {
		
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			
			Double[] ratio = getRatio();
			int left = (int) (ratio[1]*(double)touch.getmLeftTopPosX());
			// 0 is height
			int top = (int) (ratio[0]*(double)touch.getmLeftTopPosY());

			int right = (int)(ratio[1]*(double)touch.getmRightBottomPosX());

			int bottom = (int)(ratio[0]*(double)touch.getmRightBottomPosY());
			
			reTrans = new ReadingTranslate();
			reTrans.execute(new Integer[]{left,top,right,bottom});
			
	
			return false;
		}
	};

	 
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			//Log.i(TAG, "onKeyDown(" + keyCode + ")");

			// to take the pic ASAP, grab the preview frame data from here - don't wait for photo
			if (keyCode == KeyEvent.KEYCODE_BACK){
				finish();
			}
			return super.onKeyDown(keyCode, event); // pass the key along to other handlers 
		}

		private boolean savePhoto(Bitmap bm) {
			System.gc();
			FileOutputStream image = null;
			try {
				image = new FileOutputStream(mLocation);
			} catch (FileNotFoundException e) {
				//Log.i(TAG,"Could not find image locatoin");
				e.printStackTrace();
			}
			bm.compress(CompressFormat.JPEG, 100, image);
			Log.i("Save Photo", "Photo disimpan");
			return true;
		}

		public boolean onInterceptTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
			boolean intercept = false;
			switch (action) {
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_DOWN:
				float x = ev.getX();
				float y = ev.getY();

				if ((x >= rec.left) && (x <= rec.right) && (y>=rec.top) && (y<=rec.bottom)){
					intercept = true;
				}
				break;
			}
			return intercept;
		}


		public void onSensorChanged(SensorEvent event) {

			if (mInvalidate == true){
				touch.invalidate();
				mInvalidate = false;
			}
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			if (!mInitialized){
				mLastX = x;
				mLastY = y;
				mLastZ = z;
				mInitialized = true;
			}
			float deltaX  = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);

			if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
				mAutoFocus = false;
//				mPreview.setCameraFocus(myAutoFocusCallback);
			}
			if (deltaY > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
				mAutoFocus = false;
	//			mPreview.setCameraFocus(myAutoFocusCallback);
			}
			if (deltaZ > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) */
				mAutoFocus = false;
		//		mPreview.setCameraFocus(myAutoFocusCallback);
			}

			mLastX = x;
			mLastY = y;
			mLastZ = z;

		}

		// extra overrides to better understand app lifecycle and assist debugging
		@Override
		protected void onDestroy() {
			super.onDestroy();
			//Log.i(TAG, "onDestroy()");
		}

		@Override
		protected void onPause() {
			super.onPause();
			//Log.i(TAG, "onPause()");
			sensorMan.unregisterListener(this);
		}

		@Override
		protected void onResume() {
			super.onResume();
			sensorMan.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
			//Log.i(TAG, "onResume()");
		}

		@Override
		protected void onRestart() {
			super.onRestart();
			//Log.i(TAG, "onRestart()");
		}

		@Override
		protected void onStop() {
			super.onStop();
			//Log.i(TAG, "onStop()");
		}

		@Override
		protected void onStart() {
			super.onStart();
			//Log.i(TAG, "onStart()");
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		private void startOCR(){
			Log.i("Start OCR","Inisialisasi Tessbase Api");
			
		
			TessBaseAPI baseAPI = new TessBaseAPI();
			baseAPI.init(DATA_PATH, "ind");
			Log.i("inisialisasi mulai", "text ara");
			baseAPI.setPageSegMode(TessBaseAPI.PSM_AUTO_OSD);
			baseAPI.setImage(mLocation);
			String recognizedText = baseAPI.getUTF8Text();
			Log.i("recognize",recognizedText+"");
			touch.setOcrTextString(recognizedText);
			baseAPI.end();
			
			Log.i("base APi","End Base Api");
			
		
			translate(recognizedText);
			
		}
		
		private void translate(String recognizedText) {
			//Translate.setKey(KEY);

			String translatedText;
			sharePref = PreferenceManager.getDefaultSharedPreferences(this);
			asal = sharePref.getString("language_from", "ind");
			Log.i("Lang From :", asal);
			tujuan = sharePref.getString("language_to", "eng");
			Log.i("Lang to :",tujuan);
			
			
			try {
				
				
				terjemahan = new Terjemahan();
				translatedText = terjemahan.terjemahkan(recognizedText, asal, tujuan);
				touch.setOcrTextString(recognizedText);
				touch.setRecognizedTextString(translatedText);
				Log.i("REcog","recognized text");
			mInvalidate = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			// TODO Auto-generated method stub
			
				Intent intent = new Intent(this,LanguagePref.class);
				startActivity(intent);
				Log.i("Menu Selected",""+R.id.menu_settings);
				
			
			return super.onMenuItemSelected(featureId, item);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.setting, menu);
			return super.onCreateOptionsMenu(menu);
			
			
		}

		private void dataPathSetting(){
			String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
			Log.i(TAG,"dataPathSetting");
			for (String path : paths) {
				File dir = new File(path);
				if (!dir.exists()) {
					if (!dir.mkdirs()) {
						Log.i(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
						return;
					} else {
						Log.i(TAG, "Created directory " + path + " on sdcard");
					}
				}

			}
			
			if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
				try {

					AssetManager assetManager = getAssets();
					InputStream in = assetManager.open("tessdata/ind.traineddata");
					OutputStream out = new FileOutputStream(DATA_PATH
							+ "tessdata/ind.traineddata");

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
					
					Log.i(TAG, "Copied " + lang + " traineddata");
				} catch (IOException e) {
					Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
				}
			}

			
		}
		
		
		class ReadingTranslate extends AsyncTask<Integer, String, String>{
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				pd.dismiss();
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				pd = new ProgressDialog(CameraPreview.this);
				pd.setTitle("");
				pd.setMessage("Terjemahkan...");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Integer... params){
				// TODO Auto-generated method stub
				int left = params[0];
				int top = params[1];
				int right = params[2];
				int bottom = params[3];
				publishProgress("Update...");
				savePhoto(mPreview.getPic(left,top,right,bottom));
				startOCR();
				
				publishProgress("Done...");
				return null;
			}
			
		}

		
}