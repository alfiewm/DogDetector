package meng.nothd

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import clarifai2.api.ClarifaiBuilder
import clarifai2.api.ClarifaiClient
import clarifai2.api.request.ClarifaiRequest
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import meng.nothd.extensions.toast
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    companion object {
        private val PICK_IMAGE = 1000
    }

    private val clarifaiClient: ClarifaiClient by lazy {
        ClarifaiBuilder(getString(R.string.api_key)).buildSync()
    }

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        magicBtn.setOnClickListener { selectToCheck() }
    }

    private fun selectToCheck() {
        predictResultView.text = ""
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            predict()
        }
    }

    private fun predict() {
        if (imageUri == null) {
            Toast.makeText(this, "Image invalid", Toast.LENGTH_SHORT).show()
            return
        }
        loadingProgress.visibility = View.VISIBLE
        Glide.with(this).load(imageUri).into(imageView)
        val bytes = getImageBytes(imageUri!!)
        clarifaiClient.defaultModels.generalModel()
                .predict().withInputs(ClarifaiInput.forImage(bytes!!))
                .executeAsync(object : ClarifaiRequest.Callback<List<ClarifaiOutput<Concept>>> {

                    override fun onClarifaiResponseNetworkError(e: IOException) {
                        runOnUiThread {
                            loadingProgress.visibility = View.GONE
                            toast("network error")
                        }
                    }

                    override fun onClarifaiResponseSuccess(result: List<ClarifaiOutput<Concept>>) {
                        runOnUiThread {
                            loadingProgress.visibility = View.GONE
                            updatePredictResult(result[0])
                        }
                    }

                    override fun onClarifaiResponseUnsuccessful(errorCode: Int) {
                        runOnUiThread {
                            loadingProgress.visibility = View.GONE
                            toast("failed predict: errorCode = $errorCode")
                        }
                    }
                })
    }

    private fun updatePredictResult(clarifaiOutput: ClarifaiOutput<Concept>) {
        val isDog = clarifaiOutput.data().any { it.name().equals("狗", true) }
        predictResultView.text = if (isDog) "很可能是条狗" else "应该不是狗"
    }

    private fun getImageBytes(uri: Uri): ByteArray? {
        var inStream: InputStream? = null
        var bitmap: Bitmap? = null

        return try {
            inStream = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inStream)
            val outStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.toByteArray()
        } catch (e: FileNotFoundException) {
            null
        } finally {
            inStream?.close()
            bitmap?.recycle()
        }
    }
}
