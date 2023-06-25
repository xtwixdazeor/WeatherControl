package com.example.weathercontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapt (private val items: ArrayList<WeatherItem>): RecyclerView.Adapter<Adapt.ViewHolder>(){
    class ViewHolder(itemsView: View): RecyclerView.ViewHolder(itemsView){
        var date: TextView? = null
        var time: TextView? = null
        var weather: TextView? = null
        var temp: TextView? = null

        init{
            date = itemsView.findViewById(R.id.date)
            time = itemsView.findViewById(R.id.time)
            weather = itemsView.findViewById(R.id.weather)
            temp = itemsView.findViewById(R.id.temp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.weather_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.weather?.text = item.weather
        holder.date?.text = item.date
        holder.time?.text = item.time
        holder.temp?.text = item.temp.toString() + "°C"
    }
}