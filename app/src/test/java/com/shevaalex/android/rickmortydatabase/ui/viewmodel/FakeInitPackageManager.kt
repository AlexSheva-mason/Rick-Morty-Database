package com.shevaalex.android.rickmortydatabase.ui.viewmodel


class FakeInitPackageManager : InitPackageManager {

    override fun getInstallingPackageName(): String = "TestInstallerName"

}