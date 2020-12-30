package com.km.cameraapp

import android.content.DialogInterface
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.km.cameraapp.databinding.DialogConfirmImageBinding
import com.km.cameraapp.databinding.DialogLastImageBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import java.io.File


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    lateinit var lastImage:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationsUtil.createNotificationChannel(this)

        val camera = findViewById<CameraView>(R.id.camera)
        val btnCapture = findViewById<ImageView>(R.id.btnCapture)
        lastImage = findViewById(R.id.lastImage)

        camera.setLifecycleOwner(this)

        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                result.toFile(File(cacheDir, System.currentTimeMillis().toString() + ".jpg"),
                    object : FileCallback {
                        override fun onFileReady(file: File?) {
                            file?.let {
                                Log.d(TAG, "onFileReady: " + file?.absolutePath)
                                confirmFileUpload(file)
                            }
                        }
                    })
            }
        })


        btnCapture.setOnClickListener {
            camera.takePicture()
        }
        lastImage.setOnClickListener {
            showLastImage()
        }

        refreshLastImage()
    }

    override fun onResume() {
        super.onResume()
        Prefs.getPrefs(this)?.registerOnSharedPreferenceChangeListener(sharedListener)
    }

    override fun onPause() {
        super.onPause()
        Prefs.getPrefs(this)?.unregisterOnSharedPreferenceChangeListener(sharedListener)
    }

    var sharedListener = OnSharedPreferenceChangeListener { prefs, key ->
        when(key){
            Prefs.LAST_IMAGE->{
                refreshLastImage()
            }
        }
    }

    fun refreshLastImage(){
        var lastimageUrl=Prefs.getStringData(this,Prefs.LAST_IMAGE)
        if(lastimageUrl.isNullOrEmpty()){
            return
        }
        Glide.with(this)
            .load(lastimageUrl)
            .override(128)
            .into(lastImage)
    }

    fun confirmFileUpload(file: File){
        var builder=AlertDialog.Builder(this)
        var dialogBinding = DialogConfirmImageBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogBinding.root)

        Glide.with(this)
            .load(file)
            .override(512)
            .into(dialogBinding.image)

        builder.setPositiveButton("Upload", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
                MyUploadService.startUploadFile(this@MainActivity,file)
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }
        })
        var dialog=builder.create()

        dialog.show()
    }

    fun showLastImage(){
        var lastimageUrl=Prefs.getStringData(this,Prefs.LAST_IMAGE)
        if(lastimageUrl.isNullOrEmpty()){
            return
        }

        var builder=AlertDialog.Builder(this)
        var dialogBinding = DialogLastImageBinding.inflate(LayoutInflater.from(this))
        builder.setView(dialogBinding.root)

        Glide.with(this)
            .load(lastimageUrl)
            .override(512)
            .into(dialogBinding.image)

        builder.setNegativeButton("Dismiss", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.dismiss()
            }
        })
        var dialog=builder.create()

        dialog.show()
    }

}