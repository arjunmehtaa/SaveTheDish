package com.example.savethedish

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.savethedish.databinding.ActivityMainBinding
import com.example.savethedish.databinding.CustomDialogBinding

class MainActivity : AppCompatActivity() {

    lateinit var viewBinding : ActivityMainBinding
    private lateinit var dialogViewBinding : CustomDialogBinding
    private var list : MutableList<String> = mutableListOf()
    private lateinit var dialog : Dialog
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        dialogViewBinding = CustomDialogBinding.inflate(layoutInflater)
        sharedPref = getPreferences(Context.MODE_PRIVATE)
        setContentView(viewBinding.root)
        setupDialog()
        setupRecycler()
        setupListFromSharedPrefs()
        viewBinding.addButton.setOnClickListener { addButtonClicked() }
        dialogViewBinding.addDishButton.setOnClickListener { addDishButtonClicked() }
        dialogViewBinding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialogViewBinding.dishNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialogViewBinding.addDishButton.isEnabled = dialogViewBinding.dishNameEditText.text.isNotEmpty()
            }
        })
    }

    private fun setupDialog(){
        val layoutParams = WindowManager.LayoutParams()
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogViewBinding.root)
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        dialog.window?.attributes = layoutParams
    }

    private fun setupRecycler(){
        val adapter = DishesRecyclerViewAdapter(list)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        viewBinding.recyclerView.adapter = adapter
    }

    private fun addButtonClicked(){
        dialogViewBinding.addDishButton.isEnabled = false
        dialogViewBinding.dishNameEditText.text.clear()
        dialogViewBinding.dishNameEditText.clearFocus()
        dialog.show()
    }

    private fun addDishButtonClicked(){
        addDish(dialogViewBinding.dishNameEditText.text.toString())
        dialog.dismiss()
    }

    private fun addDish(dishName: String){
        list.add(dishName)
        if(list.isNotEmpty()) viewBinding.noDishesLayout.visibility = View.GONE
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun saveDataToSharedPrefs(){
        val csvList = StringBuilder()
        for (i in 0 until list.size) {
            csvList.append(list[i])
            if(i!=(list.size-1))  csvList.append(",")
        }
        sharedPref.edit().putString("dishesList", csvList.toString()).apply()
    }

    private fun setupListFromSharedPrefs(){
        val csvList: String = sharedPref.getString("dishesList", "")!!
        val items = csvList.split(",")
        for (i in items.indices) {
            if(items[i] != "") list.add(items[i])
        }
        if(list.isNotEmpty()) viewBinding.noDishesLayout.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        saveDataToSharedPrefs()
    }
}