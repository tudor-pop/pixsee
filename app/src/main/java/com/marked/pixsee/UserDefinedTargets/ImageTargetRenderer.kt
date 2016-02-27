/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.marked.pixsee.UserDefinedTargets

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.marked.pixsee.VuforiaApplication.ApplicationSession
import com.marked.pixsee.VuforiaApplication.utils.SampleMath
import com.marked.pixsee.VuforiaApplication.utils.SampleUtils
import com.qualcomm.vuforia.Renderer
import com.qualcomm.vuforia.Tool
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION
import com.threed.jpct.*
import com.threed.jpct.util.MemoryHelper
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// The renderer class for the ImageTargetsBuilder sample.
class ImageTargetRenderer(// Reference to main activity
		private val mActivity: ImageTargets, private val mVuforiaAppSession: ApplicationSession) : GLSurfaceView.Renderer {
	var mIsActive = false
	private var modelViewMat: FloatArray? = null

	private val mTextureManager: TextureManager
	private var fb: FrameBuffer? = null
	private val mWorld: World
	private val mSun: Light
	private var cam: Camera? = null
	private val fov: Float = 0.toFloat()
	private val fovy: Float = 0.toFloat()

	init {
		this.mTextureManager = TextureManager.getInstance()
		this.mWorld = World()
		this.mSun = Light(mWorld)
		mWorld.setAmbientLight(25, 25, 25)
		mSun.setIntensity(250f, 250f, 250f)
		try {
			// keep the texture small(ideal under 1mb; if it's too big won't keep up with the program because the texture is loaded
			// on an background thread so it must be easy to load the texture) or compress any texture online
			// it's also recommended to be a power of 2. 2^x width/height
			var texture: Texture
			if (!mTextureManager.containsTexture("bourak")) {
				texture = Texture(mActivity.assets.open("bourak_3ds/bourak.jpg"))
				mTextureManager.addTexture("bourak", texture)
			}
			if (!mTextureManager.containsTexture("box")) {
				texture = Texture(64, 64, RGBColor.WHITE)
				mTextureManager.addTexture("box", texture)
			}

		} catch (e: IOException) {
			e.printStackTrace()
		}

		val loadOBJ: Object3D

		try {
			loadOBJ = loadModel(mActivity.assets.open("bourak_3ds/bourak.3DS"), 0.008.toFloat())
			loadOBJ.setTexture("bourak")
			loadOBJ.build()

			mWorld.addObject(loadOBJ)
			cam = mWorld.camera
			cam!!.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
			cam!!.lookAt(loadOBJ.transformedCenter)

			val sv = SimpleVector()
			sv.set(loadOBJ.transformedCenter)
			sv.y += 100f
			sv.z += 100f
			mSun.position = sv

		} catch (e: Exception) {
			Log.e("**", e.stackTrace.toString())

			val box = Primitives.getCube(10.0f)
			box.calcTextureWrapSpherical()
			box.setTexture("box")
			box.strip()
			box.build()

			mWorld.addObject(box)
			cam = mWorld.camera
			cam!!.moveCamera(Camera.CAMERA_MOVEOUT, 50f)
			cam!!.lookAt(box.transformedCenter)

			val sv = SimpleVector()
			sv.set(box.transformedCenter)
			sv.y += 100f
			sv.z += 100f
			mSun.position = sv
		}


		MemoryHelper.compact()
	}

	// Called when the surface is created or recreated.
	@SuppressLint("LongLogTag")
	override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceCreated")
		// Call Vuforia function to (re)initialize rendering after first use
		// or after OpenGL ES context was lost (e.g. after onPause/onResume):
		mVuforiaAppSession.onSurfaceCreated()
	}

	// Called when the surface changed size.
	@SuppressLint("LongLogTag")
	override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
		// Call function to update rendering when render surface
		// parameters have changed:

		// Call Vuforia function to handle render surface size changes:
		mActivity.updateRendering()
		mVuforiaAppSession.onSurfaceChanged(width, height)

		if (fb != null) {
			fb!!.dispose()
		}
		fb = com.threed.jpct.FrameBuffer(width, height)
		Config.viewportOffsetAffectsRenderTarget = true

	}

	// Called to draw the current frame.
	override fun onDrawFrame(gl: GL10) {
		if (!mIsActive)
			return
		// Call our function to render content
		renderFrame()
		updateCamera()
	}

	fun drawWorld() {
		try {
			mWorld.renderScene(fb)
			mWorld.draw(fb)
			fb!!.display()
		} catch (e: Exception) {
			e.printStackTrace()
		}

	}

	private fun renderFrame() {
		// Clear color and depth buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

		// Get the state from Vuforia and mark the beginning of a rendering
		// section
		val state = Renderer.getInstance().begin()
		// Explicitly render the Video Background
		Renderer.getInstance().drawVideoBackground()
		GLES20.glEnable(GLES20.GL_DEPTH_TEST)
		GLES20.glEnable(GLES20.GL_CULL_FACE)
		if (Renderer.getInstance().videoBackgroundConfig.reflection == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
			GLES20.glFrontFace(GLES20.GL_CW) // Front camera
		else
			GLES20.glFrontFace(GLES20.GL_CCW) // Back camera

		// Render the RefFree UI elements depending on the current state
		mActivity.refFreeFrame!!.render()
		// Did we find any trackables this frame?
		for (tIdx in 0..state.numTrackableResults - 1) {
			// Get the trackable:
			val trackableResult = state.getTrackableResult(tIdx)
			val modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(trackableResult.pose)
			val modelViewMatrix = modelViewMatrix_Vuforia.data

			val angle = 90f
			val modelViewProjection = FloatArray(16)
			Matrix.translateM(modelViewMatrix, 0, 0f, 0f, kObjectScale)
			Matrix.rotateM(modelViewMatrix, 0, angle, 0f, 0f, kObjectScale)
			Matrix.scaleM(modelViewMatrix, 0, kObjectScale, kObjectScale, kObjectScale)
			Matrix.multiplyMM(modelViewProjection, 0, mVuforiaAppSession.projectionMatrix.data, 0, modelViewMatrix,
					0)
			modelViewMatrix_Vuforia.data = modelViewMatrix

			val inverseMV = SampleMath.Matrix44FInverse(modelViewMatrix_Vuforia)
			val invTranspMV = SampleMath.Matrix44FTranspose(inverseMV)
			updateModelviewMatrix(invTranspMV.data)

			// hide the objects when the targets are not detected
			if (state.numTrackableResults == 0) {
				val m = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, -10000f, 1f)
				updateModelviewMatrix(m)
			}

			drawWorld()
			SampleUtils.checkGLError("UserDefinedTargets renderFrame")
		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST)
		Renderer.getInstance().end()
	}

	fun updateCamera() {
		if (modelViewMat != null) {
			val m = modelViewMat!!
			val camUp: SimpleVector

			if (mActivity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
				camUp = SimpleVector(-m[0], -m[1], -m[2])
			else
				camUp = SimpleVector(-m[4], -m[5], -m[6])

			val camDirection = SimpleVector(m[8], m[9], m[10])
			val camPosition = SimpleVector(m[12], m[13], m[14])

			cam!!.setOrientation(camDirection, camUp)
			cam!!.position = camPosition

			cam!!.fov = fov
			cam!!.yfov = fovy
		}
	}

	fun updateModelviewMatrix(mat: FloatArray) {
		modelViewMat = mat
	}

	@Throws(FileNotFoundException::class)
	private fun loadModel(filename: InputStream, scale: Float): Object3D {
		val model = Loader.load3DS(BufferedInputStream(filename), scale)
		var o3d = Object3D(0)
		var temp: Object3D? = null
		for (i in model.indices) {
			temp = model[i]
			temp!!.center = SimpleVector.ORIGIN
			temp.rotateX((.4 * Math.PI).toFloat())
			temp.rotateY((.1 * Math.PI).toFloat())
			temp.rotateMesh()
			temp.rotationMatrix = com.threed.jpct.Matrix()
			o3d = Object3D.mergeObjects(o3d, temp)
			o3d.build()
		}
		return o3d
	}

	companion object {
		// Constants:
		internal val kObjectScale = 2.0f
		private val LOGTAG = "ImageTargetRenderer"
	}
}
