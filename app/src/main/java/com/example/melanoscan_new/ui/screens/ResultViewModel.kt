package com.example.melanoscan_new.ui.screens

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melanoscan_new.util.TfliteClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _classificationResult = MutableStateFlow<TfliteClassifier.Companion.ClassificationResult?>(null)
    val classificationResult = _classificationResult.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val tfliteClassifier: TfliteClassifier = TfliteClassifier(application)

    fun classifyImage(encodedUri: String) {
        viewModelScope.launch(Dispatchers.Default) { // Use Dispatchers.Default for CPU-intensive work
            _isLoading.value = true
            try {
                val decodedUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
                val uri = Uri.parse(decodedUri)
                Log.d("ResultViewModel", "Decoded URI: $uri")

                val bitmap = uriToBitmap(getApplication(), uri)

                val result = tfliteClassifier.classify(bitmap)

                withContext(Dispatchers.Main) {
                    _classificationResult.value = result
                }

            } catch (e: Exception) {
                Log.e("ClassificationError", "Classification failed: ${e.message}", e)
                _classificationResult.value = null // Set result to null on error
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
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

    override fun onCleared() {
        super.onCleared()
        tfliteClassifier.close()
    }
}
