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
        db.execSQL(context.getString(R.string.sql_command_create_table, TABLE_NAME, ID, DISH_NAME))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(context.getString(R.string.sql_command_drop_table, TABLE_NAME))
        onCreate(db)
    }

    fun addDishesToDatabase(dishList: MutableList<Dish>) {
        val db = this.writableDatabase
        db.execSQL(context.getString(R.string.sql_command_clear_table, TABLE_NAME))
        dishList.forEach{
            val values = ContentValues()
            values.put(ID, it.id)
            values.put(DISH_NAME, it.name)
            db.insert(TABLE_NAME, null, values)
        }
        db.close()
    }

    fun getDishesFromDatabase() : MutableList<Dish>{
        val list : MutableList<Dish> = mutableListOf()
        val db = this.readableDatabase
        val cursor = db.rawQuery(context.getString(R.string.sql_command_select, TABLE_NAME), null)
        cursor.moveToFirst()
        while(!cursor.isAfterLast){
            list.add(Dish(list.size,cursor.getString(cursor.getColumnIndex(DISH_NAME))))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }

    companion object {
        const val ID = "ID"
        const val DATABASE_NAME = "DishesDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "dishesList"
        const val DISH_NAME = "DISH_NAME"
    }
}