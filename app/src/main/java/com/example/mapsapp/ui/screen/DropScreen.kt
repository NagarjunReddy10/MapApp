package com.example.mapsapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mapsapp.ui.components.LocationCard
import com.example.mapsapp.viewmodel.MapViewModel


enum class LocationField{
    PICKUP,
    DROP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropScreen(
    navController: NavController,
    viewModel: MapViewModel
) {

    val pickup by viewModel.pickupAddress.collectAsState()
    val drop by viewModel.dropAddress.collectAsState()
    val editingField by viewModel.editingField.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()

    LaunchedEffect(currentAddress) {
        if(pickup.isEmpty() && currentAddress.isNotEmpty()){
            viewModel.setPickup(currentAddress)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = if(editingField == LocationField.PICKUP) "Pickup" else "Drop",
                    fontWeight = FontWeight.SemiBold
                )
            },
                navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LocationCard(
                title = "Pickup",
                value = pickup,
                dotColor = Color(0xFF2E7D32),
                isActive = editingField == LocationField.PICKUP,
                onClick = {
                    viewModel.setEditingField(LocationField.PICKUP)
                },
                onValueChange = {
                    viewModel.setPickup(it)
                },
                onClear = {
                    viewModel.setPickup("")
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            LocationCard(
                title = "Drop",
                value = drop,
                dotColor = Color.Red,
                isActive = editingField == LocationField.DROP,
                onClick = {
                    viewModel.setEditingField(LocationField.DROP)
                },
                onValueChange = {
                    viewModel.setDrop(it)
                },
                onClear = {
                    viewModel.setDrop("")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .background(Color(0xFF1565C0))
                .clickable {
                    navController.popBackStack()
                }
                .padding(16.dp), contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Select from map", color = Color.White, fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}