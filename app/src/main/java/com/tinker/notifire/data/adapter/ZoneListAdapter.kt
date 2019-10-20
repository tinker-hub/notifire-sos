package com.tinker.notifire.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tinker.notifire.R
import com.tinker.notifire.data.model.ZoneItem

class ZoneListAdapter(
    val zoneList: ArrayList<ZoneItem>,
    val listener: OnSubscribeZoneListener
) :
    RecyclerView.Adapter<ZoneListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.zoneName.text = zoneList[position].name
        if (zoneList[position].subscribed) {
            holder.subscribeCheck.visibility = View.VISIBLE
        } else {
            holder.subscribeCheck.visibility = View.GONE
        }

        holder.layout.setOnClickListener {
            listener.onZoneItemClicked(zoneList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.layout_zone_item,
                    parent,
                    false
                )
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return zoneList.size
    }

    fun updateZone(zone: ZoneItem) {
        val position = zoneList.indexOf(zone)
        notifyItemChanged(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val zoneName: AppCompatTextView = itemView.findViewById(R.id.textview_zone_name)
        val subscribeCheck: ImageView = itemView.findViewById(R.id.imageview_subscribed)
        val layout: ConstraintLayout = itemView.findViewById(R.id.layout_zone_item)
    }


    interface OnSubscribeZoneListener {
        fun onZoneItemClicked(item: ZoneItem)
    }
}