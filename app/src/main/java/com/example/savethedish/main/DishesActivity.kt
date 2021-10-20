package com.example.savethedish.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.R
import com.example.savethedish.data.DishesSQLiteHelper
import com.example.savethedish.databinding.ActivityMainBinding
import com.example.savethedish.databinding.CustomDialogBinding
import com.example.savethedish.model.Dish
import com.example.savethedish.ui.DishNameTextWatcher
import com.example.savethedish.ui.DishesRecyclerViewAdapter
import java.util.*

class DishesActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityMainBinding
    private lateinit var dialogViewBinding: CustomDialogBinding
    private var list: MutableList<Dish> = mutableListOf()
    private var youCanMakeList: MutableList<Dish> = mutableListOf()
    private lateinit var dialog: Dialog
    private lateinit var dishesSQLiteHelper: DishesSQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        dialogViewBinding = CustomDialogBinding.inflate(layoutInflater)
        dishesSQLiteHelper = DishesSQLiteHelper(this, null)
        setupViews()
    }

    private fun setupViews() {
        setupDialog()
        list = dishesSQLiteHelper.getDishesFromDatabase()
        setupRecycler()
        with(viewBinding) {
            if (list.isNotEmpty()) {
                noDishesLayout.visibility = View.GONE
                nestedScrollView.visibility = View.VISIBLE
            } else {
                noDishesLayout.visibility = View.VISIBLE
                nestedScrollView.visibility = View.GONE
            }
            addButton.setOnClickListener { addButtonClicked() }
            showDishesToCookButton.setOnClickListener { checkDishesToCookButtonClicked() }
        }
        with(dialogViewBinding) {
            addDishButton.setOnClickListener { addDishToList() }
            cancelButton.setOnClickListener { dialog.dismiss() }
            dishNameEditText.addTextChangedListener(DishNameTextWatcher(addDishButton, dishNameEditText))
        }
    }

    private fun setupDialog() {
        val layoutParams = WindowManager.LayoutParams()
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogViewBinding.root)
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        dialog.window?.attributes = layoutParams
    }

    private fun setupRecycler() {
        val adapter = DishesRecyclerViewAdapter(list, true)
        viewBinding.allDishesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewBinding.allDishesRecyclerView.adapter = adapter
        val adapterYouCanMake = DishesRecyclerViewAdapter(youCanMakeList, false)
        viewBinding.dishesToCookRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewBinding.dishesToCookRecyclerView.adapter = adapterYouCanMake
    }

    private fun addButtonClicked() {
        with(dialogViewBinding) {
            addDishButton.isEnabled = false
            dishNameEditText.text.clear()
            dishIngredientsEditText.text.clear()
            dishNameEditText.clearFocus()
            dishIngredientsEditText.clearFocus()
        }
        dialog.show()
    }

    private fun addDishToList() {
        val ingredientsString: String = dialogViewBinding.dishIngredientsEditText.text.toString()
        val ingredientsList: List<String> = ingredientsString.split(",")
        val finalIngredientsList: MutableList<String> = mutableListOf()
        ingredientsList.forEach { finalIngredientsList.add(it.trim()) }
        dishesSQLiteHelper.addDishToDatabase(
            Dish(
                dialogViewBinding.dishNameEditText.text.toString(),
                finalIngredientsList
            )
        )
        list = dishesSQLiteHelper.getDishesFromDatabase()
        setupRecycler()
        if (list.isNotEmpty()) {
            viewBinding.noDishesLayout.visibility = View.GONE
            viewBinding.nestedScrollView.visibility = View.VISIBLE
        }
        viewBinding.allDishesRecyclerView.adapter?.notifyItemInserted(list.size - 1)
        dialog.dismiss()
    }

    private fun checkDishesToCookButtonClicked() {
        with(viewBinding) {
            val ing = myIngredientsEdittext.text.toString()
            val ingredientsList: List<String> = ing.split(",")
            val finalIngredientsList: MutableList<String> = mutableListOf()
            ingredientsList.forEach { finalIngredientsList.add(it.trim().toLowerCase(Locale.ROOT)) }
            youCanMakeList.clear()
            list.forEach {
                val ingredientsToCompare =
                    it.ingredients.map { ingredient -> ingredient.toLowerCase(Locale.ROOT) }
                if (finalIngredientsList.containsAll(ingredientsToCompare)) {
                    youCanMakeList.add(it)
                }
            }
            viewBinding.myIngredientsEdittext.clearFocus()
            viewBinding.myIngredientsEdittext.hideSoftInput()
            viewBinding.dishesToCookRecyclerView.adapter?.notifyDataSetChanged()
            updateDishesYouCanCookCard()
        }
    }

    private fun updateDishesYouCanCookCard() {
        with(viewBinding) {
            if (youCanMakeList.isEmpty()) {
                noDishesToCookCard.visibility = View.VISIBLE
                if (myIngredientsEdittext.text.toString() == "") {
                    noDishesToCookTextview.text =
                        resources.getString(R.string.list_the_ingredients)
                } else noDishesToCookTextview.text =
                    resources.getString(R.string.could_not_find_to_cook)
            } else {
                noDishesToCookCard.visibility = View.GONE
            }
        }
    }

    private fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}