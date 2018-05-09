package com.yuanfudao.android.jira.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.yuanfudao.android.jira.R
import kotlinx.android.synthetic.main.jira_activity_jira_entry.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by meng on 2018/5/8.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class JIRAEntryActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_CODE_CAPTURE_PERMISSION = 100
        private const val REQUEST_CODE_EDIT_CAPTURE = 101
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val screenWidth by lazy { resources.displayMetrics.widthPixels }
    private val screenHeight by lazy { resources.displayMetrics.heightPixels }
    private val screenDensity by lazy { resources.displayMetrics.densityDpi }
    private val screenshotPath: String by lazy {
        cacheDir.absolutePath + "/jira-screenshot.jpg"
    }

    private val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jira_activity_jira_entry)
        screenshotBug.visibility = if (supportScreenshot()) View.VISIBLE else View.GONE
        directBug.setOnClickListener {
            createIssue(false)
        }
        screenshotBug.setOnClickListener {
            takeScreenshot()
        }
    }

    private fun takeScreenshot() {
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_CAPTURE_PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAPTURE_PERMISSION) {
            if (resultCode != Activity.RESULT_OK) {
                return
            }
            rootView.visibility = View.INVISIBLE
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, 1, 2)
            virtualDisplay = mediaProjection?.createVirtualDisplay("ScreenCapture",
                    screenWidth, screenHeight, screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader?.surface, null, null)
            startCapture()
        } else if (requestCode == REQUEST_CODE_EDIT_CAPTURE) {
            if (resultCode != Activity.RESULT_CANCELED) {
                createIssue(true)
            } else {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startCapture() {
        val image: Image? = imageReader?.acquireLatestImage()
        if (image == null) {
            rootView.postDelayed({
                startCapture()
            }, 200)
            return
        }
        SaveImageTask(screenWidth, screenHeight, screenshotPath) { success ->
            if (success) {
                onCaptureSuccess()
            } else {
                createIssue(false)
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image)
    }

    private fun onCaptureSuccess() {
        startActivityForResult(
                ScreenshotEditActivity.createIntent(this, screenshotPath),
                REQUEST_CODE_EDIT_CAPTURE)
    }

    private fun createIssue(withScreenshot: Boolean) {
        val intent = Intent(this, JIRACreateIssueActivity::class.java)
        if (withScreenshot) {
            intent.putExtra(JIRACreateIssueActivity.ARG_SCREENSHOT_FILE_PATH, screenshotPath)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (supportScreenshot()) {
            mediaProjection?.stop()
            mediaProjection = null
            virtualDisplay?.release()
            virtualDisplay = null
        }
    }

    private fun supportScreenshot(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    }

    private class SaveImageTask(
            val screenWidth: Int,
            val screenHeight: Int,
            val screenshotPath: String,
            val callback: ((Boolean) -> Unit)?
    ) : AsyncTask<Image, Any, Boolean>() {

        override fun doInBackground(vararg params: Image): Boolean {
            val image = params[0]
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * screenWidth
            val file = File(screenshotPath)
            val fos = FileOutputStream(file)
            try {
                val bitmap = Bitmap.createBitmap(screenWidth + rowPadding / pixelStride,
                        screenHeight, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                return true
            } catch (ignored: Exception) {
            } finally {
                image.close()
                fos.close()
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            callback?.invoke(result)
        }
    }
}
