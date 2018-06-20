package com.ae.andriod.popularmovies.Util;

import android.content.pm.PackageManager;

public class PackageCheck {

    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
