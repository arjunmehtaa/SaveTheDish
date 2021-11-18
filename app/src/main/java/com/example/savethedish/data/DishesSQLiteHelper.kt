package com.example.savethedish.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.savethedish.R
import com.example.savethedish.model.Dish

class DishesSQLiteHelper(private val context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(context.getString(R.string.sql_command_create_table, TABLE_NAME, DISH_NAME, DISH_ING))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(context.getString(R.string.sql_command_drop_table, TABLE_NAME))
        onCreate(db)
    }

    fun addDishToDatabase(dish : Dish) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DISH_NAME, dish.name)
        val ingredientsString = StringBuilder("")
        for (i in dish.ingredients.indices) {
            ingredientsString.append(dish.ingredients[i])
            if (i != dish.ingredients.size - 1) {
                ingredientsString.append(",")
            }
        }
        values.put(DISH_ING, ingredientsString.toString())
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getDishesFromDatabase() : MutableList<Dish>{
        val list : MutableList<Dish> = mutableListOf()
        val db = this.readableDatabase
        val cursor = db.rawQuery(context.getString(R.string.sql_command_select, TABLE_NAME), null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast) {
            val dishName = cursor.getString(cursor.getColumnIndex(DISH_NAME))
            val ingredientsList = cursor.getString(cursor.getColumnIndex(DISH_ING)).split(",")
            val finalIngredientsList: MutableList<String> = mutableListOf()
            ingredientsList.forEach { finalIngredientsList.add(it.trim()) }
            list.add(Dish(dishName, finalIngredientsList))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }

    fun deleteDishFromDatabase(dish: Dish) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$DISH_NAME =?", arrayListOf<String>(dish.name).toTypedArray())
        db.close()
    }

    fun updateDishInDatabase(oldName: String, name: String, ingredients: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DISH_NAME, name)
        contentValues.put(DISH_ING, ingredients)
        db.update(TABLE_NAME, contentValues, "$DISH_NAME= ?", arrayOf(oldName))
    }

    companion object {
        const val DATABASE_NAME = "DishesDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "dishesList"
        const val DISH_NAME = "DISH_NAME"
        const val DISH_ING = "DISH_ING"
    }
}