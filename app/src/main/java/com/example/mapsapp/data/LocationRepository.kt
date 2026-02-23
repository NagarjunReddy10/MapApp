package com.example.mapsapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationRepository (private val context: Context){

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val geocoder =
        Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception){
            null
        }
    }

    suspend fun getAddress(latLng: LatLng): String{
        return try {
            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
            addresses?.firstOrNull()?.getAddressLine(0)
                ?: "Current Location"
        } catch (e: Exception){
            "Unable to fetch address"
        }
    }
}