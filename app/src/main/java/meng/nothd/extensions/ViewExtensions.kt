package meng.nothd.extensions

import android.content.Context
import android.widget.Toast

/**
 * Created by meng on 2017/12/10.
 */

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}