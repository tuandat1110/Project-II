package com.example.projectii

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class LightAdapter(private val context: Context, private val lights: List<LightItem>) : BaseAdapter() {

    override fun getCount(): Int = lights.size

    override fun getItem(position: Int): LightItem = lights[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView as? LightControlView ?: LightControlView(context)
        // Cập nhật dữ liệu cho LightControlView
        view.setLightData(getItem(position))
        return view
    }
}
