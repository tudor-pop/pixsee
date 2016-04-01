package com.marked.pixsee.face;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by Tudor Pop on 01-Apr-16.
 */
public class CameraSourcePreview extends ViewGroup {
	private static final String TAG = "CameraSourcePreview";

	private Context       mContext;
	private GLSurfaceView mSurfaceView;
	private AugRenderer   mAugRenderer;

	private boolean       mStartRequested;
	private boolean       mSurfaceAvailable;
	private CameraSource  mCameraSource;

	public CameraSourcePreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mStartRequested = false;
		mSurfaceAvailable = false;

		mSurfaceView = new GLSurfaceView(context);
		mAugRenderer = new AugRenderer((AppCompatActivity) mContext);
		mSurfaceView.setRenderer(mAugRenderer);
		mSurfaceView.getHolder().addCallback(new SurfaceCallback());
		mAugRenderer.setActive(true);
		addView(mSurfaceView);
	}

	public void start(CameraSource cameraSource) throws IOException {
		if (cameraSource == null) {
			stop();
		}

		mCameraSource = cameraSource;

		if (mCameraSource != null) {
			mStartRequested = true;
			startIfReady();
		}
	}

	private void startIfReady() throws IOException {
		if (mStartRequested && mSurfaceAvailable) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			mCameraSource.start(mSurfaceView.getHolder());
			mStartRequested = false;
		}
	}

	public void stop() {
		if (mCameraSource != null) {
			mCameraSource.stop();
		}
	}

	public void release() {
		if (mCameraSource != null) {
			mCameraSource.release();
			mCameraSource = null;
		}
	}

	public void pause() {
		if (mSurfaceView != null) {
			mSurfaceView.onPause();
		}
	}

	public void resume() {
		if (mSurfaceView != null) {
			mSurfaceView.onResume();
		}
	}

	private class SurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder surface) {
			mSurfaceAvailable = true;
			try {
				startIfReady();
			} catch (IOException e) {
				Log.e(TAG, "Could not start camera source.", e);
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surface) {
			mSurfaceAvailable = false;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int width = 320;
		int height = 240;
		if (mCameraSource != null) {
			Size size = mCameraSource.getPreviewSize();
			if (size != null) {
				width = size.getWidth();
				height = size.getHeight();
			}
		}

		// Swap width and height sizes when in portrait, since it will be rotated 90 degrees
		if (isPortraitMode()) {
			int tmp = width;
			width = height;
			height = tmp;
		}

		final int layoutWidth = right - left;
		final int layoutHeight = bottom - top;

		// Computes height and width for potentially doing fit width.
		int childWidth = layoutWidth;
		int childHeight = (int) (((float) layoutWidth / (float) width) * height);

		// If height is too tall using fit width, does fit height instead.
		if (childHeight > layoutHeight) {
			childHeight = layoutHeight;
			childWidth = (int) (((float) layoutHeight / (float) height) * width);
		}

		for (int i = 0; i < getChildCount(); ++i) {
			getChildAt(i).layout(0, 0, childWidth, childHeight);
		}

		try {
			startIfReady();
		} catch (IOException e) {
			Log.e(TAG, "Could not start camera source.", e);
		}
	}

	private boolean isPortraitMode() {
		int orientation = mContext.getResources()
		                          .getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return false;
		}
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		}

		Log.d(TAG, "isPortraitMode returning false by default");
		return false;
	}
}
