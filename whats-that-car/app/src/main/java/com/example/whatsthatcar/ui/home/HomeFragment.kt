package com.example.whatsthatcar.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
import com.example.whatsthatcar.R
import com.example.whatsthatcar.databinding.FragmentHomeBinding
import java.io.IOException
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Build ML model
        val localModel = LocalModel.Builder().setAssetFilePath("model.tflite").build()

        // Get and set test image
        val img: ImageView = root.findViewById(R.id.imageToLabel)
        val fileName = "LEXUS.jpg"
        val bitmap: Bitmap? = assetsToBitmap(fileName)
        bitmap?.apply {
            img.setImageBitmap(this)
        }

        // Handle UI input
        val txtOutput : TextView = root.findViewById(R.id.txtOutput)
        val btn: Button = root.findViewById(R.id.btnTest)
        btn.setOnClickListener {
            val options = CustomImageLabelerOptions.Builder(localModel)
                .setMaxResultCount(5)
                .build()
            val labeler = ImageLabeling.getClient(options)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            var outputText = ""
            Toast.makeText(activity, "Processing...", Toast.LENGTH_LONG).show()

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        outputText += "$text : $confidence\n"
                    }
                    txtOutput.text = outputText
                    Toast.makeText(activity, "Output: $outputText", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assetsToBitmap(fileName: String): Bitmap? {
        return try {
            with(requireActivity().assets.open(fileName)) {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) { null }
    }
}