package com.stxnext.stxinsider.view.elementItemView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.stxnext.stxinsider.R
import com.stxnext.stxinsider.model.SliderItem

import java.io.IOException
import java.io.InputStream

/**
 * Created by bkosarzycki on 01.02.16.
 */
open abstract class BaseItemView(private val mContext: Context, attrs: AttributeSet?) : FrameLayout(mContext, attrs) {

    internal val TAG = BaseItemView::class.java.name
    var item: SliderItem? = null
        private set

    init {
        removeAllViews()
        addLayoutView(mContext);
    }

    abstract fun addLayoutView(cont: Context)

    @Suppress("UNUSED_PARAMETER")
    fun bind(item: SliderItem, position: Int, clickListener: OnClickListener?) {
        this.item = item

        val nameTextView = findViewById(R.id.item_teams_list_header) as TextView
        val teamImageView = findViewById(R.id.item_teams_list_team_background) as ImageView
        //val titleTextView =  findViewById(R.id.title) as TextView;
        val description = findViewById(R.id.description) as TextView
        nameTextView.text = item.header
        description.text = item.description

        try {
            val file = context.assets.open(item.imagePath)
            //teamImageView.setImageDrawable(Drawable.createFromStream(file, null))
        } catch (e: IOException) {
            Log.e(TAG, "Error creating team image: " + e.toString())
        }

        if (clickListener != null && !this.javaClass.isAssignableFrom(TallItemView::class.java))
            this.setOnClickListener(clickListener)
    }
}
