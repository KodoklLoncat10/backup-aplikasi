package com.example.melanoscan_new.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.melanoscan_new.R
import com.example.melanoscan_new.ui.navigation.Screen
import com.example.melanoscan_new.ui.theme.MelanoScan_newTheme
import com.example.melanoscan_new.ui.theme.neumorphic

@Composable
fun ScanScreen(navController: NavController, scanViewModel: ScanViewModel = viewModel()) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val isClassifying by scanViewModel.isClassifying.collectAsState()
    val navigateToResult by scanViewModel.navigateToResult.collectAsState()

    LaunchedEffect(navigateToResult) {
        if (navigateToResult) {
            navController.navigate(Screen.Result.route)
            scanViewModel.onNavigationComplete()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { newBitmap ->
        newBitmap?.let { bitmap = it }
    }

    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { bitmap = uriToBitmap(context, it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(24.dp)
    ) {
        Text(
            stringResource(id = R.string.nav_scan),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .neumorphic(cornerRadius = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F2F5))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isClassifying) {
                CircularProgressIndicator()
            } else if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(stringResource(id = R.string.scan_select_image), fontSize = 20.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { requestCameraPermission(context, permissionLauncher) },
                enabled = !isClassifying,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .neumorphic(cornerRadius = 30.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.scan_button_camera), fontSize = 16.sp, color = Color.White)
            }
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                enabled = !isClassifying,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .neumorphic(cornerRadius = 30.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.scan_button_gallery), fontSize = 16.sp, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { bitmap?.let { scanViewModel.classifyImage(it) } },
            enabled = bitmap != null && !isClassifying,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .neumorphic(cornerRadius = 30.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(id = R.string.scan_analyze_now), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Analyze", tint = Color.White)
        }
    }
}

private fun requestCameraPermission(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
            launcher.launch(Manifest.permission.CAMERA)
        }
        else -> {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}

private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }.copy(Bitmap.Config.ARGB_8888, true)
}

@Preview(showBackground = true, locale = "in")
@Composable
fun ScanScreenPreview() {
    MelanoScan_newTheme {
        ScanScreen(navController = rememberNavController())
    }
}
