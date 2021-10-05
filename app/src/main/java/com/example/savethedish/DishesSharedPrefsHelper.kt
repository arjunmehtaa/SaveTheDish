package com.example.savethedish

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class DishesSharedPrefsHelper(application: Application, private val list: MutableList<String>) {

    private val sharedPref: SharedPreferences = application.getSharedPreferences(
        DISH_LIST_KEY, Context.MODE_PRIVATE
    )

    fun setupListFromSharedPrefs(): MutableList<String> {
        val dishesStringList: String = sharedPref.getString(DISH_LIST_KEY, COMMA)!!
        val items = dishesStringList.split(COMMA)
        for (i in items.indices) {
            if (items[i].isNotEmpty()) list.add(items[i])
        }
        return list
    }

    fun saveDataToSharedPrefs() {
        val dishesStringList = StringBuilder()
        for (i in 0 until list.size) {
            dishesStringList.append(list[i])
            if (i != (list.size - 1)) dishesStringList.append(COMMA)
        }
        sharedPref.edit().putString(DISH_LIST_KEY, dishesStringList.toString()).apply()
    }

    companion object {
        const val DISH_LIST_KEY = "dishesList"
        const val COMMA = ","
    }
}