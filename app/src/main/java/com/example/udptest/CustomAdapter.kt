package com.example.udptest

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class CustomAdapter(private val context: Context, private var items: ArrayList<Model>?) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getCount(): Int {
        if(items!=null){
            return items!!.size
        }else
            return 0

    }

    override fun getItem(position: Int): Any {
        return items!![position]
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


            holder.tvFruit = convertView!!.findViewById(R.id.pairName) as TextView
            holder.pairNum = convertView.findViewById(R.id.number) as TextView
            holder.tokenTest = convertView.findViewById(R.id.plus) as Button
            holder.tokenDelete = convertView.findViewById(R.id.minus) as Button

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.tvFruit!!.text = items!![position].getTokens()
        //holder.pairNum!!.text = items!![position].getNumbers().toString()
        Log.d("pairNum",items!![position].getNumbers().toString())
        holder.tokenTest!!.setTag(R.integer.btn_plus_view, convertView)
        holder.tokenTest!!.setTag(R.integer.btn_plus_pos, position)
        holder.tokenTest!!.setOnClickListener {

            Thread(Runnable {
                TokenSetting().testToken(items!![position].getTokens()?.replace("Pair ",""))
            }).start()
        }

        holder.tokenDelete!!.setTag(R.integer.btn_minus_view, convertView)
        holder.tokenDelete!!.setTag(R.integer.btn_minus_pos, position)
        holder.tokenDelete!!.setOnClickListener {


            TokenSetting().deleteToken(items!![position].getTokens()?.replace("Pair ",""))
            Log.d("clear","success")

            refreshView()

        }

        return convertView
    }

    private inner class ViewHolder {

        var tokenTest: Button? = null
        var tokenDelete: Button? = null
        var tvFruit: TextView? = null
        internal var pairNum: TextView? = null

    }
    fun refreshView(){
        val list = java.util.ArrayList<Model>()
        val msg : String? = TokenSetting().listToken()

        if(msg!=""){
            val msgList : List<String> = msg!!.split(",")
            for (i in 0 until msgList.size-1) {
                val model = Model()
                model.setNumbers(msgList[i].replace("Pair ","").toInt())
                model.setTokens(msgList[i])
                list.add(model)

            }
            items = list
        }else{
            items = null
        }
        Log.d("View","refresh ListView")
        notifyDataSetChanged()
    }
}