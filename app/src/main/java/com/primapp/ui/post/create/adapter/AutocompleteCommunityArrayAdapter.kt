package com.primapp.ui.post.create.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.Nullable
import com.primapp.model.community.CommunityData
import kotlinx.android.synthetic.main.item_simple_text.view.*

@SuppressLint("DefaultLocale")
class AutocompleteCommunityArrayAdapter(
    context: Context,
    private val resourceId: Int
) :
    ArrayAdapter<CommunityData>(context, resourceId) {
    private val itemList = ArrayList<CommunityData>()
    private var tempItems: ArrayList<CommunityData> = arrayListOf()
    private var suggestions: MutableList<CommunityData> = ArrayList()

    fun addAll(list: List<CommunityData>) {
        itemList.clear()
        itemList.addAll(list)
        tempItems.clear()
        tempItems.addAll(list)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView

        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(resourceId, parent, false)
        }

        val countryItem: CommunityData = getItem(position)
        view!!.tvTitle.text = countryItem.communityName

        return view
    }

    @Nullable
    override fun getItem(position: Int): CommunityData {
        return itemList[position]
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return fruitFilter
    }

    fun contains(value: String): Boolean {
        return itemList.find { it.communityName.equals(value, true) } != null
    }

    fun getItemId(value: String): Int? {
        val item = itemList.find { it.communityName.equals(value, true) }
        return item!!.id
    }

    private val fruitFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val items: CommunityData = resultValue as CommunityData
            return items.communityName
        }


        protected override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (items in tempItems) {
                    if (items.communityName.toLowerCase()
                            .startsWith(charSequence.toString().toLowerCase())
                    ) {
                        suggestions.add(items)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        protected override fun publishResults(
            charSequence: CharSequence?,
            filterResults: FilterResults?
        ) {
            if (filterResults != null && filterResults.count > 0) {
                Log.d("anshul", "result--")
                itemList.clear()
                for (item in filterResults.values as ArrayList<CommunityData>) {
                    itemList.add(item)
                }
                notifyDataSetChanged()
            } else {
                Log.d("anshul", "result : temp size : ${tempItems.size}")
                itemList.clear()
                if (charSequence.isNullOrEmpty())
                    itemList.addAll(tempItems)
                notifyDataSetChanged()
            }
        }
    }
}