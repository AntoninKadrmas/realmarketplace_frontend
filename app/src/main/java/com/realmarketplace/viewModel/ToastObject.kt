package com.realmarketplace.viewModel

import android.content.Context
import android.widget.Toast

object ToastObject {
    lateinit var myToast: Toast
    fun showToast(context: Context,message: String,timer:Int){
        try {
            myToast.view?.isShown // true if visible
            myToast.setText(message)
        } catch (e: Exception) {         // invisible if exception
            myToast = Toast.makeText(context, message, timer)
        }
        myToast.show()
    }
}