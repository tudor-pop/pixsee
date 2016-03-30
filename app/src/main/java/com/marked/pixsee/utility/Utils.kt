package com.marked.pixsee.utility

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.marked.pixsee.data.User
import com.marked.pixsee.data.database.database
import com.marked.pixsee.data.mapper.Mapper
import com.marked.pixsee.data.mapper.UserToCvMapper
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.async
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.onUiThread

/**
 * Created by Tudor Pop on 15-Nov-15.
 */
object Utils {
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun showNoConnectionDialog(ctx1: Context) {
        val ctx = ctx1
        val builder = AlertDialog.Builder(ctx)
        builder.setCancelable(true)
        builder.setMessage("You need to activate cellular or wifi network in order to login.")
        builder.setTitle("No internet !")
        builder.setPositiveButton("Wifi") { dialog, which -> ctx.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
        builder.setNegativeButton("Cellular") { dialog, which ->
            val intent = Intent()
            intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$DataUsageSummaryActivity")
            ctx.startActivity(intent)
        }

        builder.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })

        builder.setOnCancelListener(DialogInterface.OnCancelListener { return@OnCancelListener })

        builder.show()
    }


    fun getNumberDigits(inString: String): Int {
        if (inString.isNullOrBlank()) return 0
        return inString.filter { it.isDigit() }.length
    }
}
private val userToCv: Mapper<User, ContentValues> by lazy { UserToCvMapper() }
/**
 * Save the specified JsonArray to the specified table string
 * It has to be a Contact array for now
 * @param table In which table to save the array
 * @param jsonArray the array to save
 * */
fun Context.saveToTable(table: String, jsonArray: JsonArray) {
    async() {
        database.use {
            transaction {
                val gson = Gson()
                jsonArray.forEach {
                    insertWithOnConflict(table, null, userToCv.map(gson.fromJson(it, User::class.java)), SQLiteDatabase.CONFLICT_IGNORE)
                }
            }
        }
    }
}


inline fun Fragment.onUiThread(crossinline f: () -> Unit) {
    activity.onUiThread { f() }
}

fun <T : Fragment> AnkoAsyncContext<T>.supportFragmentUiThread(f: (T) -> Unit) {
    val fragment = weakRef.get() ?: return
    if (fragment.isDetached) return
    val activity = fragment.activity ?: return
    activity.runOnUiThread { f(fragment) }
}

@Deprecated("Use onUiThread() instead", ReplaceWith("onUiThread(f)"))
inline fun Fragment.uiThread(crossinline f: () -> Unit) {
    activity.onUiThread { f() }
}

inline fun Cursor.apply(block: Cursor.() -> kotlin.Unit): Cursor {
    moveToFirst()
    block()
    close()
    return this
}