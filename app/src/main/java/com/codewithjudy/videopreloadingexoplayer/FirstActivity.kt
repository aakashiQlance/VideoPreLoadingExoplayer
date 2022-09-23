package com.codewithjudy.videopreloadingexoplayer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.codewithjudy.videopreloadingexoplayer.model.Videos
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {


    private val videoUrl1 =
        "https://firebasestorage.googleapis.com/v0/b/testi-30703.appspot.com/o/Android%20Kotlin%20Developer%20-%20Wake%20Up%2C%20Aleks!%201.mkv?alt=media&token=251ab4ab-284c-4820-9d5c-09d656bc8739"

    private val videoUrl2 =
        "https://firebasestorage.googleapis.com/v0/b/lol-videos-8dc74.appspot.com/o/Blog_Images%2Fvideo%3A10142?alt=media&token=9f7734fa-f714-4838-bd65-8a4d594ec2ce"

    private val videoUrl3 =
        "https://firebasestorage.googleapis.com/v0/b/reelsdemo-2fb0f.appspot.com/o/CGI%20Animation%20Of%20Space.mp4?alt=media&token=b8072c2f-ec57-4d15-8fc2-809a64614c22"

    private val videoUrl4 =
        "https://firebasestorage.googleapis.com/v0/b/reelsdemo-2fb0f.appspot.com/o/pexels-rostislav-uzunov-7385122.mp4?alt=media&token=2a4d606e-c357-46f6-a0ca-2a02ad4018c8"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        var videoList = ArrayList<Videos>()
        videoList.add(Videos("video1", videoUrl1))
        videoList.add(Videos("video2", videoUrl2))
        videoList.add(Videos("video3", videoUrl3))
        videoList.add(Videos("video4", videoUrl4))



        btnShowReels.setOnClickListener {
                schedulePreloadWork(videoList[0].videoUrl,videoList[0].videoName)
            startActivity(Intent(this, ReelsActivity::class.java).putExtra("video_list", videoList))
        }

    }

    private fun schedulePreloadWork(videoUrl: String, videoName: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val videoPreloadWorker = VideoPreloadWorker.buildWorkRequest(videoUrl, videoName)
        workManager.enqueueUniqueWork(
            "VideoPreloadWorker",
            ExistingWorkPolicy.KEEP,
            videoPreloadWorker
        )
    }
}