package com.example.todo.ui.mine

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.todo.R
import com.example.todo.TodoApplication
import kotlinx.android.synthetic.main.activity_change_photo.*
import java.io.File
import java.io.OutputStream

class ChangePhotoActivity : AppCompatActivity() {

    val takePhoto = 1
    val fromAlbum = 2
    lateinit var imageUri: Uri
    lateinit var outputImage : File

    private var avatarChangedListener: OnAvatarChangedListener? = null


    interface OnAvatarChangedListener {
        fun onAvatarChanged(uri: Uri)
    }

    fun setOnAvatarChangedListener(listener: OnAvatarChangedListener) {
        avatarChangedListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_photo)
        takePhotoBtn.setOnClickListener {
            //创建File对象，用于存储拍照后的图片
            outputImage = File(externalCacheDir,"output_image.jpg")
            if (outputImage.exists()){
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                FileProvider.getUriForFile(this,"com.example.todo.fileprovider",outputImage)
            }else{
                Uri.fromFile(outputImage)
            }
            //启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
            startActivityForResult(intent,takePhoto)
        }
        fromAlbumBtn.setOnClickListener {
            //打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只显示图片
            intent.type = "image/*"
            startActivityForResult(intent,fromAlbum)
        }
        setAsAvatarBtn.setOnClickListener {
            if (::imageUri.isInitialized) {
                val currentUser = TodoApplication.getCurrentUser()
              //  实际上一定不为Null
                if(currentUser!=null){
                    saveAvatarToStorage(this,currentUser.account,imageUri)
                }

                val intent = Intent(this, MineActivity::class.java)
                intent.putExtra("avatarUri", imageUri.toString())
                startActivity(intent)
                finish()
            } else {
                // 处理未初始化的情况，比如显示一个 Toast 提示用户选择图片
                Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show()
            }
        }
        backBtn.setOnClickListener{
            MineActivity.actionStart(this)
        }

    }

    private fun saveAvatarToStorage(context: Context, account: String, imageUri: Uri) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("avatar_uri_$account", imageUri.toString()).apply()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            takePhoto ->{
                if(resultCode == Activity.RESULT_OK){
                    //将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    val rotatedBitmap = rotateIfRequired(bitmap)
                    imageView.setImageBitmap(rotatedBitmap)

                    //保存照片到相册
                    savePhotoToAlbum(rotatedBitmap)
                }
            }
            fromAlbum -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    data.data?.let { uri ->
                        imageUri = uri
                        //将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri,"r")?.use{
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap,90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap,180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap,270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,
            matrix,true)
        bitmap.recycle()
        return rotatedBitmap
    }

    //保存照片到相册
    private fun savePhotoToAlbum(bitmap: Bitmap){
        val contentValues = ContentValues().apply{
            put(MediaStore.Images.Media.DISPLAY_NAME, "output_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
            }
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Toast.makeText(this, "照片已保存到相册", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
            }
        }
    }
}