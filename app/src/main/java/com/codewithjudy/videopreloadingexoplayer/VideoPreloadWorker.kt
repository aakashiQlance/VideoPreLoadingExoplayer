package com.codewithjudy.videopreloadingexoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.*

class VideoPreloadWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private var videoCachingJob: Job? = null
    private lateinit var mHttpDataSourceFactory: HttpDataSource.Factory
    private lateinit var mDefaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var mCacheDataSource: CacheDataSource
    private val cache: SimpleCache = VideoApp.cache

    companion object {
        const val VIDEO_URL = "video_url"

        fun buildWorkRequest(yourParameter: String, videoName: String): OneTimeWorkRequest {
            val data =
                Data.Builder().putString(VIDEO_URL, yourParameter).putString("videoName", videoName)
                    .build()
            return OneTimeWorkRequestBuilder<VideoPreloadWorker>().apply { setInputData(data) }
                .build()
        }
    }


    override fun doWork(): Result {
        try {

            val videoUrl: String? = inputData.getString(VIDEO_URL)
            val videoName = inputData.getString("videoName")

            //{ BoilerPlate Code
            mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)

            mDefaultDataSourceFactory = DefaultDataSourceFactory(context, mHttpDataSourceFactory)

            mCacheDataSource = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                .createDataSource()
            //}
            preCacheVideo(videoUrl, videoName)

            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private var downloadPercentage: Double = 0.0

    private fun preCacheVideo(videoUrl: String?, videoName: String?) {

        if(downloadPercentage.toInt() == 100){
            return
        }
        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)


        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            downloadPercentage = (bytesCached * 100.0 / requestLength)
            // Do Something
            Log.i("downloadPercent", "$videoName: $downloadPercentage percent")
            Log.i("size", "${cache.cacheSpace.toInt()}")
        }


        //Recursion call
        videoCachingJob = GlobalScope.launch(Dispatchers.IO) {
            cacheVideo(dataSpec, progressListener)
            preCacheVideo(videoUrl, videoName)
        }
    }

    private fun cacheVideo(mDataSpec: DataSpec, mProgressListener: CacheWriter.ProgressListener) {
        runCatching {
            CacheWriter(
                mCacheDataSource,
                mDataSpec,
                null,
                mProgressListener,
            ).cache()
        }.onFailure {
            it.printStackTrace()
        }
    }
}