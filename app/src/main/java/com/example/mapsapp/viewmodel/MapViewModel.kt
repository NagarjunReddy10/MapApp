package com.example.mapsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.data.LocationRepository
import com.example.mapsapp.ui.screen.LocationField
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: LocationRepository
): ViewModel() {
    private val _currentAddress =
        MutableStateFlow("Detecting Location...")

    val currentAddress: StateFlow<String> = _currentAddress

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

    fun updateAddress(latLng: LatLng){
        viewModelScope.launch{
            _currentAddress.value =
                repository.getAddress(latLng)
        }
    }
    fun getCurrentLocation(onResult: (LatLng?) -> Unit){
        viewModelScope.launch {
            onResult(repository.getCurrentLocation())
        }
    }

    fun setPickup(address: String){
        _pickupAddress.value = address
    }

    fun setDrop(address: String){
        _dropAddress.value = address
    }
}