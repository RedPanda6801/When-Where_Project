package com.example.whenandwhere

data class editplaceClass(
    val userName: String,
    var departurePlace: String,
    var isCarIcon: Boolean
){
    fun updateDeparturePlace(place: String) {
        departurePlace = place
    }
}

