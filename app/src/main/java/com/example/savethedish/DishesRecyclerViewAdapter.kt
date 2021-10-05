package com.example.savethedish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.model.Dish
import kotlinx.android.synthetic.main.dish_item.view.*

class DishesRecyclerViewAdapter(private val data: MutableList<Dish>) :
    RecyclerView.Adapter<DishesRecyclerViewAdapter.DishesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishesViewHolder {
        return DishesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dish_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DishesViewHolder, position: Int) =
        holder.bind(data[position])

    inner class DishesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dish: Dish) = with(itemView) {
            dish_name.text = dish.name
            delete_button.setOnClickListener {
                data.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                if (data.size == 0) {
                    (context as DishesActivity).viewBinding.noDishesLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = data.size
}