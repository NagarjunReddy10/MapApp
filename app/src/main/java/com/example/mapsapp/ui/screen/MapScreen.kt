package com.example.mapsapp.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.ui.components.BottomSearchBar
import com.example.mapsapp.ui.components.MyLocationButton
import com.example.mapsapp.ui.components.TopLocationBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun MapScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentAddress by remember { mutableStateOf("Detecting Location...") }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
           scope.launch {
               animateToCurrentLocation(fusedLocationClient, cameraPositionState)
           }
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val geocoder = Geocoder(context, Locale.getDefault())

    LaunchedEffect(cameraPositionState.isMoving) {
        if(!cameraPositionState.isMoving){
            val center = cameraPositionState.position.target

            scope.launch {
                try {
                    val addresses = geocoder.getFromLocation(
                        center.latitude,
                        center.longitude,
                        1
                    )
                    currentAddress =
                        addresses?.firstOrNull()?.getAddressLine(0) ?: "Current Location"
                } catch (e: Exception) {
                    currentAddress = "Unable to fetch address"
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false
            )
        )
        Icon(Icons.Default.LocationOn, contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.align(Alignment.Center).size(40.dp)
            )

        TopLocationBar(
            address = currentAddress
        )

        BottomSearchBar {
            navController.navigate("drop")
        }

        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp)
        ){
            MyLocationButton {
                animateToCurrentLocation(
                    fusedLocationClient,
                    cameraPositionState
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun animateToCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState
){
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }
}