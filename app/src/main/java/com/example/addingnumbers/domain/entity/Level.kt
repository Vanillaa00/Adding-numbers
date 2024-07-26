package com.example.addingnumbers.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
enum class Level: Parcelable {

    TEST, EASY, MEDIUM, HARD
}