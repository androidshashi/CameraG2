package com.androidshashi.camerag2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Bundle
import android.util.Size
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidshashi.camerag2.databinding.ActivityCamera2Binding


class Camera2Activity : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    private lateinit var binding: ActivityCamera2Binding

    private var cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

    private lateinit var cameraCharacteristics: CameraCharacteristics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamera2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            initCamera()
        }
    }

    private fun configSurfaceView(){
        val config: StreamConfigurationMap? =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val sizesForJPEG: Array<Size> = config!!.getOutputSizes(ImageFormat.JPEG)

        var bestFitJPEG = 0
        for (i in sizesForJPEG.indices) {
            if (sizesForJPEG[i].getWidth() <= 500 && sizesForJPEG[i].getHeight() <= 500) {
                bestFitJPEG = i
                break
            }
        }
        val imageReader = ImageReader.newInstance(
            sizesForJPEG[bestFitJPEG].getWidth(),
            sizesForJPEG[bestFitJPEG].getHeight(),
            ImageFormat.JPEG,
            3
        )

        val surfaceView = binding.surfaceView
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val surfaceViewLayoutParams =
                    surfaceView.layoutParams as ConstraintLayout.LayoutParams
                surfaceViewLayoutParams.width = sizesForJPEG[0].getWidth()
                surfaceViewLayoutParams.height = sizesForJPEG[0].getHeight()
                surfaceView.layoutParams = surfaceViewLayoutParams
                // When using a SurfaceView make sure to open a session only after the surface is created.
                
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera()
            } else {
                // Handle permission denial

            }
        }
    }

    @SuppressLint("MissingPermission")
    fun initCamera(){
        val cameraIdList = cameraManager.cameraIdList

        var cameraId: String? = "0"

        for (currentCameraId in cameraIdList) {
            cameraCharacteristics = cameraManager.getCameraCharacteristics(currentCameraId!!)
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK) {
                cameraId = currentCameraId
                break
            }
        }
        // configuration of surface view
        configSurfaceView()

        cameraManager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {

            override fun onOpened(camera: CameraDevice) {

            }

            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {

            }
        }, null)
    }
}
