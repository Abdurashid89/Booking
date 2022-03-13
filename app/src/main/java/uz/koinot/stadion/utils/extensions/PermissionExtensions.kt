package uz.koinot.stadion.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import java.util.ArrayList

/**
 * Created by Shohboz Qoraboyev on 12,Февраль,2021
 */

fun Fragment.checkPermission(permission: String, granted: () -> Unit) {
    val mContext = context ?: return
    val options = Permissions.Options()
    options.setCreateNewTask(true)
    Permissions.check(
        mContext,
        arrayOf(permission),
        null,
        options,
        object : PermissionHandler() {
            override fun onGranted() {
                granted()
            }
        })
}

fun FragmentActivity.checkPermission(permission: String, granted: () -> Unit) {
    val mContext = this
    val options = Permissions.Options()
    options.setCreateNewTask(true)
    Permissions.check(
        mContext,
        arrayOf(permission),
        null,
        options,
        object : PermissionHandler() {
            override fun onGranted() {
                granted()
            }
        })
}

fun Fragment.checkPermissionState(permission: String, granted: () -> Unit,notGranted:()->Unit) {
    val mContext = context ?: return
    val options = Permissions.Options()
    options.setCreateNewTask(true)
    Permissions.check(
        mContext,
        arrayOf(permission),
        null,
        options,
        object : PermissionHandler() {
            override fun onGranted() {
                granted()
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                notGranted()
            }
        })
}
