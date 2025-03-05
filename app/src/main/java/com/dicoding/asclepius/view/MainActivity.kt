package com.dicoding.asclepius.view

import com.dicoding.asclepius.helper.ImageClassifierHelper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classificationListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?) {
                        val resultString = results?.joinToString("\n") {
                            val threshold = (it.categories[0].score * 100).toInt()
                            "${it.categories[0].label} : ${threshold}%"
                        }
                        if (resultString != null) {
                            lifecycleScope.launch(Dispatchers.IO) {

                                this@MainActivity.runOnUiThread {
                                    moveToResult(resultString)
                                }

                            }
                        }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(uri)
        } ?: showToast("Pilih gambar terlebih dahulu")
    }

    private fun moveToResult(resultText: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT_TEXT", resultText)
            currentImageUri?.let { putExtra("IMAGE_URI", it.toString()) }
        }
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}