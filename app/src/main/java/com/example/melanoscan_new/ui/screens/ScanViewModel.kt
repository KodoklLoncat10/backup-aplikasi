package com.example.melanoscan_new.ui.screens

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.melanoscan_new.util.TfliteClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _classificationResult = MutableStateFlow<TfliteClassifier.Companion.ClassificationResult?>(null)
    val classificationResult = _classificationResult.asStateFlow()

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    private val _isClassifying = MutableStateFlow(false)
    val isClassifying = _isClassifying.asStateFlow()

    private val _navigateToResult = MutableStateFlow(false)
    val navigateToResult = _navigateToResult.asStateFlow()

    private val tfliteClassifier: TfliteClassifier = TfliteClassifier(application)

    fun classifyImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _scannedBitmap.value = bitmap
            _isClassifying.value = true
            try {
                // Ensure classification runs on a non-UI thread
                val result = withContext(Dispatchers.Default) {
                    tfliteClassifier.classify(bitmap)
                }

                withContext(Dispatchers.Main) {
                    _classificationResult.value = result
                    _navigateToResult.value = true
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Classification failed with exception", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, "Analysis failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                // Ensure the classifying state is always reset on the main thread.
                withContext(Dispatchers.Main) {
                    _isClassifying.value = false
                }
            }
        }
    }

    fun onResultScreenDisposed() {
        _classificationResult.value = null
        _scannedBitmap.value = null
    }

    fun onNavigationComplete() {
        _navigateToResult.value = false
    }

    override fun onCleared() {
        super.onCleared()
        tfliteClassifier.close()
    }
}