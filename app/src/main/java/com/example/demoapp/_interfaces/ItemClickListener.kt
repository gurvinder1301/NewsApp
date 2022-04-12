package com.example.demoapp._interfaces

import com.example.demoapp.models.Article

interface ItemClickListener {
    fun onBookmarkClick(article: Article, position: Int)
    fun onItemsClick(article: Article, position: Int)

}
