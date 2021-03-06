package com.stxnext.stxinsider

import android.app.Fragment
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.stxnext.stxinsider.model.SliderItem
import butterknife.bindView
import com.stxnext.stxinsider.R
import com.stxnext.stxinsider.fragment.DetailsListFragment
import com.stxnext.stxinsider.fragment.TextContentFragment
import com.stxnext.stxinsider.view.model.DetailsContentList
import com.stxnext.stxinsider.view.model.DetailsItem
import java.io.IOException

class DetailsActivity<T> : AppCompatActivity() {

    enum class TYPE { STRING, LIST, EMPTY }
    val TAG = this.javaClass.simpleName

    val mTitleTextView: TextView by bindView(R.id.activity_details_title)
    val mSubtitleTextView: TextView by bindView(R.id.activity_details_subtitle)
    val mHeaderImageView: ImageView by bindView(R.id.activity_details_header_image)


    var mItem: DetailsItem<T>? = null
    var mContentType : TYPE? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mItem = Gson().fromJson<DetailsItem<T>>(intent.getStringExtra("item"), DetailsItem::class.java)
        val contentTypeExtraString = intent.getStringExtra("type")

        mContentType = if (contentTypeExtraString != null)  DetailsActivity.TYPE.valueOf(contentTypeExtraString) else TYPE.EMPTY
        bind(mItem!!)

        if (mContentType == TYPE.EMPTY)
            Toast.makeText(this, "Null content found!", Toast.LENGTH_SHORT).show()
        else if (mContentType == TYPE.STRING)
            replaceContentFragmentWithStringContent()
        else if (mContentType == TYPE.LIST)
            replaceContentFragmentWithList()
        else
            Toast.makeText(this, "Content type unknown!", Toast.LENGTH_SHORT).show()

        var replaceImagePath : String? = mItem?.replacingImagePath
        if (replaceImagePath != null)
            replaceImage(replaceImagePath)
    }

    private fun replaceImage(path: String?) {
        try {
            val file = this.assets.open(path)
            val draw = Drawable.createFromStream(file, null)
            mHeaderImageView.setImageDrawable(draw)

            //set new image height dynamically
            val newHeight = (draw as BitmapDrawable).bitmap.height
            val oldWidth = mHeaderImageView.layoutParams.width
            val layoutParams = LinearLayout.LayoutParams(oldWidth, newHeight)
            mHeaderImageView.layoutParams = layoutParams
            mHeaderImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            mHeaderImageView.requestLayout()
        } catch (e: IOException) {
            Log.e(TAG, "Error creating team image: " + e.toString())
        }

    }

    class EmptyFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_empty, container, false)
        }
    }

    private fun replaceContentFragmentWithStringContent() {
        val detailsContentFragment  = TextContentFragment()

        val content = Gson().fromJson(Gson().toJson( mItem!!.content), SpannableString::class.java)
        detailsContentFragment.itemData = content
        detailsContentFragment.contentType = TextContentFragment.CONTENT_TYPE.HTML

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.activity_details_content_fragment, detailsContentFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun replaceContentFragmentWithList() {

        val detailsContentFragment = DetailsListFragment()

        val content = Gson().fromJson(Gson().toJson( mItem!!.content), DetailsContentList::class.java)
        detailsContentFragment.itemData = content

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.activity_details_content_fragment, detailsContentFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun bind(item: DetailsItem<T>) {
        mTitleTextView.text = item.title
        mSubtitleTextView.text = item.subtitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}
