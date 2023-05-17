package hu.bme.aut.ourtrips

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process

class AppRestarter(private val context: Context) {

    fun restart() {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
            Process.killProcess(Process.myPid())
        }
    }
}
