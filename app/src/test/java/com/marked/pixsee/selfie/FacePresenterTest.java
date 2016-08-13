package com.marked.pixsee.selfie;

import android.graphics.SurfaceTexture;

import com.marked.pixsee.selfie.custom.CameraSource;

import org.jetbrains.annotations.Contract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.rajawali3d.renderer.Renderer;

import static com.marked.pixsee.selfie.custom.CameraSource.PictureCallback;
import static com.marked.pixsee.selfie.custom.CameraSource.ShutterCallback;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Tudor on 12-Aug-16.
 */
public class FacePresenterTest {
	@Mock
	SelfieContract.View mView;
	@Mock
	Renderer mRenderer;
	@Mock
	CameraSource mCameraSource;

	private FacePresenter mFacePresenter;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		mFacePresenter = new FacePresenter(mView, mRenderer, mCameraSource);
		Mockito.doNothing().when(mCameraSource).stop();
	}

	@Test
	public void viewShouldHavePresenter() throws Exception {
		verify(mView).setPresenter(mFacePresenter);
	}

	@Test
	public void takePicture_shouldCallCameraSourceTakePicture() throws Exception {
		mFacePresenter.takePicture();

		verify(mView).displayEmojiActions(false);
		verify(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));
	}

	@Test
	public void takePicture_shouldFreezeCamera() throws Exception {
		doAnswer(callTakePictureCallbacks()).when(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));

		mFacePresenter.takePicture();

		verify(mCameraSource).stop();
	}

	@Test
	public void takePicture_shouldShowActions() throws Exception {
		doAnswer(callTakePictureCallbacks()).when(mCameraSource).takePicture(any(ShutterCallback.class), any(PictureCallback.class));

		mFacePresenter.takePicture();

		verify(mView).showTakenPictureActions();
	}

	@Test
	public void nullSurfaceTexture_shouldNotStartCamera() throws Exception {
		mFacePresenter.resumeSelfie();

		verify(mView).displayEmojiActions(true);
		verify(mCameraSource, never()).start(any(SurfaceTexture.class));
	}

	@Test
	public void onCameraAvailable() throws Exception {
		mFacePresenter.onAvailableCameraSurfaceTexture(mock(SurfaceTexture.class));

		verify(mCameraSource).start(any(SurfaceTexture.class));
	}

	/*****************
	 * DSL
	 *******************/
	@Contract(" -> !null")
	private Answer callTakePictureCallbacks() {
		return new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ShutterCallback shutterCallback = (ShutterCallback) invocation.getArguments()[0];
				PictureCallback pictureCallback = (PictureCallback) invocation.getArguments()[1];
				shutterCallback.onShutter();
				pictureCallback.onPictureTaken(new byte[]{});
				return null;
			}
		};
	}
}