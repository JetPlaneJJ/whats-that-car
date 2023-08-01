package com.example.whatsthatcar.ui.home

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.whatsthatcar.R
import com.example.whatsthatcar.databinding.ActivityMainBinding
import com.example.whatsthatcar.databinding.FragmentHomeBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    /**
     * Private variables
     */
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private var imageCapture: ImageCapture? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val _TAG = "HomeFragment DEBUG"
    private val _REQUIRED_PERMISSIONS =
        mutableListOf (
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
        }.toTypedArray()

    val _uri: MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Log.d(_TAG, "Selected URI: $uri")
            _uri.value = uri
        }
    }
    private val permissionResultsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in _REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(context,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Build ML model
        val localModel = LocalModel.Builder().setAssetFilePath("model.tflite").build()
        val options = CustomImageLabelerOptions.Builder(localModel)
            .setMaxResultCount(5)
            .build()

        val labeler = ImageLabeling.getClient(options)
        val txtOutput: TextView = root.findViewById(R.id.txtOutput)
        val img: ImageView = root.findViewById(R.id.imageToLabel)

        handlePhotoPicker(root, requireContext().contentResolver, labeler, txtOutput, img)

//        val cameraBtn: Button = root.findViewById(R.id.cameraPicker)
//        cameraBtn.setOnClickListener {takePhoto(requireContext().contentResolver)}

//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            requestPermissions()
//        }

        return root
    }

    /**
     * Helper functions
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
//                .also {
//                    it.setSurfaceProvider(_binding!!.viewFinder.surfaceProvider)
//                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e(_TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(contentResolver: ContentResolver) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(_TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d(_TAG, msg)
                }
            }
        )
    }

    private fun requestPermissions() {
        permissionResultsLauncher.launch(_REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = _REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Labels an InputImage and outputs the results in a TextView
     */
    private fun labelImage(labeler: ImageLabeler,
                           txtOutput: TextView,
                           image: InputImage) {
        var outputText = ""
        Toast.makeText(activity, "Processing...", Toast.LENGTH_LONG).show()
        labeler.process(image)
            .addOnSuccessListener { labels ->
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    outputText += "$text : $confidence\n"
                }
                txtOutput.text = outputText
                Log.i(_TAG, outputText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
            }
    }

    /**
     * Handles UI for picking and labeling a photo from the photo gallery.
     * @param root parent View
     * @param contentResolver for retrieving picked image
     * @param labeler ML Model labeler
     * @param txtOutput View in which to display labeler results
     * @param img ImageView to display picked image
     */
    private fun handlePhotoPicker(
        root: View,
        contentResolver: ContentResolver,
        labeler: ImageLabeler,
        txtOutput: TextView,
        img: ImageView
    ) {
        val photoObserver = Observer<Uri> { uri ->
            val bitmap: Bitmap?
            try {
                val inputStream = contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.apply {
                    img.setImageBitmap(this) // replaces picture shown on screen
                }
                val image = InputImage.fromBitmap(bitmap!!, 0)
                labelImage(labeler, txtOutput, image)
                try {
                    inputStream?.close() // close stream
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                Log.e(_TAG, e.toString())
            }
        }

        _uri.observe(viewLifecycleOwner, photoObserver)

        val photoPickerBtn: Button = root.findViewById(R.id.photoPicker)
        photoPickerBtn.setOnClickListener {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
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
        } catch (e: IOException) {
            null
        }
    }
}