package com.example.savethedish.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.R
import com.example.savethedish.data.DishesSQLiteHelper
import com.example.savethedish.main.DishesActivity
import com.example.savethedish.model.Dish
import kotlinx.android.synthetic.main.dish_item.view.*

class DishesRecyclerViewAdapter(
    private var data: MutableList<Dish>,
    private val showExtraOptions: Boolean
) :
    RecyclerView.Adapter<DishesRecyclerViewAdapter.DishesViewHolder>() {

    private lateinit var dishesSQLiteHelper: DishesSQLiteHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishesViewHolder {
        dishesSQLiteHelper = DishesSQLiteHelper(parent.context, null)
        return DishesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dish_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DishesViewHolder, position: Int) =
        holder.bind(data[position])

    inner class DishesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dish: Dish) = with(itemView) {
            if (showExtraOptions) delete_button.visibility = View.VISIBLE
            else delete_button.visibility = View.INVISIBLE
            dish_name.text = dish.name
            val ingredientsString = StringBuilder("")
            for (i in dish.ingredients.indices) {
                ingredientsString.append(dish.ingredients[i])
                if (i != dish.ingredients.size - 1) {
                    ingredientsString.append(", ")
                }
            }
            dish_ingredients.text = ingredientsString
            delete_button.setOnClickListener {
                dishesSQLiteHelper.deleteDishFromDatabase(dish)
                data = dishesSQLiteHelper.getDishesFromDatabase()
                notifyItemRemoved(adapterPosition)
                if (data.size == 0) {
                    (context as DishesActivity).viewBinding.noDishesLayout.visibility = View.VISIBLE
                    (context as DishesActivity).viewBinding.nestedScrollView.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount() = data.size
}