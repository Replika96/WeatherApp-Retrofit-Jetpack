package com.tazmin.weather.retrofit

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Current(
    val temp_c: Double,
    val condition: Condition,
    val wind_kph: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

