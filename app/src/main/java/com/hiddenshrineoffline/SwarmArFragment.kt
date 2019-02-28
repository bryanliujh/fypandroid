package com.hiddenshrineoffline

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.ux.ArFragment
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class SwarmArFragment : ArFragment() {
    private val trackableMap = mutableMapOf<String, AugmentedImageAnchorNode>()

    var setOnStarted: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view!!.visibility = View.GONE

        // Turn off the plane discovery since we're only looking for ArImages
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        arSceneView.scene.addOnUpdateListener(::onUpdateFrame)


        ArResources.init(this.context!!).handle { _, _ ->
            setOnStarted?.invoke()

            view.visibility = View.VISIBLE
        }

        return view
    }

    override fun onPause() {
        super.onPause()

        trackableMap.forEach {
            arSceneView.scene.removeChild(it.value)
        }


        trackableMap.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        trackableMap.forEach {
            arSceneView.scene.removeChild(it.value)
        }


        trackableMap.clear()
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO

        //config.augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, context!!.resources.assets.open("ar.imgdb"))
        // or you can build image database real time


        val db = AugmentedImageDatabase(session)
        val Files = getPrivateAlbumStorageDir(context, "ShrineAlbum").listFiles();
        for (file in Files){
            Log.d("Files", "FileName:" + file.name);
            val augmentedImageBitmap = loadAugmentedImageBitmap(file.name)
            db.addImage(file.name, augmentedImageBitmap)
        }
        config.augmentedImageDatabase = db

        return config
    }



    private fun loadAugmentedImageBitmap(shrineUUID: String): Bitmap? {

        val myDir = File(getPrivateAlbumStorageDir(context, "ShrineAlbum"), shrineUUID)

        if (myDir != null) {
            try {
                FileInputStream(myDir).use { `is` -> return BitmapFactory.decodeStream(`is`) }
            } catch (e: IOException) {
                Log.e("cannot load bitmap", "IO exception loading augmented image bitmap.", e)
            }

        }
        return null

    }

    fun getPrivateAlbumStorageDir(context: Context?, albumName: String): File {
        // Get the directory for the app's private pictures directory.
        val file = File(context!!.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName)
        if (!file.mkdirs()) {
            Log.e("Warning1", "Directory not created")
        }
        return file
    }


    private fun createArNode(image: AugmentedImage) {
        Logger.d("create : ${image.name}(${image.index}), pose: ${image.centerPose}, ex: ${image.extentX}, ez: ${image.extentZ}")

        val node = SwarmAnchorNode(image.name, this.context!!).init(image)
        trackableMap[image.name] = node
        arSceneView.scene.addChild(node)

        Toast.makeText(context, "add video", Toast.LENGTH_LONG).show()

    }

    private fun onUpdateFrame(@Suppress("UNUSED_PARAMETER") frameTime: FrameTime?) {
        val frame = arSceneView.arFrame

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        frame.getUpdatedTrackables(AugmentedImage::class.java).forEach { image ->
            when (image.trackingState) {
                TrackingState.TRACKING -> if (trackableMap.contains(image.name)) {
                    if (trackableMap[image.name]?.update(image) == true) {
                        Logger.d("update node: ${image.name}(${image.index}), pose: ${image.centerPose}, ex: ${image.extentX}, ez: ${image.extentZ}")
                    }
                } else {
                    createArNode(image)
                }
                TrackingState.STOPPED -> {
                    Logger.d("remove node: ${image.name}(${image.index})")

                    trackableMap.remove(image.name)
                }
                else -> {
                }
            }
        }
    }
}