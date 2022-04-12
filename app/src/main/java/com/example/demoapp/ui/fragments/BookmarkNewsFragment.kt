package com.example.demoapp.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.R
import com.example.demoapp._interfaces.ItemClickListener
import com.example.demoapp.adapters.PopularNewsAdapter
import com.example.demoapp.models.Article
import com.example.demoapp.ui.NewsActivity
import com.example.demoapp.ui.NewsViewModel
import com.example.demoapp.utils.Constants.Companion.BOOKMARK_NEWS
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bookmark_news.*

class BookmarkNewsFragment : Fragment(R.layout.fragment_bookmark_news), ItemClickListener {

    lateinit var viewModel: NewsViewModel
    lateinit var popularNewsAdapter: PopularNewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()
        iv_back.setOnClickListener {
            getActivity()?.onBackPressed();
        }
        iv_search.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", null)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_searchNewsFragment,
                bundle
            )
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, { articles ->
            popularNewsAdapter.differ.submitList(articles)
        })
    }

    override fun onBookmarkClick(article: Article, position: Int) {
        bookmarkAlert(article)
    }

    override fun onItemsClick(article: Article, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }

        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment,
            bundle
        )
    }

    private fun setupRecyclerView() {
        popularNewsAdapter = PopularNewsAdapter(BOOKMARK_NEWS, this)
        rvSavedNews.apply {
            adapter = popularNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun bookmarkAlert(article: Article) {
        val titleView = TextView(context)
        titleView.text = "Alert"
        titleView.gravity = Gravity.LEFT
        titleView.setPadding(25, 20, 20, 20)
        titleView.textSize = 15f
        titleView.setTypeface(Typeface.DEFAULT_BOLD)
        titleView.setBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.white))
        titleView.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
        val ad = AlertDialog.Builder(requireContext()).create()
        ad.setCustomTitle(titleView)
        ad.setCancelable(false)
        ad.setMessage(getString(R.string.bookmark_alert_note))
        ad.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.ok)
        ) { dialog, which ->
            viewModel.deleteArticle(article)
            Snackbar.make(requireView(), getString(R.string.scessfully_deleted_bookmark), Snackbar.LENGTH_SHORT)
                .show()
        }
        ad.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, which ->
            dialog.cancel()
        }
        ad.show()

        val messageView = ad.findViewById<TextView>(android.R.id.message)
        if (messageView != null) {
            messageView.gravity = Gravity.LEFT
        }
        val buttonOK: Button = ad.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonOK.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
        val negative: Button = ad.getButton(DialogInterface.BUTTON_NEGATIVE)
        negative.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.black))
    }
}