package com.example.udptest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class CustomAdapter(private val context: Context) : BaseAdapter() {



    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return MainActivity.modelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return MainActivity.modelArrayList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.lv_item, null, true)


            holder.tvFruit = convertView!!.findViewById(R.id.animal) as TextView
            holder.tvnumber = convertView.findViewById(R.id.number) as TextView
            holder.btn_plus = convertView.findViewById(R.id.plus) as Button
            holder.btn_minus = convertView.findViewById(R.id.minus) as Button

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.tvFruit!!.setText(MainActivity.modelArrayList.get(position).getTokens())
        holder.tvnumber!!.setText(MainActivity.modelArrayList.get(position).getNumbers().toString())

        holder.btn_plus!!.setTag(R.integer.btn_plus_view, convertView)
        holder.btn_plus!!.setTag(R.integer.btn_plus_pos, position)
        holder.btn_plus!!.setOnClickListener {
            val tempview = holder.btn_plus!!.getTag(R.integer.btn_plus_view) as View
            val tv = tempview.findViewById(R.id.number) as TextView
            val pos = holder.btn_plus!!.getTag(R.integer.btn_plus_pos) as Int

            val number = Integer.parseInt(tv.text.toString()) + 1
            tv.text = number.toString()

            MainActivity.modelArrayList.get(pos).setNumbers(number)
        }

        holder.btn_minus!!.setTag(R.integer.btn_minus_view, convertView)
        holder.btn_minus!!.setTag(R.integer.btn_minus_pos, position)
        holder.btn_minus!!.setOnClickListener {
            val tempview = holder.btn_minus!!.getTag(R.integer.btn_minus_view) as View
            val tv = tempview.findViewById(R.id.number) as TextView
            val pos = holder.btn_minus!!.getTag(R.integer.btn_minus_pos) as Int

            val number = Integer.parseInt(tv.text.toString()) - 1
            tv.text = number.toString()

            MainActivity.modelArrayList.get(pos).setNumbers(number)
        }

        return convertView
    }

    private inner class ViewHolder {

        var btn_plus: Button? = null
        var btn_minus: Button? = null
        var tvFruit: TextView? = null
        internal var tvnumber: TextView? = null

    }

}