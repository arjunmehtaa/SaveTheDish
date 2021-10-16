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
        viewBinding.myButton.setOnClickListener {
            val ing = viewBinding.myIng.text.toString()
            val ingredientsList: List<String> = ing.split(", ")
            youCanMakeList.clear()
            list.forEach {
                if (ingredientsList.containsAll(it.ingredients)) {
                    youCanMakeList.add(it)
                }
            }
            viewBinding.myIng.clearFocus()
            viewBinding.myIng.hideSoftInput()
            viewBinding.recyclerViewOne.adapter?.notifyDataSetChanged()
            if (youCanMakeList.isEmpty()) {
                viewBinding.noDishesToCookCard.visibility = View.VISIBLE
                if (viewBinding.myIng.text.toString() == "") {
                    viewBinding.noDishesToCookTextview.text =
                        resources.getString(R.string.list_the_ingredients)
                } else viewBinding.noDishesToCookTextview.text =
                    resources.getString(R.string.could_not_find_to_cook)
            } else {
                viewBinding.noDishesToCookCard.visibility = View.GONE
            }
        }
    }

    private fun setupViews() {
        setupDialog()
        list = dishesSQLiteHelper.getDishesFromDatabase()
        setupRecycler()
        if (list.isNotEmpty()) {
            viewBinding.noDishesLayout.visibility = View.GONE
            viewBinding.nestedScrollView.visibility = View.VISIBLE
        } else {
            viewBinding.noDishesLayout.visibility = View.VISIBLE
            viewBinding.nestedScrollView.visibility = View.GONE
        }
        viewBinding.addButton.setOnClickListener { addButtonClicked() }
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
        viewBinding.recyclerViewTwo.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewBinding.recyclerViewTwo.adapter = adapter
        val adapterYouCanMake = DishesRecyclerViewAdapter(youCanMakeList, false)
        viewBinding.recyclerViewOne.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewBinding.recyclerViewOne.adapter = adapterYouCanMake
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
        val ingredientsList: List<String> = ingredientsString.split(", ")
        dishesSQLiteHelper.addDishToDatabase(
            Dish(
                dialogViewBinding.dishNameEditText.text.toString(),
                ingredientsList
            )
        )
        list = dishesSQLiteHelper.getDishesFromDatabase()
        setupRecycler()
        if (list.isNotEmpty()) {
            viewBinding.noDishesLayout.visibility = View.GONE
            viewBinding.nestedScrollView.visibility = View.VISIBLE
        }
        viewBinding.recyclerViewTwo.adapter?.notifyItemInserted(list.size - 1)
        dialog.dismiss()
    }

    private fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}