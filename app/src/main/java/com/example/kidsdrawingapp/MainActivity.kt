package com.example.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.security.Permission


class MainActivity : AppCompatActivity() {
    val opengalarylauncher:ActivityResultLauncher<Intent> =registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->
        if(result.resultCode== RESULT_OK&&result.data!=null)
        {
            val imgbg:ImageView=findViewById(R.id.iv_background)
            imgbg.setImageURI(result.data?.data)
        }
    }
    private val readExternalstorage:ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts .RequestMultiplePermissions())
        {permissions->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted=it.value

                if(isGranted)
                {
                    if(permissionName==Manifest.permission.READ_EXTERNAL_STORAGE)
                    {
                        Toast.makeText(this,"permission granted",Toast.LENGTH_LONG).show()
                    }

                    val intentvar=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    opengalarylauncher.launch(intentvar)
                }
                else{
                    if(permissionName==Manifest.permission.READ_EXTERNAL_STORAGE)
                    {
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show()
                    }
                }
            }






        }



    private var drawingview:DrawingView?=null
    private var mImageButtonCurrentPaint:ImageButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCameraPermission:ImageButton=findViewById(R.id.permission_button)

        btnCameraPermission.setOnClickListener{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                showRationaleDialog("permission for storage is required","storge access denied")
            }
            else
            {
               readExternalstorage.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }

        }


        drawingview=findViewById(R.id.drawing_view)
        drawingview?.setSizeForBrush(20.toFloat())
        val LinearLayoutPaintColors=findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint=LinearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))
        val ib_brush:ImageButton=findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        val ib_undo:ImageButton=findViewById(R.id.ib_undo)
        ib_undo.setOnClickListener {
            drawingview?.onClickUndo()

        }
    }


//    private fun requestStoragePermission()
//    {
//        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
//        {
//            showRationaleDialog("kids drawing app","need to access storage to work ")
//        }
//
//    }
private fun requestStoragePermission (){
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )){
        showRationaleDialog("Kind Drawing App","Kid Drawing App" +
                "needs to Access Your external storage")
    }else{
        readExternalstorage.launch(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE

        ))
    }
}
    private fun showRationaleDialog(
        title : String,
        message : String,
    )
    {
        val builder  : AlertDialog.Builder = AlertDialog.Builder(this,)
        builder.setTitle(title)
            .setMessage(message).setPositiveButton("Cancel"){dialog,_->
                dialog.dismiss()
            }
        builder.create().show()
    }


    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn : ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener(View.OnClickListener{
            drawingview?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        })

        val mediumBtn : ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener(View.OnClickListener{
            drawingview?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        })

        val largeBtn : ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener(View.OnClickListener{
            drawingview?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        })

        brushDialog.show()
    }
    fun paintClicked(view : View){
        if (view != mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingview?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)

            )
            mImageButtonCurrentPaint = view
        }
    }

}