package com.example.mapsapp.viewmodel

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.data.LocationRepository
import com.example.mapsapp.ui.screen.LocationField
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class MapViewModel(
    private val repository: LocationRepository
): ViewModel() {
    private val _currentAddress =
        MutableStateFlow("Fetching Location...")

    val currentAddress: StateFlow<String> = _currentAddress

    private val _realAddress = MutableStateFlow("")
    val realAddress: StateFlow<String> = _realAddress

    private val _currentLatLng = MutableStateFlow<LatLng?>(null)
    val currentLatLng: StateFlow<LatLng?> = _currentLatLng
    private val _pickupAddress = MutableStateFlow("")
    val pickupAddress: StateFlow<String> = _pickupAddress

    private val _dropAddress = MutableStateFlow("")
    val dropAddress: StateFlow<String> = _dropAddress

    private val _editingField =
        MutableStateFlow(LocationField.DROP)
    val editingField: StateFlow<LocationField> = _editingField

    fun setEditingField(field: LocationField){
        _editingField.value = field
    }

    fun getCurrentLocation(onResult: (LatLng?) -> Unit){
        viewModelScope.launch {
            val location = repository.getCurrentLocation()
            _currentLatLng.value = location
            onResult(location)
        }
    }

    fun updateAddress(latLng: LatLng){
        viewModelScope.launch {
            val address = repository.getAddress(latLng)
            _realAddress.value = address

            val current = _currentLatLng.value

            if (current != null && isSameLocation(current, latLng)){
                _currentAddress.value = "Current Location"
            } else {
                _currentAddress.value = address
            }
        }
    }

    private fun isSameLocation(a: LatLng, b: LatLng): Boolean {
        val threshold = 0.0001
        return abs(a.latitude - b.latitude) < threshold &&
                abs(a.longitude - b.longitude) < threshold
    }

    fun setPickup(address: String){
        _pickupAddress.value = address
    }

    fun setDrop(address: String){
        _dropAddress.value = address
    }
}