package com.example.demoapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.R
import com.example.demoapp._interfaces.ItemClickListener
import com.example.demoapp.adapters.PopularNewsAdapter
import com.example.demoapp.adapters.TopNewsAdapter
import com.example.demoapp.models.Article
import com.example.demoapp.ui.NewsActivity
import com.example.demoapp.ui.NewsViewModel
import com.example.demoapp.utils.Constants.Companion.HOME_NEWS
import com.example.demoapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.demoapp.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home_news.*


class HomeNewsFragment : Fragment(R.layout.fragment_home_news),
    ItemClickListener, TopNewsAdapter.ClickListener {

    lateinit var viewModel: NewsViewModel
    private lateinit var popularNewsAdapter: PopularNewsAdapter
    private lateinit var topNewsAdapter: TopNewsAdapter
    private var list = ArrayList<Article>()
    private lateinit var article: Article
    private var bookmarkPosition: Int = 0


    private val TAG = "HomeNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView2()

        iv_bookmark.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", null)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_savedNewsFragment,
                bundle
            )
        }
        iv_search.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", null)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_searchNewsFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let { newsResponse ->
                        list = newsResponse.articles as ArrayList<Article>
                        article = newsResponse.articles.get(bookmarkPosition)
                        setupRecyclerView()

                        popularNewsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            rvBreakingNews2.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error accrued: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private var scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        topNewsAdapter = TopNewsAdapter(list,this)
        rvBreakingNews4.apply {
            adapter = topNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeNewsFragment.scrollListener)
        }
    }

    private fun setupRecyclerView2() {
        popularNewsAdapter = PopularNewsAdapter(HOME_NEWS,this)
        rvBreakingNews2.apply {
            adapter = popularNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeNewsFragment.scrollListener)
        }
    }

    override fun onBookmarkClick(article: Article, position: Int) {
        viewModel.saveArticle(article)
        Snackbar.make(requireView(), "Bookmarked Saved Successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemsClick(article: Article, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }

        findNavController().navigate(
            R.id.action_breakingNewsFragment_to_articleFragment,
            bundle
        )    }

    override fun onTopBookmarkClick(article: Article, position: Int) {
        viewModel.saveArticle(article)
        Snackbar.make(view!!, getString(R.string.bookmark_success), Snackbar.LENGTH_SHORT).show()
    }
    override fun onTopItemsClick(article: Article, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }

        findNavController().navigate(
            R.id.action_breakingNewsFragment_to_articleFragment,
            bundle
        )     }
}