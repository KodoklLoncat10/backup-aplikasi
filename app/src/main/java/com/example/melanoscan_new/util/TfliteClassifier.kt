package com.example.melanoscan_new.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TfliteClassifier(context: Context) {

    private val interpreter: Interpreter
    private val modelInputSize = 224
    val labels = listOf("Non_Cancer", "benign", "malignant")

    init {
        try {
            interpreter = Interpreter(loadModelFile(context))
        } catch (e: Exception) {
            Log.e("TfliteClassifier", "Error initializing TFLite Interpreter.", e)
            throw e
        }
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val modelAssetPath = "resnet50_ready.tflite"
        val fileDescriptor = context.assets.openFd(modelAssetPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * modelInputSize * modelInputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputSize, modelInputSize, true)

        val pixels = IntArray(modelInputSize * modelInputSize)
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        // ResNet50 Caffe-style preprocessing: BGR order and mean subtraction.
        val meanB = 103.939f
        val meanG = 116.779f
        val meanR = 123.68f

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF).toFloat()
            val g = (pixelValue shr 8 and 0xFF).toFloat()
            val b = (pixelValue and 0xFF).toFloat()

            byteBuffer.putFloat(b - meanB)
            byteBuffer.putFloat(g - meanG)
            byteBuffer.putFloat(r - meanR)
        }
        byteBuffer.rewind()
        return byteBuffer
    }

    fun classify(bitmap: Bitmap): ClassificationResult {
        val inputBuffer = convertBitmapToByteBuffer(bitmap)
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)

        interpreter.run(inputBuffer, outputBuffer.buffer)

        val outputArray = outputBuffer.floatArray
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] }!!

        val className = labels[maxIndex]
        val confidence = outputArray[maxIndex] * 100f

        val probabilities = labels.mapIndexed { index, label ->
            label to outputArray[index] * 100f
        }.toMap()

        return ClassificationResult(className, confidence, probabilities)
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        data class ClassificationResult(val className: String, val confidence: Float, val probabilities: Map<String, Float>)
    }
}