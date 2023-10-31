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
import com.primapp.model.category.ParentCategoryResult
import kotlinx.android.synthetic.main.item_simple_text.view.*

@SuppressLint("DefaultLocale")
class AutoCompleteCategoryArrayAdapter(
    context: Context,
    private val resourceId: Int
) :
    ArrayAdapter<ParentCategoryResult>(context, resourceId) {
    private val itemList = ArrayList<ParentCategoryResult>()
    private var tempItems: ArrayList<ParentCategoryResult> = arrayListOf()
    private var suggestions: MutableList<ParentCategoryResult> = ArrayList()

    fun addAll(list: List<ParentCategoryResult>) {
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

        val countryItem: ParentCategoryResult = getItem(position)
        view!!.tvTitle.text = countryItem.categoryName

        return view
    }

    @Nullable
    override fun getItem(position: Int): ParentCategoryResult {
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
        return itemList.find { it.categoryName.equals(value, true) } != null
    }

    fun getCategoryName(value: Int): String {
        val item = itemList.find { it.id == value }
        return item!!.categoryName
    }

    fun getItemId(value: String): Int? {
        val item = itemList.find { it.categoryName.equals(value, true) }
        return item!!.id
    }

    private val fruitFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val items: ParentCategoryResult = resultValue as ParentCategoryResult
            return items.categoryName
        }


        protected override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (items in tempItems) {
                    if (items.categoryName.toLowerCase()
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
                for (item in filterResults.values as ArrayList<ParentCategoryResult>) {
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