package com.example.picsee.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.picsee.BuildConfig
import com.example.picsee.R
import com.example.picsee.api.Hit
import com.example.picsee.database.ImageDatabase
import com.example.picsee.databinding.FragmentDetailBinding
import com.example.picsee.databinding.FragmentWebViewBinding
import com.example.picsee.repository.ImageRepository
import com.example.picsee.viewmodel.ImageViewModel
import com.example.picsee.viewmodel.ImageViewModelProviderFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.image_recyclerview_item.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * DetailFragment is used to provide a more detailed manner of pictures that we load from the given API
 * We use Data binding to link layout elements with the code, and using bundle we take arguments from
 * SearchFragment to DetailFragment.
 * From DetailFragment we will be able to save, share, download, and view it in website
 */
class DetailFragment : Fragment() {

    lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    private val imgRepository by lazy {
        ImageRepository(ImageDatabase.getInstance(this.requireContext()))
    }

    private val viewModelProviderFactory by lazy {
        ImageViewModelProviderFactory(requireActivity().application, imgRepository)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(ImageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        //------------------------------------------------------------------------------------------------------------
        //This part of code takes arguments from Search Fragment which are provided by the Entity and Hit class
        val myHits = args.hits
        Picasso
            .get()
            .load(myHits.webformatURL)
            .fit()
            .into(binding.detailImgView)

        binding.theUser.text = myHits.user
        binding.likes.text = myHits.likes.toString()

        binding.comments.text = myHits.comments.toString()
        binding.downloads.text = myHits.downloads.toString()
        binding.stars.text = myHits.favorites.toString()
        binding.views.text = myHits.views.toString()
        //----------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------


        //----------------------------------------------------------------------------------------------------------------
        //This part of code shows a bottom sheet dialog which provides the properties of saving downloading sharing etc...
        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

        bottomSheetDialog?.setContentView(view)

        binding.detailImgView.setOnClickListener {

            bottomSheetDialog?.show()

        }
        view.share.setOnClickListener {
            shareImage()

        }


        view.download.setOnClickListener {
            saveImage()
        }

        view.save.setOnClickListener {

            viewModel.saveImage(myHits)
            Toast.makeText(activity, "Image Saved", Toast.LENGTH_SHORT).show()
        }

        view.web.setOnClickListener{
            val bundle = Bundle().apply {
                putParcelable("hits", myHits)
            }
            findNavController().navigate(R.id.action_detailFragment_to_webViewFragment, bundle)
            bottomSheetDialog?.hide()
        }
        //--------------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------------


        return binding.root
    }

    //Method used for sharing images or data with other applications within the device
    private fun shareImage() {

        val bitmap: Bitmap

        bitmap = (binding.detailImgView.drawable as BitmapDrawable).bitmap

        val photoUri: Uri
        try {

            val file = File(context?.externalCacheDir, "myImage.png")

            val fileOutputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

            fileOutputStream.flush()
            fileOutputStream.close()

            photoUri = FileProvider.getUriForFile(
                this.requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri)
            shareIntent.type = "image/png"

            startActivity(Intent.createChooser(shareIntent, "Share image via:"))
        } catch (ignore: NullPointerException) {
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Method for saving the image to internal storage of the device
    private fun saveImage() {

        val bitmap: Bitmap

        bitmap = (binding.detailImgView.drawable as BitmapDrawable).bitmap

        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())

        val imgName = "$time.PNG"

        val directory =
            File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myImages.png")

        directory.mkdirs()

        val file = File(directory, imgName)

        val stream: OutputStream
        try {

            stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            Toast.makeText(this.requireContext(), "Image Saved to Pictures", Toast.LENGTH_SHORT)
                .show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}