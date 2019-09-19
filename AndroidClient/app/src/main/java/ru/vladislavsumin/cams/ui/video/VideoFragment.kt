package ru.vladislavsumin.cams.ui.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.annotation.AnyThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_video.*
import ru.vladislavsumin.cams.R

class VideoFragment : Fragment() {
    private val play: MutableLiveData<Uri> = MutableLiveData()
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaController = MediaController(context)
        video_container.setMediaController(mediaController)
        mediaController.setAnchorView(video_container)

        video_container.setOnPreparedListener { mp ->
            val width = mp.videoWidth
            val height = mp.videoHeight
            video_container.setVideoSize(width, height)
        }
    }

    override fun onStart() {
        super.onStart()
        play.observe(this, Observer {
            video_container.setVideoURI(it)
            video_container.requestFocus()
            video_container.start()
        })
    }

    @AnyThread
    fun playVideo(uri: Uri) {
        play.postValue(uri)
    }
}