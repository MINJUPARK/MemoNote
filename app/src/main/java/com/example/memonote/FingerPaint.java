package com.example.memonote;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;

public class FingerPaint extends MainActivity implements View.OnClickListener {
	int curMode = 1;
	private int curColor = Color.BLACK;
	private int curWidth = 5;
	// Current Mode, Color, Width variable

	final int PENMODE = 1, ERASERMODE = 2, RECTMODE = 3, CIRCLEMODE = 4;
	// Mode Change

	private Paint mPaint;
	private Path mPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
	}

	// 펜 모드 설정
	protected void penInit() {
		mPaint = new Paint();
		mPaint.setDither(true); // 디더링 효과 (없으면 빠르지만 정확도↓)
		mPaint.setColor(curColor); // 색상 변경
		mPaint.setStyle(Paint.Style.STROKE); // stroke는 겉만 그리기
		mPaint.setStrokeJoin(Paint.Join.ROUND); // 끝 부분을 둥글게
		mPaint.setStrokeCap(Paint.Cap.ROUND); // 모서리를 둥글게
		mPaint.setStrokeWidth(curWidth); // 선 굵기
		mPaint.setAntiAlias(true); // 경계에 중간색 삽입해서 품질 좋게하기
	}

	// 지우개 모드 설정
	protected void eraserInit() {
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(curWidth);
		mPaint.setAntiAlias(true);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(new MyView(this));
		penInit();
	}

	// 펜의 굵기 설정
	public void setWidth() {
		final EditText editText = new EditText(this);
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("1부터 16사이의 사이즈 입력");
		ad.setView(editText);
		ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			// Event Processing
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String tmpText = editText.getText().toString();
				int width = 0;
				if (!(tmpText.equals(""))) {
					width = Integer.parseInt(tmpText);
					if (width <= 0 || width > 16)
						width = 5;
					curWidth = width;
				} else { // non input, error (Exception)
					curWidth = 5;
				}
				if (curMode == PENMODE)
					penInit();
				else if (curMode == ERASERMODE)
					eraserInit();
			}
		});
		ad.show();
	}


	public void onClick(DialogInterface dialog, int which) {
	}

	class MyView extends View {

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Paint mBitmapPaint;
		float startX, startY;
		float stopX, stopY;

		public MyView(Context context) {
			super(context);

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			mBitmap = Bitmap.createBitmap(metrics.widthPixels,
					metrics.heightPixels, Bitmap.Config.ARGB_8888);

			mBitmapPaint = new Paint(Paint.DITHER_FLAG);

			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mCanvas.drawColor(Color.WHITE); // Background Color
			curMode = PENMODE;
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
			// 이 부분이 없는 경우 지우개 사용 시 하얀 선이 그려짐

			if (curMode != ERASERMODE)
				canvas.drawPath(mPath, mPaint);
		}

		private float mX, mY;

		// 터치 시작, 드래그 시작
		private void touch_start(float x, float y) {
			mPath.reset();
			switch (curMode) {
				case PENMODE:
				case ERASERMODE:
					mPath.moveTo(x, y);
					mX = x;
					mY = y;
					break;

				case RECTMODE:
				case CIRCLEMODE:
					break;
			}
		}

		// 드래그
		private void touch_move(float x, float y) {

			switch (curMode) {
				case PENMODE:
				case ERASERMODE:
					mPath.lineTo(x, y);
					mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
					mX = x;
					mY = y;

				case RECTMODE:
				case CIRCLEMODE:
					break;
			}
		}

		// 드래그 종료
		private void touch_up(float x, float y) {

			switch (curMode) {
				case PENMODE:
				case ERASERMODE:
					mPath.lineTo(mX, mY);
					break;

				case RECTMODE:
					mPath.addRect(startX, startY, stopX, stopY, Path.Direction.CW);
					break;

				case CIRCLEMODE:
					// 원 모양 계산
					mPath.addCircle(startX, startY, (float) Math.sqrt((Math.pow(
							Math.abs(stopX - startX), 2) + Math.pow(
							Math.abs(stopY - startY), 2))), Path.Direction.CW);
					break;
			}
			mCanvas.drawPath(mPath, mPaint);
		}

		// 이벤트 프로그래밍
		public boolean onTouchEvent(MotionEvent event) {

			int action = event.getAction();

			switch (action) {

				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					touch_start(startX, startY);
					break;

				case MotionEvent.ACTION_MOVE:
					stopX = event.getX();
					stopY = event.getY();
					touch_move(stopX, stopY);
					break;

				case MotionEvent.ACTION_UP:
					stopX = event.getX();
					stopY = event.getY();
					touch_up(stopX, stopY);
					break;
			}
			invalidate(); // onDraw를 호출하게 된다. 즉 실질적인 그리기
			return true;
		}
	}

	// 옵션 메뉴  설정
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		return true;
	}

	// 옵션 이벤트 프로그래밍
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.initialize: // 모두 초기화 시킨다.
				curColor = Color.BLACK;
				mPath.reset();
				onResume();
				break;

			case R.id.eraser_mode: // 지우개 모드
				curMode = ERASERMODE;
				eraserInit();
				break;

			case R.id.pen_mode: // 펜 모드
				curMode = PENMODE;
				penInit();
				break;

			case R.id.rect_mode: // 다각형 모드
				curMode = RECTMODE;
				penInit();
				break;

			case R.id.circle_mode: // 원 모드
				curMode = CIRCLEMODE;
				penInit();
				break;

			// 색상 변경
			case R.id.black:
				curColor = Color.BLACK;
				penInit();
				break;

			case R.id.red:
				curColor = Color.RED;
				penInit();
				break;

			case R.id.green:
				curColor = Color.GREEN;
				penInit();
				break;

			case R.id.blue:
				curColor = Color.BLUE;
				penInit();
				break;

			// 굵기 설정
			case R.id.width_choice:
				setWidth();
				penInit();
				break;
		}
		return true;
	}
}