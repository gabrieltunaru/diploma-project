package com.cannondev.messaging.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Response
import com.cannondev.messaging.Constants.BACKEND_URL
import com.cannondev.messaging.FileDataPart
import com.cannondev.messaging.VolleyFileUploadRequest
import com.cannondev.messaging.http.Queue
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.net.URL


object ImageHandler {
    fun getImage(path: String): Bitmap? {
        val url = URL("$BACKEND_URL/$path")
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        return bmp
    }

    fun loadPhoto(ctx: Context, url: String?, view: ImageView) {
        if (url != null ) {
            val photoUrl = "$BACKEND_URL/general/image/${url}"
            Picasso.with(ctx).load(photoUrl).transform(CircleTransform()).into(view)
        } else {
            Log.w(this::class.simpleName, "null photo url")
        }
    }


    fun uploadImage(uri: Uri, context: Context, authToken: String) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            sendToServer((it.readBytes()), context, authToken)
        }
    }

    fun sendToServer(imageData: ByteArray,context: Context , authToken: String) {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            "${BACKEND_URL}/profile/setPhoto",
            authToken,
            Response.Listener {
                println("response is: $it")
            },
            Response.ErrorListener {
                Toast.makeText(context, "Couldn't upload image", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }
        }
        Queue.getQueue().add(request)
    }
}
class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(
            squaredBitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )
        paint.setShader(shader)
        paint.setAntiAlias(true)
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}