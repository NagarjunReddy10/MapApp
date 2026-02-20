package com.example.mapsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun MyLocationButton(
    onClick:() -> Unit
){
    Box(
        modifier = Modifier.size(48.dp)
            .shadow(6.dp, CircleShape)
            .background(Color.White, CircleShape)
            .clickable{ onClick() },
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null,
            tint = Color(0xFF1A73E8),
            modifier = Modifier.size(22.dp)
        )
    }
}