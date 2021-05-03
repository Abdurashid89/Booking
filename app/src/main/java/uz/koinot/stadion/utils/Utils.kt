package uz.koinot.stadion.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import uz.koinot.stadion.R
import uz.koinot.stadion.app.App
import java.io.File


object Utils {

    fun hasInternetConnection(application: App): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun animationShimmer(context: Context?, view: View) {
        val animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val animationFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animationFadeIn.duration = 1000
        animationFadeOut.duration = 1000
        val animListener: AnimationListener = object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation) {
                if (animation === animationFadeIn) {
                    view.startAnimation(animationFadeOut)
                } else if (animation === animationFadeOut) {
                    view.startAnimation(animationFadeIn)
                }
            }
        }
        animationFadeIn.setAnimationListener(animListener)
        animationFadeOut.setAnimationListener(animListener)
        view.startAnimation(animationFadeOut)
    }

    fun toast(context: Context?, message: String?) {
        context?.let {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun toastLong(context: Context?, message: String?) {
        context?.let {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun toastNoConnection(context: Context?) {
        context?.let {
            Toast.makeText(context, "Internet bilan aloqa yoq!", Toast.LENGTH_SHORT).show()
//            Snackbar.make(view, "Profilga fotosurat joylash.", Snackbar.LENGTH_LONG)
//                    .setAction("Ok", null).show()
        }
    }

    fun <T> startActivity(context: Context?, activity: Class<T>) {
        context?.let {
            context.startActivity(Intent(context, activity))
        }
    }

    fun pxToDp(px: Int, context: Context?): Int {
        return if (context != null) {
            val resources = context.resources
            val metrics = resources.displayMetrics
            (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        } else {
            val metrics = Resources.getSystem().displayMetrics
            (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }
    }

    fun dpToPx(dp: Int, context: Context?): Int {
        return if (context != null) {
            val resources = context.resources
            val metrics = resources.displayMetrics
            (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        } else {
            val metrics = Resources.getSystem().displayMetrics
            (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
        }
    }

    fun permissionRecorderAudio(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(activity, permissions, 0)
                false
            } else
                true
        }
        return true
    }


    fun permissionDeviceGallery(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                )
                ActivityCompat.requestPermissions(activity, permissions, 0)
                false
            } else
                true
        }
        return true
    }

    fun permissionWriteStorage(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(activity, permissions, 0)
                false
            } else
                true
        }
        return true
    }


    fun expand(v: View) {
        val matchParentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
            (v.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec: Int = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight: Int = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight: Int = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    fun fadeOut(v: View) {
        val fadeOut: Animation = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.duration = 350
        fadeOut.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                v.visibility = View.GONE
            }
        })
        v.startAnimation(fadeOut)
    }

    fun fadeIn(v: View) {
        val fadeOut: Animation = AlphaAnimation(0f, 1f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.duration = 350
        fadeOut.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                v.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {}
        })
        v.startAnimation(fadeOut)
    }

    fun hideViewOutRight(context: Context?, view: View) {
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)
        //use this to make it longer:  animation.setDuration(1000);
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
            }
        })
        view.startAnimation(animation)
    }

    fun showViewInRights(context: Context?, view: View, view2: View) {
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        //use this to make it longer:  animation.setDuration(1000);
        view.visibility = View.VISIBLE
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view2.visibility = View.GONE
            }
        })
        view.startAnimation(animation)
    }

//    private lateinit var dialogProgress: Dialog
//    fun onProgress(context: Context) {
//        dialogProgress = Dialog(context, R.style.AppTheme)
//        dialogProgress.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        dialogProgress.setContentView(R.layout.progress)
//        dialogProgress.setCancelable(false)
//        dialogProgress.show()
//    }
//
//    fun dissmisProgress() {
//        dialogProgress.dismiss()
//    }

    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun checkContactsPermission(activity: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                102
            )
            false
        } else {
            true
        }
    }

    fun checkContactsPermissions(context: Context, fragment: Fragment): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fragment.requestPermissions(

                arrayOf(Manifest.permission.READ_CONTACTS),
                102
            )
            false
        } else {
            true
        }
    }


    fun checkFilesPermissions(context: Context, fragment: Fragment): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fragment.requestPermissions(

                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                102
            )
            false
        } else {
            true
        }
    }


//    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
//        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
//        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
//        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        vectorDrawable.draw(canvas)
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var width: Int = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height: Int = drawable.intrinsicHeight
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun navOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }


    fun expandNoDuration(v: View) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) WindowManager.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        v.startAnimation(a)
    }

    fun collapseNoDuration(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: Transformation?
            ) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        v.startAnimation(a)
    }

    fun deleteFilesDir(context: Context): Boolean {
        val filesDir = context.filesDir
        if (filesDir.isDirectory) {
            val children: Array<File>? = filesDir.listFiles()

            children?.forEach { file ->
                file.delete()
            }
        }
        return true
    }

    fun deleteCacheDir(context: Context): Boolean {
        val filesDirPath = context.cacheDir.path + "/luban_disk_cache"
        val filesDir = File(filesDirPath)
        if (filesDir.isDirectory) {
            val children: Array<File>? = filesDir.listFiles()

            children?.forEach { file ->
                if (file.name.startsWith("Ads_image"))
                    file.delete()
            }
        }
        return true
    }

    fun dimBackground(activity: Activity, view: View, dimAmount: Float) {
        if (dimAmount <= 0f) {
            view.alpha = 0f
        } else {
            view.alpha = dimAmount
        }
    }


}