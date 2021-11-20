package com.example.savethedish.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.R
import com.example.savethedish.data.DishesSQLiteHelper
import com.example.savethedish.databinding.CustomDialogBinding
import com.example.savethedish.main.DishesActivity
import com.example.savethedish.model.Dish
import kotlinx.android.synthetic.main.dish_item.view.*

class DishesRecyclerViewAdapter(
    private var data: MutableList<Dish>,
    private val showExtraOptions: Boolean,
    private val clickListener: () -> Unit
) :
    RecyclerView.Adapter<DishesRecyclerViewAdapter.DishesViewHolder>() {

    private lateinit var dishesSQLiteHelper: DishesSQLiteHelper
    private lateinit var dialogViewBinding: CustomDialogBinding
    private lateinit var dialog: Dialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishesViewHolder {
        dishesSQLiteHelper = DishesSQLiteHelper(parent.context, null)
        dialogViewBinding = CustomDialogBinding.inflate((parent.context as Activity).layoutInflater)
        setupDialog()
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
            else {
                edit_button.visibility = View.INVISIBLE
                delete_button.visibility = View.INVISIBLE
            }
            dish_name.text = dish.name
            val ingredientsString = getIngredientsString(dish)
            dish_ingredients.text = ingredientsString
            delete_button.setOnClickListener { deleteDish(dish, adapterPosition, context) }
            edit_button.setOnClickListener { dishEdited(dish, ingredientsString) }
        }
    }

    private fun setupDialog() {
        val layoutParams = WindowManager.LayoutParams()
        dialog = Dialog(dialogViewBinding.addDishButton.context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogViewBinding.root)
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        dialog.window?.attributes = layoutParams
    }

    private fun getIngredientsString(dish: Dish): StringBuilder {
        val ingredientsString = StringBuilder("")
        for (i in dish.ingredients.indices) {
            ingredientsString.append(dish.ingredients[i])
            if (i != dish.ingredients.size - 1) {
                ingredientsString.append(", ")
            }
        }
        return ingredientsString
    }

    private fun deleteDish(dish: Dish, adapterPosition: Int, context: Context) {
        dishesSQLiteHelper.deleteDishFromDatabase(dish)
        data = dishesSQLiteHelper.getDishesFromDatabase()
        notifyItemRemoved(adapterPosition)
        if (data.size == 0) {
            (context as DishesActivity).viewBinding.noDishesLayout.visibility = View.VISIBLE
            context.viewBinding.nestedScrollView.visibility = View.GONE
        }
    }

    private fun dishEdited(dish: Dish, ingredientsString: StringBuilder) {
        with(dialogViewBinding) {
            cancelButton.setOnClickListener { dialog.dismiss() }
            dishNameEditText.setText(dish.name)
            dishIngredientsEditText.setText(ingredientsString)
            dishIngredientsEditText.clearFocus()
            addDishButton.isEnabled = true
            addDishButton.setText(R.string.confirm_changes)
            addDishButton.setOnClickListener {
                dishesSQLiteHelper.updateDishInDatabase(
                    dish.name,
                    dishNameEditText.text.toString(),
                    dishIngredientsEditText.text.toString()
                )
                clickListener()
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun getItemCount() = data.size
}