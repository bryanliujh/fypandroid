package com.hiddenshrineoffline

class SwarmAnchorNode(imageName: String): AugmentedImageAnchorNode() {
    override val imageWidth: Float = 1F // 100 cm
    override val imageHeight: Float = 0.6667f // 66.7 cm

    private val sceneList = mutableListOf<AugmentedImageNodeGroup>()
    private var currentSceneIndex = 0
    private val imageName2 = imageName

    override fun onInit() {
        sceneList.add(SwarmScene(imageName2).init(this))
    }

    override fun onActivate() {
        super.onActivate()

    }


}