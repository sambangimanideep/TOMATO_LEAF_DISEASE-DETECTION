package com.example.myapplication



import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.ml.Model // Replace with your actual model class name
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val PERMISSION_REQUEST_CODE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)

        val captureButton: Button = findViewById(R.id.openbutton)
        val galleryButton: Button = findViewById(R.id.gallery)

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            }
        }

        galleryButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(photo)
                    resultTextView.text = "Image Captured"
                    processImage(photo)
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    val inputStream: InputStream? = selectedImageUri?.let { contentResolver.openInputStream(it) }
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap)
                    resultTextView.text = "Image Imported"
                    processImage(bitmap)
                }
            }
        }
    }

    private fun processImage(bitmap: Bitmap) {
        resultTextView.text = "Processing Image..."

        // Resize and normalize the bitmap to match your model's input size.
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)

        // Load the TFLite model.
        val model = Model.newInstance(this) // Replace with your model's actual name

        // Create input for the model.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 150, 150, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Run the model and get the result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Interpret the result based on your tea leaf class labels.
        val predictedClass = getPredictedClass(outputFeature0.floatArray)

        // Display result.
        resultTextView.text = "Predicted: $predictedClass"

        // Release model resources.
        model.close()
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(150 * 150 * 3 * 4) // 4 bytes per float.
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(150 * 150)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixelIndex = 0
        for (i in 0 until 150) {
            for (j in 0 until 150) {
                val pixel = intValues[pixelIndex++]
                byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))
                byteBuffer.putFloat(((pixel and 0xFF) / 255.0f))
            }
        }
        return byteBuffer
    }

    private fun getPredictedClass(probabilities: FloatArray): String {
        val labels = arrayOf("Bacterial_spot", "Early_blight", "healthy", "mosaic_virus", "Target_Spot", "YellowLeaf__Curl_Virus")
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        return labels[maxIndex]
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}