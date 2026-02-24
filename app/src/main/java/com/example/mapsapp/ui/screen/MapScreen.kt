package com.example.mapsapp.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.ui.components.BottomSearchBar
import com.example.mapsapp.ui.components.MyLocationButton
import com.example.mapsapp.ui.components.TopLocationBar
import com.example.mapsapp.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapScreen(
    navController: NavController, viewModel: MapViewModel
) {

    val currentAddress by viewModel.currentAddress.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val isMoving = cameraPositionState.isMoving

    val shadowSize by animateDpAsState(
        targetValue = if (isMoving) 6.dp else 10.dp,
        label = "ShadowScale"
    )

    val pinOffset by animateDpAsState(
        targetValue = if (isMoving) (-20).dp else 0.dp,
        label = "PinBounce"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted

        if (granted) {
            viewModel.getCurrentLocation { latLng ->
                latLng?.let {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(it, 17f)
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            viewModel.updateAddress(center)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false
            )
        )
        Box(
            modifier = Modifier.align(Alignment.Center)
                .offset(y = 2.dp)
                .size(shadowSize)
                .background(
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = CircleShape
                )
        )
        Image(
            painter = painterResource(id = R.drawable.ic_map_pin),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
                .offset(y = pinOffset -18.dp)
                .size(40.dp)
        )

        TopLocationBar(
            address = currentAddress, onClick = {
                viewModel.setPickup(currentAddress)
                viewModel.setEditingField(LocationField.PICKUP)
                navController.navigate("drop")
            })

        BottomSearchBar(
            modifier = Modifier.align(Alignment.BottomCenter), onClick = {
                viewModel.setEditingField(LocationField.DROP)
                navController.navigate("drop")
            })

        MyLocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 100.dp), onClick = {
                if (hasLocationPermission) {
                    viewModel.getCurrentLocation { latLng ->
                        latLng?.let {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(it, 17f)
                            )
                        }
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            })
    }
}
