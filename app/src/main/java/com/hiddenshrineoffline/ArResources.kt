package com.hiddenshrineoffline

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import java.util.concurrent.CompletableFuture

object ArResources {
    fun init(context: Context): CompletableFuture<Void> {


        val texture = ExternalTexture()
        //Logger.d(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).path + "/e47edd5f-9668-4085-9541-c8d61141c385.mp4")
        //videoPlayer = MediaPlayer.create(context, R.raw.video)
        videoPlayer = MediaPlayer.create(context, Uri.parse(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).path + "/e47edd5f-9668-4085-9541-c8d61141c385.mp4"))
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