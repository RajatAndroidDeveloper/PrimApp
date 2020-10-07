package com.primapp.ui.initial

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.Nullable
import com.primapp.model.auth.CountryListDataModel
import kotlinx.android.synthetic.main.item_simple_text.view.*


class CountryListArrayAdapter(
    context: Context,
    resourceId: Int,
    items: ArrayList<CountryListDataModel>
) :
    ArrayAdapter<CountryListDataModel>(context, resourceId, items) {
    private val resourceId: Int
    private val items: List<CountryListDataModel>
    private lateinit var tempItems: List<CountryListDataModel>
    private lateinit var suggestions: MutableList<CountryListDataModel>


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView

        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            view = inflater.inflate(resourceId, parent, false)
        }

        val countryItem: CountryListDataModel = getItem(position)
        view!!.tvTitle.text = countryItem.value

        return view
    }

    @Nullable
    override fun getItem(position: Int): CountryListDataModel {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return fruitFilter
    }

    fun contains(value: String): Boolean {
        return items.find { it.value.equals(value, true) } != null
    }

    fun getItemKey(value: String): String {
        val item = items.find { it.value.equals(value, true) }
        return item!!.key
    }

    private val fruitFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val items: CountryListDataModel = resultValue as CountryListDataModel
            return items.value
        }

        protected override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (items in tempItems) {
                    if (items.value.toLowerCase()
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
                clear()
                for (item in filterResults.values as ArrayList<CountryListDataModel>) {
                    add(item)
                }
                notifyDataSetChanged()
            } else {
                Log.d("anshul", "result")
                clear()
                addAll(tempItems)
                notifyDataSetChanged()
            }
        }
    }


    init {
        this.items = items
        this.resourceId = resourceId
        tempItems = ArrayList(items)
        suggestions = ArrayList()
    }
}