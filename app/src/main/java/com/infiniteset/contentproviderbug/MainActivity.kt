package com.infiniteset.contentproviderbug

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rawResId = R.raw.file_1
        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(rawResId))
            .appendPath(resources.getResourceTypeName(rawResId))
            .appendPath(resources.getResourceEntryName(rawResId))
            .build()

        val germanLocalContext = createLocaleConfigurationContext(Locale.GERMAN)
        val germanMediaPlayerResId = MediaPlayer.create(germanLocalContext, rawResId)
        val germanMediaPlayerUri = MediaPlayer().apply {
            setDataSource(germanLocalContext, uri)
            prepare()
        }

        val englishLocalContext = createLocaleConfigurationContext(Locale.ENGLISH)
        val englishMediaPlayerUri = MediaPlayer().apply {
            setDataSource(englishLocalContext, uri)
            prepare()
        }
        val englishMediaPlayerResId = MediaPlayer.create(englishLocalContext, rawResId)

        txt_output.text =
            "German media player:\n" +
                    "\t\t\tURI duration   : ${germanMediaPlayerUri.duration} (this value is the same as for English media player, " +
                    "while should the same as the value at the next line)\n" +
                    "\t\t\tRes id duration: ${germanMediaPlayerResId.duration}\n" +
                    "English media player\n" +
                    "\t\t\tURI duration    :${englishMediaPlayerUri.duration}\n" +
                    "\t\t\tRes id duration:: ${englishMediaPlayerResId.duration}"
    }
}

var Configuration.localeCompat: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales[0]
    } else {
        locale
    }
    set(locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocales(LocaleList(locale))
        } else {
            setLocale(locale)
        }
    }

fun Context.createLocaleConfigurationContext(locale: Locale): Context {
    val resConfig = Configuration(resources.configuration).apply {
        localeCompat = locale
    }
    return createConfigurationContext(resConfig)
}
