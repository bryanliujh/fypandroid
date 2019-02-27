package com.hiddenshrineoffline

class SwarmAnchorNode: AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    private val sceneList = mutableListOf<AugmentedImageNodeGroup>()
    private var currentSceneIndex = 0

    override fun onInit() {
        sceneList.add(SwarmScene().init(this))
    }

    override fun onActivate() {
        super.onActivate()

    }


}