package com.fynzero.moviecenter.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieModel(
    var id: Int = 0,
    var title: String = "",
    var poster: String = "",
    var backdrop: String = "",
    var date: String = "",
    var rating: Double = 0.0,
    var overview: String = ""
) : Parcelable