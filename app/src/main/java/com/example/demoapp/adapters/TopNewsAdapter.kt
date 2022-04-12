package com.example.demoapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp.models.Article
import kotlinx.android.synthetic.main.items_popular_news_layout.view.*

class TopNewsAdapter(private val mList: List<Article>, private val clickListener: ClickListener) :
    RecyclerView.Adapter<TopNewsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.items_top_news_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val article = mList[0]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
        }
        holder.itemView.ivBookmarkImage.setOnClickListener {
            clickListener.onTopBookmarkClick(article, position)
        }

        holder.itemView.setOnClickListener {
            clickListener.onTopItemsClick(article, position)

        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)

    interface ClickListener {
        fun onTopBookmarkClick(article: Article, position: Int)
        fun onTopItemsClick(article: Article, position: Int)

    }

}
