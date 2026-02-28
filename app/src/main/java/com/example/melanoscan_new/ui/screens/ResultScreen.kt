package com.example.melanoscan_new.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.melanoscan_new.R
import com.example.melanoscan_new.data.HistoryRepository
import com.example.melanoscan_new.ui.theme.MelanoScan_newTheme
import com.example.melanoscan_new.ui.theme.neumorphic
import com.example.melanoscan_new.util.BitmapUtil
import com.example.melanoscan_new.util.TfliteClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ResultScreen(
    navController: NavController,
    result: TfliteClassifier.Companion.ClassificationResult?,
    bitmap: Bitmap?,
    onResultScreenDisposed: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    val historyRepository = remember { HistoryRepository(context) }

    DisposableEffect(Unit) {
        onDispose(onResultScreenDisposed)
    }

    result?.let { classificationResult ->
        val (displayText, resultColor) = when (classificationResult.className) {
            "malignant" -> Pair(stringResource(id = R.string.result_malignant), Color.Red)
            "benign" -> Pair(stringResource(id = R.string.result_benign), Color(0xFFFFC107))
            else -> Pair(stringResource(id = R.string.result_non_cancer), Color(0xFF4CAF50))
        }

        val resultIcon = when (classificationResult.className) {
            "malignant" -> Icons.Default.Warning
            else -> Icons.Default.CheckCircle
        }

        val medicalAdvice = when (classificationResult.className) {
            "malignant" -> stringResource(id = R.string.advice_malignant)
            "benign" -> stringResource(id = R.string.advice_benign)
            else -> stringResource(id = R.string.advice_non_cancer)
        }

        Box(Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Confidence Threshold Check
                if (classificationResult.confidence < 70f) {
                    // Unclear Image UI
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neumorphic(cornerRadius = 28.dp)
                            .border(2.dp, Color.Gray, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFF0F2F5))
                            .padding(32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Warning", tint = Color.Gray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(id = R.string.result_unclear_image),
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray
                            )
                        }
                    }
                } else {
                    // Main Neumorphic Card with Results
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neumorphic(cornerRadius = 28.dp)
                            .border(2.dp, resultColor, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFF0F2F5))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Recessed Image Display
                            bitmap?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .neumorphic(cornerRadius = 24.dp, shadowOffset = (-5).dp) // Inner shadow
                                        .clip(RoundedCornerShape(24.dp))
                                ) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Scanned Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Diagnosis
                            Icon(imageVector = resultIcon, contentDescription = "Result Icon", tint = resultColor, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(displayText, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = resultColor)
                            Text(
                                "${stringResource(id = R.string.result_confidence)}: ${String.format("%.1f%%", classificationResult.confidence)}",
                                fontSize = 18.sp,
                                color = Color.DarkGray
                            )

                            // Low-confidence "Benign" warning
                            if (classificationResult.className == "benign" && classificationResult.confidence < 85f) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(id = R.string.warning_low_confidence_benign),
                                    fontSize = 14.sp,
                                    color = resultColor, // Use the benign color for the warning
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Probability Breakdown
                            Text(
                                stringResource(id = R.string.result_probability_breakdown),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                classificationResult.probabilities.forEach { (label, probability) ->
                                    ProbabilityTile(label = label.replaceFirstChar { it.titlecase() }, probability = probability)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Medical Advice
                            Text(
                                stringResource(id = R.string.result_medical_advice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(medicalAdvice, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CollaborationFooter()
                Spacer(modifier = Modifier.height(88.dp)) // Space for the buttons at the bottom
            }

            // --- Bottom Buttons --- (Placed in Box to overlay on top of the Column)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Save Result Button
                Button(
                    onClick = {
                        bitmap?.let { bitmapToSave ->
                            isSaving = true
                            coroutineScope.launch(Dispatchers.Main) {
                                try {
                                    val displayName = "MelanoScan_Result_${System.currentTimeMillis()}"
                                    val imagePath = withContext(Dispatchers.IO) {
                                        BitmapUtil.saveBitmapToGallery(context, bitmapToSave, displayName)
                                    }
                                    historyRepository.saveClassificationResult(classificationResult, imagePath ?: "")
                                    Toast.makeText(context, context.getString(R.string.result_saved_to_gallery), Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "${context.getString(R.string.result_failed_to_save)}: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    },
                    enabled = !isSaving && bitmap != null && (result.confidence >= 70f),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF616161), // Solid Gray
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = stringResource(id = R.string.result_save_to_gallery), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(id = R.string.result_save_to_gallery),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Back to Scan Button
                Button(
                    onClick = { navController.popBackStack() },
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3), // Solid Blue
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        stringResource(id = R.string.result_back_to_scan),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- Saving Indicator --- (Overlay)
            if (isSaving) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(id = R.string.result_saving), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProbabilityTile(label: String, probability: Float) {
    Box(
        modifier = Modifier
            .neumorphic(cornerRadius = 12.dp, shadowOffset = (-4).dp) // Pressed look
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F2F5))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${String.format("%.1f%%", probability)}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.DarkGray)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun CollaborationFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Image(painter = painterResource(id = R.drawable.logo_bumi_shalawat), contentDescription = "Logo Bumi Shalawat", modifier = Modifier.height(40.dp))
            Image(painter = painterResource(id = R.drawable.logo_research), contentDescription = "Logo Research", modifier = Modifier.height(40.dp))
            Image(painter = painterResource(id = R.drawable.logo_melanoscan), contentDescription = "Logo MelanoScan", modifier = Modifier.height(40.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.about_collaboration),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true, locale = "in")
@Composable
fun ResultScreenPreview() {
    MelanoScan_newTheme {
        ResultScreen(
            navController = rememberNavController(),
            result = TfliteClassifier.Companion.ClassificationResult(
                className = "benign",
                confidence = 82.7f,
                probabilities = mapOf("malignant" to 13.2f, "benign" to 82.7f, "non-cancer" to 4.1f)
            ),
            bitmap = null, // Bitmap is not available in preview
            onResultScreenDisposed = {}
        )
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
fun ResultScreenUnclearPreview() {
    MelanoScan_newTheme {
        ResultScreen(
            navController = rememberNavController(),
            result = TfliteClassifier.Companion.ClassificationResult(
                className = "benign",
                confidence = 65.0f,
                probabilities = mapOf("malignant" to 30f, "benign" to 65f, "non-cancer" to 5f)
            ),
            bitmap = null, // Bitmap is not available in preview
            onResultScreenDisposed = {}
        )
    }
}
