package com.redefine.welike.commonui.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;

import com.redefine.welike.R;


/**
 * Created by honglin on 2018/5/29.
 */

public class ShortCutHelper {

    /**
     * crate shortcut
     */
    public static void createShortCut(Context context) {
        try {

            if (RomUtils.isEmui() || RomUtils.isMiui() || RomUtils.isOppo() || RomUtils.isVivo()) {
                return;
            }

            Intent shortcut = new Intent(
                    "com.android.launcher.action.INSTALL_SHORTCUT");

            Intent shortcutIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            if (shortcutIntent == null) return;

            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

            String title;
            try {
                final PackageManager pm = context.getPackageManager();
                title = pm.getApplicationLabel(
                        pm.getApplicationInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA)).toString();
            } catch (Exception e) {

                e.printStackTrace();

                return;
            }
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
            shortcut.putExtra("duplicate", false);
            Parcelable iconResource = Intent.ShortcutIconResource.fromContext(context,
                    R.mipmap.ic_launcher);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
            context.sendBroadcast(shortcut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
