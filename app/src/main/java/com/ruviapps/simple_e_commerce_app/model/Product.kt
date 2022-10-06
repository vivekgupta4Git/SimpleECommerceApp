package com.ruviapps.simple_e_commerce_app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val id : Int,
    val title : String?,
    val description : String?,
    val price : Int,
    val discountPercentage : Float,
    val rating : Float,
    val stock : Int,
    val brand : String,
    val category : String?
) : Parcelable
