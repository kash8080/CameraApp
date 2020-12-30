package com.km.cameraapp

import android.content.Context
import android.content.SharedPreferences

class Prefs {

    companion object{

        const val LAST_IMAGE="LAST_IMAGE";

        fun getPrefs(context: Context?):SharedPreferences?{
            return context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE)

        }

        fun setStringData(
            context: Context?,
            key: String,
            value: String
        ) {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return

            with (sharedPref.edit()) {
                putString(key, value)
                commit()
            }
        }

        fun getStringData(
            context: Context?,
            key: String
        ):String? {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return ""
            return sharedPref.getString(key, "")
        }

        fun setIntData(
            context: Context?,
            key: String,
            value: Int
        ) {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return

            with (sharedPref.edit()) {
                putInt(key, value)
                commit()
            }
        }

        fun getIntData(
            context: Context?,
            key: String
        ):Int {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return -1
            return sharedPref.getInt(key, -1)
        }

        fun setBooleanData(
            context: Context?,
            key: String,
            value: Boolean
        ) {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return

            with (sharedPref.edit()) {
                putBoolean(key, value)
                commit()
            }
        }

        fun getBooleanData(
            context: Context?,
            key: String
        ):Boolean {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return false
            return sharedPref.getBoolean(key,false)
        }

        fun setLongData(
            context: Context?,
            key: String,
            value: Long
        ) {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return

            with (sharedPref.edit()) {
                putLong(key, value)
                commit()
            }
        }

        fun getLongData(
            context: Context?,
            key: String
        ):Long {
            val sharedPref = context?.getSharedPreferences(context.resources.getString(R.string.curApplicationId)+".prefs_file_key", Context.MODE_PRIVATE) ?: return 0L
            return sharedPref.getLong(key,0L)
        }


        fun shouldRequestAgain(context: Context,key:String,duration:Long):Boolean{
            var lastAt= getLongData(context,key)
            return lastAt<System.currentTimeMillis()-duration
        }

    }


}