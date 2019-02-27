package com.hiddenshrineoffline

import android.content.Context

class BetaSwarmAnchorNode(imageName: String, context: Context): AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    private val sceneList = mutableListOf<AugmentedImageNodeGroup>()
    private var currentSceneIndex = 0
    private val imageName2 = imageName
    private val context2 = context

    override fun onInit() {
        sceneList.add(BetaSwarmScene(imageName2, context2).init(this))
    }

    override fun onActivate() {
        super.onActivate()

    }


}