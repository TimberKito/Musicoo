package com.player.musicoo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.player.musicoo.R

const val PRIVACY_POLICY_URL = "https://musicoo.app/privacy"
const val TERMS_OF_SERVICE_URL = "https://musicoo.app/terms"

fun openPrivacyPolicy(context: Context, privacyPolicyUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
    context.startActivity(intent)
}

fun openTermsOfService(context: Context, termsOfServiceUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
    context.startActivity(intent)
}

fun shareApp(context: Context) {
    val appPackageName = context.packageName
    val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
    val appPlayStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"

    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app: $appName")
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download $appName from Google Play: $appPlayStoreLink")
    context.startActivity(Intent.createChooser(shareIntent, "Share $appName via"))
}

fun sendFeedback(context: Context, email: String, subject: String) {
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "message/rfc822"
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    try {
        context.startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
    } catch (ex: android.content.ActivityNotFoundException) {
        Toast.makeText(context,"There is no app that supports sending emails",Toast.LENGTH_LONG).show()
    }
}