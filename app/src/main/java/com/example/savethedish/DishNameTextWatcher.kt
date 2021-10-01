package com.example.savethedish

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class DishNameTextWatcher(private val button: Button, private val editText: EditText) :
    TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        button.isEnabled = editText.text.isNotEmpty()
    }
}