package me.umroh.iterator;

import me.umroh.iterator.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


public class TouchView extends View{
	
	private Drawable mLeftTopIcon;
	private Drawable mRightTopIcon;
	private Drawable mLeftBottomIcon;
	private Drawable mRightBottomIcon;

	private boolean mLeftTopBool = false;
	private boolean mRightTopBool = false;
	private boolean mLeftBottomBool = false;
	private boolean mRightBottomBool = false;

	private float mLeftTopPosX = 30;
	//private float mLeftTopPosY = 120;
	private float mLeftTopPosY = 90;


	private float mRightTopPosX = 150;
//	private float mRightTopPosY = 120;
	private float mRightTopPosY = 90;

	private float mLeftBottomPosX = 30;
//	private float mLeftBottomPosY = 200;
	private float mLeftBottomPosY = 160;

	private float mRightBottomPosX = 150;
	//private float mRightBottomPosY = 200;
	private float mRightBottomPosY = 160;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;

	private Paint topLine;
	private Paint bottomLine;
	private Paint leftLine;
	private Paint rightLine;

	private Rect buttonRec;
	
	private int mCenter;

	private static final int INVALID_POINTER_ID = -1;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	
	private Paint ocrText;
	private String ocrTextString = "";

	private Paint recognizedText;
	private String recognizedTextString = "";

	public void setOcrTextString(String ocrTextString){
		this.ocrTextString = ocrTextString;
	}
	
	public void setRecognizedTextString(String recognizedTextString) {
		this.recognizedTextString = recognizedTextString;
	}

	public TouchView(Context context){
		super(context);
		init(context);
	}

	public TouchView(Context context, AttributeSet attrs){
		super (context,attrs);
		init(context);
	}

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {



		topLine = new Paint();
		bottomLine = new Paint();
		leftLine = new Paint();
		rightLine = new Paint();
		recognizedText = new Paint();
		ocrText = new Paint();
/*		Typeface face = Typeface.createFromAsset(context.getAssets(), "trado.ttf");
		recognizedText.setTypeface(face);
		ocrText.setTypeface(face);
*/
		setLineParameters(Color.WHITE,2);

		mLeftTopIcon = context.getResources().getDrawable(R.drawable.corners);
		
		mCenter = mLeftTopIcon.getMinimumHeight()/2;
		mLeftTopIcon.setBounds((int)mLeftTopPosX, (int)mLeftTopPosY,
				mLeftTopIcon.getIntrinsicWidth()+(int)mLeftTopPosX,
				mLeftTopIcon.getIntrinsicHeight()+(int)mLeftTopPosY);

		mRightTopIcon = context.getResources().getDrawable(R.drawable.corners);
		mRightTopIcon.setBounds((int)mRightTopPosX, (int)mRightTopPosY,
				mRightTopIcon.getIntrinsicWidth()+(int)mRightTopPosX,
				mRightTopIcon.getIntrinsicHeight()+(int)mRightTopPosY);

		mLeftBottomIcon = context.getResources().getDrawable(R.drawable.corners);
		mLeftBottomIcon.setBounds((int)mLeftBottomPosX, (int)mLeftBottomPosY,
				mLeftBottomIcon.getIntrinsicWidth()+(int)mLeftBottomPosX,
				mLeftBottomIcon.getIntrinsicHeight()+(int)mLeftBottomPosY);

		mRightBottomIcon = context.getResources().getDrawable(R.drawable.corners);
		mRightBottomIcon.setBounds((int)mRightBottomPosX, (int)mRightBottomPosY,
				mRightBottomIcon.getIntrinsicWidth()+(int)mRightBottomPosX,
				mRightBottomIcon.getIntrinsicHeight()+(int)mRightBottomPosY);
		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

	}

	private void setLineParameters(int color, float width){

		topLine.setColor(color);
		topLine.setStrokeWidth(width);

		bottomLine.setColor(color);
		bottomLine.setStrokeWidth(width);

		leftLine.setColor(color);
		leftLine.setStrokeWidth(width);

		rightLine.setColor(color);
		rightLine.setStrokeWidth(width);
		
		recognizedText.setColor(Color.RED);
		recognizedText.setFakeBoldText(true);
		recognizedText.setTextSize(15);
		
		ocrText.setColor(Color.BLUE);
		ocrText.setFakeBoldText(true);
		ocrText.setTextSize(15);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		
		canvas.drawText("Translate: "+recognizedTextString, mLeftBottomPosX+10, mLeftBottomPosY+75, recognizedText);
		canvas.drawText("Text: "+ocrTextString, mLeftBottomPosX+10, mLeftBottomPosY+50, ocrText);
		
		canvas.drawLine(mLeftTopPosX+mCenter, mLeftTopPosY+mCenter,
				mRightTopPosX+mCenter, mRightTopPosY+mCenter, topLine);
		canvas.drawLine(mLeftBottomPosX+mCenter, mLeftBottomPosY+mCenter,
				mRightBottomPosX+mCenter, mRightBottomPosY+mCenter, bottomLine);
		canvas.drawLine(mLeftTopPosX+mCenter,mLeftTopPosY+mCenter,
				mLeftBottomPosX+mCenter,mLeftBottomPosY+mCenter,leftLine);
		canvas.drawLine(mRightTopPosX+mCenter,mRightTopPosY+mCenter,
				mRightBottomPosX+mCenter,mRightBottomPosY+mCenter,rightLine);
		//canvas.translate(mPosX, mPosY);
		//canvas.scale(mScaleFactor, mScaleFactor);


		mLeftTopIcon.setBounds((int)mLeftTopPosX, (int)mLeftTopPosY,
				mLeftTopIcon.getIntrinsicWidth()+(int)mLeftTopPosX,
				mLeftTopIcon.getIntrinsicHeight()+(int)mLeftTopPosY);

		mRightTopIcon.setBounds((int)mRightTopPosX, (int)mRightTopPosY,
				mRightTopIcon.getIntrinsicWidth()+(int)mRightTopPosX,
				mRightTopIcon.getIntrinsicHeight()+(int)mRightTopPosY);

		mLeftBottomIcon.setBounds((int)mLeftBottomPosX, (int)mLeftBottomPosY,
				mLeftBottomIcon.getIntrinsicWidth()+(int)mLeftBottomPosX,
				mLeftBottomIcon.getIntrinsicHeight()+(int)mLeftBottomPosY);

		mRightBottomIcon.setBounds((int)mRightBottomPosX, (int)mRightBottomPosY,
				mRightBottomIcon.getIntrinsicWidth()+(int)mRightBottomPosX,
				mRightBottomIcon.getIntrinsicHeight()+(int)mRightBottomPosY);


		mLeftTopIcon.draw(canvas);
		mRightTopIcon.draw(canvas);
		mLeftBottomIcon.draw(canvas);
		mRightBottomIcon.draw(canvas);
		canvas.restore();
	}


	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		boolean intercept = true;

		switch (action) {

		case MotionEvent.ACTION_DOWN: {

			final float x = ev.getX();
			final float y = ev.getY();

			if ((x >= buttonRec.left) && (x <=buttonRec.right) && (y>=buttonRec.top) && (y<=buttonRec.bottom)){
				intercept = false;
				break;
			}
			manhattanDistance(x,y);

			// Remember where we started
			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {

			final float x = ev.getX();
			final float y = ev.getY();
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();
			}

			final float dx = x - mLastTouchX;
			final float dy = y - mLastTouchY;

			
			if (mPosX >= 0 && mPosX <=800){
				mPosX += dx;
			}
			if (mPosY >=0 && mPosY <= 480){
				mPosY += dy;
			}

			if (mLeftTopBool && ((y+mCenter*2) < mLeftBottomPosY) && ((x+mCenter*2) < mRightTopPosX)){
				if (dy != 0){
					mRightTopPosY = y;
				}
				if (dx != 0){
					mLeftBottomPosX = x;
				}
				mLeftTopPosX = x;//mPosX;
				mLeftTopPosY = y;//mPosY;
			}
			if (mRightTopBool && ((y+mCenter*2) < mRightBottomPosY) && (x > (mLeftTopPosX+mCenter*2))){
				if (dy != 0){
					mLeftTopPosY = y;
				}
				if (dx != 0){
					mRightBottomPosX = x;
				}
				mRightTopPosX = x;//mPosX;
				mRightTopPosY = y;//mPosY;
			}
			if (mLeftBottomBool && (y > (mLeftTopPosY+mCenter*2)) && ((x +mCenter*2) < mRightBottomPosX)){
				if (dx != 0){
					mLeftTopPosX = x;
				}
				if (dy != 0){
					mRightBottomPosY = y;
				}
				mLeftBottomPosX = x;
				mLeftBottomPosY = y;
			}
			if (mRightBottomBool && (y > (mLeftTopPosY+mCenter*2)) && (x > (mLeftBottomPosX+mCenter*2) )){
				if (dx != 0){
					mRightTopPosX = x;
				}
				if (dy != 0){
					mLeftBottomPosY = y;
				}
				mRightBottomPosX = x;
				mRightBottomPosY = y;
			}

			// Remember this touch position for the next move event
			mLastTouchX = x;
			mLastTouchY = y;

			// Invalidate to request a redraw
			invalidate();
			break;
		}
		case MotionEvent.ACTION_UP: {
			mLeftTopBool = false;
			mRightTopBool = false;
			mLeftBottomBool = false;
			mRightBottomBool = false;
			//mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
			>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}
		return intercept;
	}

	private void manhattanDistance(float x, float y) {

		double leftTopMan = Math.sqrt(Math.pow((Math.abs((double)x-(double)mLeftTopPosX)),2)
				+ Math.pow((Math.abs((double)y-(double)mLeftTopPosY)),2));

		double rightTopMan = Math.sqrt(Math.pow((Math.abs((double)x-(double)mRightTopPosX)),2)
				+ Math.pow((Math.abs((double)y-(double)mRightTopPosY)),2));

		double leftBottomMan = Math.sqrt(Math.pow((Math.abs((double)x-(double)mLeftBottomPosX)),2)
				+ Math.pow((Math.abs((double)y-(double)mLeftBottomPosY)),2));

		double rightBottomMan = Math.sqrt(Math.pow((Math.abs((double)x-(double)mRightBottomPosX)),2)
				+ Math.pow((Math.abs((double)y-(double)mRightBottomPosY)),2));
		if (leftTopMan < 50){
			mLeftTopBool = true;
			mRightTopBool = false;
			mLeftBottomBool = false;
			mRightBottomBool = false;
		}
		else if (rightTopMan < 50){
			mLeftTopBool = false;
			mRightTopBool = true;
			mLeftBottomBool = false;
			mRightBottomBool = false;
		}
		else if (leftBottomMan < 50){
			mLeftTopBool = false;
			mRightTopBool = false;
			mLeftBottomBool = true;
			mRightBottomBool = false;
		}
		else if (rightBottomMan < 50){
			mLeftTopBool = false;
			mRightTopBool = false;
			mLeftBottomBool = false;
			mRightBottomBool = true;
		}

	}
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			invalidate();
			return true;
		}
	}

	public float getmLeftTopPosX(){
		return mLeftTopPosX;
	}
	public float getmLeftTopPosY(){
		return mLeftTopPosY;
	}
	public float getmRightTopPosX(){
		return mRightTopPosX;
	}
	public float getmRightTopPosY(){
		return mRightTopPosY;
	}
	public float getmLeftBottomPosX() {
		return mLeftBottomPosX;
	}
	public float getmLeftBottomPosY() {
		return mLeftBottomPosY;
	}
	public float getmRightBottomPosY() {
		return mRightBottomPosY;
	}
	public float getmRightBottomPosX() {
		return mRightBottomPosX;
	}
	public void setRec(Rect rec) {
		this.buttonRec = rec;

	}

	public void setInvalidate() {
		invalidate();
		
	}
}
