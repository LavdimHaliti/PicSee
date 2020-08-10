package com.example.picsee.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.picsee.R
import com.example.picsee.database.ImageDatabase
import com.example.picsee.databinding.FragmentSearchBinding
import com.example.picsee.recyclerview.ImageAdapter
import com.example.picsee.repository.ImageRepository
import com.example.picsee.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.picsee.util.Resource
import com.example.picsee.viewmodel.ImageViewModel
import com.example.picsee.viewmodel.ImageViewModelProviderFactory
import kotlinx.android.synthetic.main.image_recyclerview_item.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding

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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        binding.searchViewModel = viewModel

        binding.lifecycleOwner = this

        binding.searchRecyclerView.adapter = imagesAdapter

        binding.searchRecyclerView.addOnScrollListener(this@SearchFragment.scrollListener)

        binding.searchBottomNavView.menu.findItem(R.id.search_menu).isChecked = true

        imagesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("hits", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_detailFragment, bundle)
        }

        binding.searchBottomNavView.setOnNavigationItemSelectedListener {
            var itemSelected = true
            when (it.itemId) {

                R.id.favorite_menu -> findNavController().navigate(R.id.favoriteFragment)
            }
            itemSelected
        }

        var job: Job? = null
        binding.searchImgEditText.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchImages(editable.toString())
                    }
                }
            }
        }


        viewModel.searchImages.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { imageApi ->
                        imagesAdapter.differ.submitList(imageApi.hits.toList())
                        val totalPages = imageApi.totalHits / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchImagePage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })




        return binding.root
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotAtLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotAtLastPage && isAtLastItem && isNotAtBegining
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.searchImages(binding.searchImgEditText.text.toString())
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }


}
