package com.hiddenshrineoffline

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import java.util.concurrent.CompletableFuture


class BetaSwarmScene(imageName: String, context: Context) : AugmentedImageNodeGroup() {
    private val imageName2 = imageName
    private val context2 = context
    override fun onInit() {
        ArResources2.init(context2, imageName2)
        BetaVideoAugmentedImageNode().init(anchorNode, this)
        BetaVisitAugmentedImageNode(imageName2).init(anchorNode, this)
    }
}

class BetaVideoAugmentedImageNode : AugmentedImageNode(ArResources2.videoRenderable) {
    override fun initLayout() {
        super.initLayout()

        // the renderable is rectangle, so it have to scale to r
        val videoRatio = ArResources2.videoPlayer.videoWidth.toFloat() / ArResources2.videoPlayer.videoHeight

        offsetZ = (anchorNode.arHeight / 2.0f)

        // make video a little bigger to cover the while image
        scaledWidth *= 1.3f
        scaledHeight = scaledHeight * 1.3f / videoRatio
        scaledDeep = 1f
        localRotation = ArResources2.viewRenderableRotation
    }

    override fun onActivate() {
        super.onActivate()

        if (!ArResources2.videoPlayer.isPlaying) {
            ArResources2.videoPlayer.start()
        }
    }

    override fun onDeactivate() {
        super.onDeactivate()

        if (ArResources2.videoPlayer.isPlaying) {
            ArResources2.videoPlayer.pause()
        }
    }
}


class BetaVisitAugmentedImageNode(imageName: String) : AugmentedImageNode(ArResources2.visitRenderable) {
    private val imageName2 = imageName
    override fun initLayout() {
        super.initLayout()

        // make it under
        offsetZ = anchorNode.arHeight / 2 + anchorNode.arHeight * 0.1f
    }

    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = ArResources2.viewRenderableRotation
    }

    override fun onTouchEvent(p0: HitTestResult?, p1: MotionEvent?): Boolean {
        val imageName3 = imageName2.split(".")[0]
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ntuhiddenshrine.herokuapp.com/shrine_detail/" + imageName3))
        this.scene?.view?.context?.startActivity(intent)

        return false
    }
}



object ArResources2 {
    fun init(context: Context, imageName: String): CompletableFuture<Void> {


        val texture = ExternalTexture()
        //Logger.d(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).path + "/e47edd5f-9668-4085-9541-c8d61141c385.mp4")
        //videoPlayer = MediaPlayer.create(context, R.raw.video)
        //videoPlayer = MediaPlayer.create(context, Uri.parse(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).path + "/bbe49e25-aa09-499b-97d7-61ce62e89db6.mp4"))
        val imageName2 = imageName.split(".")[0]
        try {
            videoPlayer = MediaPlayer.create(context, Uri.parse(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).path + "/" + imageName2 + ".mp4"))
        } catch (e: Exception){
            videoPlayer = MediaPlayer.create(context, R.raw.video)
        }
        videoPlayer.setSurface(texture.surface)
        videoPlayer.isLooping = true

        videoRenderable = ModelRenderable.builder().setSource(context, com.google.ar.sceneform.rendering.R.raw.sceneform_view_renderable).build().also {
            it.thenAccept { renderable ->
                renderable.material.setExternalTexture("viewTexture", texture)
            }
        }


        visitRenderable = ViewRenderable.builder().setView(context, R.layout.view_visit)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.TOP)
                .build()

        return CompletableFuture.allOf(
                videoRenderable
        )
    }

    lateinit var videoPlayer: MediaPlayer

    lateinit var videoRenderable: CompletableFuture<ModelRenderable>

    lateinit var visitRenderable: CompletableFuture<ViewRenderable>


    val viewRenderableRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)
}
