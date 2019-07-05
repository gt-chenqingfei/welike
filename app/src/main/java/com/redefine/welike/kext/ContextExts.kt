package com.redefine.welike.kext

import android.content.Context

fun Context.readAssets(fileName: String) = this.resources.assets.open(fileName).bufferedReader().use { it.readText() }