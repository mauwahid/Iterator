package me.umroh.iterator;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

class Preview extends SurfaceView implements SurfaceHolder.Callback {	

	private SurfaceHolder holder;
	private Camera camera;
	private Camera.Parameters param;
	private byte[] buffer;

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Preview(Context context) {
		super(context);
		init();
	}

	public void init() {
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public Bitmap getPic(int x, int y, int width, int height) {
		System.gc(); 
		Bitmap b = null;
		Size s = param.getPreviewSize();

		YuvImage yuvimage = new YuvImage(buffer, ImageFormat.NV21, s.width, s.height, null);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(x, y, width, height), 100, outStream); // make JPG
		b = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size()); // decode JPG
		
		yuvimage = null;
		outStream = null;
		System.gc();
		return b;
	}

	private void updateBufferSize() {
		buffer = null;
		System.gc();
		int h = camera.getParameters().getPreviewSize().height;
		int w = camera.getParameters().getPreviewSize().width;
		int bitsPerPixel = ImageFormat.getBitsPerPixel( camera.getParameters().getPreviewFormat() );
		buffer = new byte[w * h * bitsPerPixel / 8];
	}

	public void surfaceCreated(SurfaceHolder holder) {
	try {
			camera = Camera.open(); 
		}
		catch (RuntimeException exception) {
			Toast.makeText(getContext(), "Camera broken, quitting :(", Toast.LENGTH_LONG).show();
			// TODO: exit program
		}

		try {
			camera.setPreviewDisplay(holder);
			updateBufferSize();
			camera.addCallbackBuffer(buffer); // where we'll store the image data
			camera.setPreviewCallbackWithBuffer(new PreviewCallback() {
				public synchronized void onPreviewFrame(byte[] data, Camera c) {

					if (camera != null) { // there was a race condition when onStop() was called..
						camera.addCallbackBuffer(buffer); // it was consumed by the call, add it back
					}
				}
			});
		} catch (Exception exception) {
			camera.release();
			camera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		try {
			param = camera.getParameters();
			param.set("orientation","landscape");
			
			camera.setParameters(param); // apply the changes
		} catch (Exception e) {
		}

		updateBufferSize(); // then use them to calculate

		camera.startPreview();
	}

	public Parameters getCameraParameters(){
		return camera.getParameters();
	}

	public void setCameraFocus(AutoFocusCallback autoFocus){
		camera.autoFocus(autoFocus);
	}

	public void setFlash(boolean flash){
		if (flash){
			param.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(param);
		}
		else{
			param.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(param);
		}
	}
}
