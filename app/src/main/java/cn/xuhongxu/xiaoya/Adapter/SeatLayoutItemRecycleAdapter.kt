package cn.xuhongxu.xiaoya.Adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import cn.xuhongxu.LibrarySeat.SeatLayoutItem
import cn.xuhongxu.xiaoya.R

/**
 * Created by xuhon on 5/18/2017.
 */

class SeatLayoutItemRecycleAdapter(val context: Context,
                                   val items : List<SeatLayoutItem>,
                                   val listener : OnClickListener)
    : RecyclerView.Adapter<SeatLayoutItemRecycleAdapter.ItemHolder>() {

    interface OnClickListener {
        fun OnClicked(item : SeatLayoutItem)
    }

    override fun onBindViewHolder(holder: ItemHolder?, position: Int) {

        if (position >= items.size) return
        val item = items[position]

        holder!!.name.text = item.name
        holder.button.text = item.name

        holder.button.setBackgroundColor(Color.parseColor("#aaffaa"))
        holder.button.setTextColor(Color.parseColor("#000000"))

        if (item.status == "IN_USE") {
            holder.button.setBackgroundColor(Color.parseColor("#ffaaaa"))
        } else if (item.status == "FREE") {
            if (item.power) {
                holder.button.setBackgroundColor(Color.parseColor("#aaffdd"))
                holder.button.setTextColor(Color.parseColor("#0000ff"))
            }
        } else if (item.status == "BOOKED") {
            holder.button.setBackgroundColor(Color.parseColor("#ddaa88"))
        } else if (item.status == "AWAY") {
            holder.button.setBackgroundColor(Color.parseColor("#ffdd99"))
        }

        if (item.type == "seat") {
            holder.name.visibility = GONE
            holder.button.visibility = VISIBLE
        }
        else {
            holder.button.visibility = GONE
            holder.name.visibility = VISIBLE
        }

        holder.button.setOnClickListener {
            listener.OnClicked(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val itemView = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.seat_layout_grid_item, parent, false)
        return ItemHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        val layout = itemView!!.findViewById(R.id.layout) as LinearLayout
        val name = itemView!!.findViewById(R.id.name) as TextView
        val button = itemView!!.findViewById(R.id.name_button) as Button
    }
}