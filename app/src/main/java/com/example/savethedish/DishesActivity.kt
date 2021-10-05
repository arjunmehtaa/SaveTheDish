package com.example.savethedish

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.databinding.ActivityMainBinding
import com.example.savethedish.databinding.CustomDialogBinding
import com.example.savethedish.model.Dish

class DishesActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityMainBinding
    private lateinit var dialogViewBinding: CustomDialogBinding
    private var list : MutableList<Dish> = mutableListOf()
    private lateinit var dialog: Dialog
    private lateinit var dishesSQLiteHelper: DishesSQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        dialogViewBinding = CustomDialogBinding.inflate(layoutInflater)
        dishesSQLiteHelper = DishesSQLiteHelper(this,null)
        setupViews()
    }

    private fun setupViews() {
        setupDialog()
        list = dishesSQLiteHelper.getDishesFromDatabase()
        setupRecycler()
        if (list.isNotEmpty()) viewBinding.noDishesLayout.visibility = View.GONE
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
        val adapter = DishesRecyclerViewAdapter(list)
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

    private fun addButtonClicked() {
        with(dialogViewBinding) {
            addDishButton.isEnabled = false
            dishNameEditText.text.clear()
            dishNameEditText.clearFocus()
        }
        dialog.show()
    }

    private fun addDishToList() {
        list.add(Dish(list.size,dialogViewBinding.dishNameEditText.text.toString()))
        if (list.isNotEmpty()) viewBinding.noDishesLayout.visibility = View.GONE
        viewBinding.recyclerView.adapter?.notifyItemInserted(list.size - 1)
        dialog.dismiss()
    }

    override fun onStop() {
        super.onStop()
        dishesSQLiteHelper.addDishesToDatabase(list)
    }
}