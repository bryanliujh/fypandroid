package com.hiddenshrineoffline

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult


class SwarmScene(imageName: String, context: Context) : AugmentedImageNodeGroup() {
    private val imageName2 = imageName
    private val context2 = context
    override fun onInit() {
        //ArResources2.init(context2, imageName2)
        VideoAugmentedImageNode().init(anchorNode, this)
        VisitAugmentedImageNode(imageName2).init(anchorNode, this)
    }
}

class VideoAugmentedImageNode : AugmentedImageNode(ArResources.videoRenderable) {
    override fun initLayout() {
        super.initLayout()

        // the renderable is rectangle, so it have to scale to r
        val videoRatio = ArResources.videoPlayer.videoWidth.toFloat() / ArResources.videoPlayer.videoHeight

        offsetZ = (anchorNode.arHeight / 2.0f)

        // make video a little bigger to cover the while image
        scaledWidth *= 1.3f
        scaledHeight = scaledHeight * 1.3f / videoRatio
        scaledDeep = 1f
        localRotation = ArResources.viewRenderableRotation
    }

    override fun onActivate() {
        super.onActivate()

        if (!ArResources.videoPlayer.isPlaying) {
            ArResources.videoPlayer.start()
        }
    }

    override fun onDeactivate() {
        super.onDeactivate()

        if (ArResources.videoPlayer.isPlaying) {
            ArResources.videoPlayer.pause()
        }
    }
}


class VisitAugmentedImageNode(imageName: String) : AugmentedImageNode(ArResources.visitRenderable) {
    private val imageName2 = imageName
    override fun initLayout() {
        super.initLayout()

        // make it under
        offsetZ = anchorNode.arHeight / 2 + anchorNode.arHeight * 0.1f
    }

    override fun modifyLayout() {
        super.modifyLayout()

        localRotation = ArResources.viewRenderableRotation
    }

    override fun onTouchEvent(p0: HitTestResult?, p1: MotionEvent?): Boolean {
        val imageName3 = imageName2.split(".")[0]
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ntushrineproject.herokuapp.com/shrine_detail/" + imageName3))
        this.scene?.view?.context?.startActivity(intent)

        return false
    }
}


