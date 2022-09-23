package com.codewithjudy.videopreloadingexoplayer.model

import com.google.android.exoplayer2.ExoPlayer

data class ExoPlayerItem(
    var exoPlayer: ExoPlayer,
    var position: Int
)