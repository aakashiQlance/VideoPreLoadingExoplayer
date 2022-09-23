package com.codewithjudy.videopreloadingexoplayer.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.codewithjudy.videopreloadingexoplayer.R
import com.codewithjudy.videopreloadingexoplayer.VideoApp
import com.codewithjudy.videopreloadingexoplayer.VideoPreloadWorker
import com.codewithjudy.videopreloadingexoplayer.model.ExoPlayerItem
import com.codewithjudy.videopreloadingexoplayer.model.Videos
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.android.synthetic.main.item_reels_adapter.view.*

class ReelsAdapter(
    private val context: Context,
    private val videoList: ArrayList<Videos>,
    var videoPreparedListener: OnVideoPreparedListener
) :
    RecyclerView.Adapter<ReelsAdapter.ViewHolder>(), Player.Listener {


    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }

    class ViewHolder(
        private var item: View, var context: Context,
        var videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(item) {

        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource
        private lateinit var mCacheDataSourceFactory: DataSource.Factory
        private val cache: SimpleCache = VideoApp.cache

        fun setVideoPath(url: String) {

            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.i("content", "Can't play this video")
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        Log.i("content", "STATE_BUFFERING")
                    } else if (playbackState == Player.STATE_READY) {
                        Log.i("content", "STATE_READY")

                    }
                }
            })

            item.player_view.player = exoPlayer

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)


            mCacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(dataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            mediaSource = ProgressiveMediaSource.Factory(mCacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_reels_adapter, parent, false),
            context,
            videoPreparedListener
        )
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = videoList[position]

        holder.itemView.txtVideoName.text = model.videoName
        when (position) {
            0 -> {
                schedulePreloadWork(
                    videoList[position + 1].videoUrl,
                    videoList[position + 1].videoUrl
                )
            }
            videoList.size - 1 -> {
                schedulePreloadWork(
                    videoList[position - 1].videoUrl,
                    videoList[position - 1].videoUrl
                )
            }
            else -> {
                schedulePreloadWork(
                    videoList[position - 1].videoUrl,
                    videoList[position - 1].videoUrl
                )
                schedulePreloadWork(
                    videoList[position + 1].videoUrl,
                    videoList[position + 1].videoUrl
                )
            }
        }
        schedulePreloadWork(model.videoUrl, model.videoName)
        holder.setVideoPath(model.videoUrl)
    }


    private fun schedulePreloadWork(videoUrl: String, videoName: String) {
        val workManager = WorkManager.getInstance(context)
        val videoPreloadWorker = VideoPreloadWorker.buildWorkRequest(videoUrl, videoName)
        workManager.enqueueUniqueWork(
            "VideoPreloadWorker",
            ExistingWorkPolicy.KEEP,
            videoPreloadWorker
        )
    }
}