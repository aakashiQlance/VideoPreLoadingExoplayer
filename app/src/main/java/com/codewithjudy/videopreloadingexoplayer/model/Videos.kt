package com.codewithjudy.videopreloadingexoplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Videos(val videoName: String, val videoUrl: String) : Parcelable