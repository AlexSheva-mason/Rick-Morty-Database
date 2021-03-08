package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitPackageManagerImpl
@Inject
constructor(
        private val application: Application
) : InitPackageManager {

    override fun getInstallingPackageName(): String? {
        with(application.packageName) {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                application.packageManager?.getInstallSourceInfo(this)?.installingPackageName
            } else {
                @Suppress("DEPRECATION")
                application.packageManager?.getInstallerPackageName(this)
            }
        }
    }

}