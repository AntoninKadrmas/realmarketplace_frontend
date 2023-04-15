package com.realmarketplace.viewModel

import android.content.Context
import android.widget.Toast

/**
 * A group of *tool*.
 *
 * Object contains toast object for disable toast stacking in some places.
 */
object ToastObject {
    lateinit var myToast: Toast
    /**
     * A group of *tool*.
     *
     * Function used to show just one toast at the time. If some toast is show no other can be show until the previous is hidden.
     * @param context context of activity or fragment where is function called
     * @param message message that would be displayed in toast
     * @param timer is code how long would take the toast to disappear
     */
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