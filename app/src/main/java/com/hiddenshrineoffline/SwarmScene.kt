package com.hiddenshrineoffline

import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult


class SwarmScene : AugmentedImageNodeGroup() {
    override fun onInit() {
        VideoAugmentedImageNode().init(anchorNode, this)
        VisitAugmentedImageNode().init(anchorNode, this)
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


class VisitAugmentedImageNode : AugmentedImageNode(ArResources.visitRenderable) {
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ntuhiddenshrine.herokuapp.com/shrine_detail/f253721a-50d6-4db2-b9ce-86a29df29a52"))
        this.scene?.view?.context?.startActivity(intent)

        return false
    }
}