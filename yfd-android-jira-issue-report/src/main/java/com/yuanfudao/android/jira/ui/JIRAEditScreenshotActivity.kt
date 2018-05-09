package com.yuanfudao.android.jira.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.yuanfudao.android.jira.R
import kotlinx.android.synthetic.main.jira_activity_screenshot_edit.*
import java.io.File
import java.io.FileOutputStream

class ScreenshotEditActivity : AppCompatActivity() {

    companion object {

        private const val ARG_EDIT_IMAGE_PATH = "ARG_EDIT_IMAGE_PATH"

        fun createIntent(context: Context, imagePath: String): Intent {
            val intent = Intent(context, ScreenshotEditActivity::class.java)
            intent.putExtra(ARG_EDIT_IMAGE_PATH, imagePath)
            return intent
        }
    }

    private val imagePath: String by lazy {
        intent.extras?.getString(ARG_EDIT_IMAGE_PATH) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (imagePath.isBlank()) {
            finish()
            return
        }
        setContentView(R.layout.jira_activity_screenshot_edit)
        btnUndo.setOnClickListener {
            picDrawView.undo()
        }

        btnRedo.setOnClickListener {
            picDrawView.redo()
        }

        Glide.with(this).load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(picDrawView)
        btnSave.setOnClickListener {
            SaveBitmapAsyncTask(this@ScreenshotEditActivity, imagePath) {
                setResult(if (it) Activity.RESULT_OK else 1)
                finish()
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, picDrawView.toBitmap())
        }
    }
}

fun View.toBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (!ViewCompat.isLaidOut(this)) {
        return null
    }
    return Bitmap.createBitmap(width, height, config).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}

inline fun Bitmap.applyCanvas(block: Canvas.() -> Unit): Bitmap {
    val c = Canvas(this)
    c.block()
    return this
}

private class SaveBitmapAsyncTask(
        val context: Context,
        val imagePath: String,
        val callback: ((success: Boolean) -> Unit)
) : AsyncTask<Bitmap?, Void, Boolean>() {

    override fun doInBackground(vararg bitmaps: Bitmap?): Boolean {
        val bitmap: Bitmap = bitmaps[0] ?: return false
        val file = File(imagePath)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            return true
        } catch (ignored: Exception) {
        } finally {
            fos?.close()
        }
        return false
    }

    override fun onPostExecute(success: Boolean) {
        callback.invoke(success)
    }
}
