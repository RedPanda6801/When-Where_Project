package com.example.whenandwhere

import android.os.Parcel
import android.os.Parcelable

data class MemberClass(
    val id : Int,
    val userId: String,
    val nickname: String
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(userId)
        parcel.writeString(nickname)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberClass>{
        override fun createFromParcel(parcel: Parcel): MemberClass {
            return MemberClass(parcel)
        }

        override fun newArray(size: Int): Array<MemberClass?> {
            return arrayOfNulls(size)
        }
    }
}
