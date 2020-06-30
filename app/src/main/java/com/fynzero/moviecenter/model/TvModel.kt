package com.fynzero.moviecenter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TvModel(
    var id: Int = 0,
    var name: String = "",
    var poster: String = "",
    var backdrop: String = "",
    var date: String = "",
    var rating: Double = 0.0,
    var overview: String = ""
) : Parcelable