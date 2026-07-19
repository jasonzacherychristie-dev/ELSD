package com.elsd.bus

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

/**
 * WORLD bus — CameraX → SurfaceTexture (GL_TEXTURE_EXTERNAL_OES).
 */
class CameraBus(
    private val context: Context,
) {
    private val executor = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null
    private var bound = false

    @SuppressLint("UnsafeOptInUsageError")
    fun start(surfaceTexture: SurfaceTexture, onFrame: () -> Unit) {
        surfaceTexture.setOnFrameAvailableListener { onFrame() }
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            try {
                val p = future.get()
                provider = p
                p.unbindAll()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider { request ->
                        val st = surfaceTexture
                        // Match camera buffer size when known
                        val res = request.resolution
                        st.setDefaultBufferSize(res.width, res.height)
                        val surface = Surface(st)
                        request.provideSurface(surface, executor) {
                            surface.release()
                        }
                    }
                }
                val owner = context as LifecycleOwner
                p.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                bound = true
                Log.i(TAG, "WORLD bus live")
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun resume() {
        // LifecycleOwner binding handles most cases
    }

    fun pause() {
        // keep provider; unbind on stop
    }

    fun stop() {
        provider?.unbindAll()
        bound = false
    }

    companion object {
        private const val TAG = "ELSD.CameraBus"
    }
}
