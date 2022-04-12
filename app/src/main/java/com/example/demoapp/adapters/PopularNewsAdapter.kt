package com.example.demoapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.R
import com.example.demoapp._interfaces.ItemClickListener
import com.example.demoapp.models.Article
import com.example.demoapp.utils.Constants.Companion.HOME_NEWS
import kotlinx.android.synthetic.main.items_popular_news_layout.view.*


class PopularNewsAdapter(type: String, private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<PopularNewsAdapter.ArticleViewHolder>() {
    private var newType: String = type

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.items_popular_news_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position + 1]

        holder.itemView.apply {
            if (newType == HOME_NEWS) {
                ivBookmarkImage.setBackgroundResource(R.drawable.bookmark_grey)
            } else {
                ivBookmarkImage.setBackgroundResource(R.drawable.bookmark)
            }
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
        }
        holder.itemView.ivBookmarkImage.setOnClickListener {
            clickListener.onBookmarkClick(article, position)
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemsClick(article, position)

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size - 1
    }
}