package com.primapp.ui.todo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.primapp.R
import com.primapp.constants.TodoTasksPriorityType
import com.primapp.model.auth.ReferenceItems
import kotlinx.android.synthetic.main.item_todo_priority_list.view.*

@SuppressLint("DefaultLocale")
class AutoCompleteTodoPriorityListArrayAdapter(
    context: Context,
    private val resourceId: Int
) :
    ArrayAdapter<ReferenceItems>(context, resourceId) {
    private val itemList = ArrayList<ReferenceItems>()
    private var tempItems: ArrayList<ReferenceItems> = arrayListOf()
    private var suggestions: MutableList<ReferenceItems> = ArrayList()

    fun addAll(list: List<ReferenceItems>) {
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

        val countryItem: ReferenceItems = getItem(position)
        view!!.tvTitle.text = countryItem.itemText

        when (countryItem.itemText) {
            TodoTasksPriorityType.HIGH -> {
                view.imageDot.setColorFilter(
                    ContextCompat.getColor(context, R.color.priority_high),
                    PorterDuff.Mode.MULTIPLY
                )
            }
            TodoTasksPriorityType.NORMAL -> {
                view.imageDot.setColorFilter(
                    ContextCompat.getColor(context, R.color.priority_normal),
                    PorterDuff.Mode.MULTIPLY
                )
            }
            TodoTasksPriorityType.LOW -> {
                view.imageDot.setColorFilter(
                    ContextCompat.getColor(context, R.color.priority_low),
                    PorterDuff.Mode.MULTIPLY
                )
            }
        }

        return view
    }

    @Nullable
    override fun getItem(position: Int): ReferenceItems {
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
        return itemList.find { it.itemText.equals(value, true) } != null
    }

    fun getItem(value: String): ReferenceItems? {
        return itemList.find { it.itemText.equals(value, true) }
    }

    fun getItemId(value: String): Int? {
        val item = itemList.find { it.itemText.equals(value, true) }
        return item!!.itemId
    }

    fun getItemKey(value: String): String {
        val item = itemList.find { it.itemText.equals(value, true) }
        return item!!.itemValue
    }

    private val fruitFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val items: ReferenceItems = resultValue as ReferenceItems
            return items.itemText
        }


        protected override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (items in tempItems) {
                    if (items.itemText.toLowerCase()
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
                for (item in filterResults.values as ArrayList<ReferenceItems>) {
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