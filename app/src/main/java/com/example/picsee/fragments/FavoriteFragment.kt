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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.picsee.BuildConfig

import com.example.picsee.R
import com.example.picsee.api.Hit
import com.example.picsee.database.ImageDatabase
import com.example.picsee.databinding.FragmentFavoriteBinding
import com.example.picsee.recyclerview.ImageAdapter

import com.example.picsee.repository.ImageRepository
import com.example.picsee.viewmodel.ImageViewModel
import com.example.picsee.viewmodel.ImageViewModelProviderFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.image_recyclerview_item.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * FavoriteFragment is used to save any desired picture in the database and shows them using recyclerview
 */
class FavoriteFragment : Fragment() {

    lateinit var binding: FragmentFavoriteBinding

    private val imgRepository by lazy {
        ImageRepository(ImageDatabase.getInstance(this.requireContext()))
    }

    private val viewModelProviderFactory by lazy {
        ImageViewModelProviderFactory(requireActivity().application, imgRepository)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelProviderFactory).get(ImageViewModel::class.java)
    }
    private val imagesAdapter by lazy {
        ImageAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false)


        binding.favViewModel = viewModel

        binding.lifecycleOwner = this

        binding.favoriteRecyclerView.adapter = imagesAdapter

        binding.favBottomNavView.menu.findItem(R.id.favorite_menu).isChecked = true

        binding.favBottomNavView.setOnNavigationItemSelectedListener {
            var itemSelected = true
            when (it.itemId) {

                R.id.search_menu -> findNavController().navigate(R.id.searchFragment)
            }
            itemSelected
        }


        imagesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("hits", it)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
        }

        viewModel.getAllImages().observe(viewLifecycleOwner, Observer { images ->
            imagesAdapter.differ.submitList(images)
        })

        //We can delete to while swiping on an item in the recyclerview using ItemTouchHelper
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val image = imagesAdapter.differ.currentList[position]
                viewModel.deleteImage(image)

                view?.let {
                    Snackbar.make(it, "Image successfully deleted", Snackbar.LENGTH_LONG).apply {
                        //This snackbar action undoes the deletion process
                        setAction("Undo") {
                            viewModel.saveImage(image)
                        }
                        show()
                    }
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.favoriteRecyclerView)
        }


        return binding.root
    }



}
