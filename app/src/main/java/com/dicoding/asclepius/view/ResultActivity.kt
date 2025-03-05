package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resultText = intent.getStringExtra("RESULT_TEXT") ?: "Hasil tidak tersedia"
        val imageUri = intent.getStringExtra("IMAGE_URI")

        binding.resultText.text = resultText
        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }
    }

}