package com.example.picsee.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.picsee.BuildConfig

import com.example.picsee.R
import com.example.picsee.database.ImageDatabase
import com.example.picsee.databinding.FragmentWebViewBinding
import com.example.picsee.repository.ImageRepository
import com.example.picsee.viewmodel.ImageViewModel
import com.example.picsee.viewmodel.ImageViewModelProviderFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.image_recyclerview_item.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class WebViewFragment : Fragment() {

    lateinit var binding: FragmentWebViewBinding
    private val args: WebViewFragmentArgs by navArgs()
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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)

        val image = args.hits
        binding.imgWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(image.pageURL)
        }

        return binding.root
    }
}
