package com.example.mapsapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.data.LocationRepository
import com.example.mapsapp.ui.screen.DropScreen
import com.example.mapsapp.ui.screen.MapScreen
import com.example.mapsapp.viewmodel.MapViewModel
import com.example.mapsapp.viewmodel.MapViewModelFactory

@Composable
fun AppNavigation(){

    val context = LocalContext.current
    val repository = remember{ LocationRepository(context) }

    val sharedViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(repository))

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "map"){
        composable("map"){
            MapScreen(navController, sharedViewModel)
        }
        composable("drop"){
            DropScreen(navController, sharedViewModel)
        }
    }
}