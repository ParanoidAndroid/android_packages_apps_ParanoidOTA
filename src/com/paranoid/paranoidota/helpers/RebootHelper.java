/*
 * Copyright 2013 ParanoidAndroid Project
 *
 * This file is part of Paranoid OTA.
 *
 * Paranoid OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Paranoid OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Paranoid OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.paranoid.paranoidota.helpers;

import java.io.DataOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.paranoid.paranoidota.IOUtils;
import com.paranoid.paranoidota.InstallOptionsCursor;
import com.paranoid.paranoidota.R;
import com.paranoid.paranoidota.Utils;
import com.paranoid.paranoidota.helpers.RecoveryHelper.RecoveryInfo;

public class RebootHelper {

    private Context mContext;
    private RecoveryHelper mRecoveryHelper;

    public RebootHelper(Context context, RecoveryHelper recoveryHelper) {
        mContext = context;
        mRecoveryHelper = recoveryHelper;
    }

    private void showBackupDialog(final Context context, final String[] items,
            final String[] originalItems, final boolean wipeSystem, final boolean wipeData,
            final boolean wipeCaches) {

        double checkSpace = 1.0;// ManagerFactory.getPreferencesManager().getSpaceLeft();
        if (checkSpace > 0) {
            double spaceLeft = IOUtils.getSpaceLeft();
            if (spaceLeft < checkSpace) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.alert_backup_space_title);
                alert.setMessage(context.getResources().getString(
                        R.string.alert_backup_space_message, checkSpace));

                alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                        reallyShowBackupDialog(context, items, originalItems, wipeSystem, wipeData,
                                wipeCaches);
                    }
                });

                alert.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
            } else {
                reallyShowBackupDialog(context, items, originalItems, wipeSystem, wipeData,
                        wipeCaches);
            }
        } else {
            reallyShowBackupDialog(context, items, originalItems, wipeSystem, wipeData, wipeCaches);
        }
    }

    private void reallyShowBackupDialog(final Context context, final String[] items,
            final String[] originalItems, final boolean wipeSystem, final boolean wipeData,
            final boolean wipeCaches) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.alert_backup_title);
        View view = LayoutInflater.from(context).inflate(R.layout.backup_dialog,
                (ViewGroup) ((Activity) context).findViewById(R.id.backup_dialog_layout));
        alert.setView(view);

        final CheckBox cbSystem = (CheckBox) view.findViewById(R.id.system);
        final CheckBox cbData = (CheckBox) view.findViewById(R.id.data);
        final CheckBox cbCache = (CheckBox) view.findViewById(R.id.cache);
        final CheckBox cbRecovery = (CheckBox) view.findViewById(R.id.recovery);
        final CheckBox cbBoot = (CheckBox) view.findViewById(R.id.boot);
        final CheckBox cbSecure = (CheckBox) view.findViewById(R.id.androidsecure);
        final CheckBox cbSdext = (CheckBox) view.findViewById(R.id.sdext);
        final EditText input = (EditText) view.findViewById(R.id.backupname);

        input.setText(Utils.getDateAndTime());
        input.selectAll();

        if (mRecoveryHelper.getRecovery().getId() == R.id.twrp) {
            if (!mRecoveryHelper.hasAndroidSecure()) {
                cbSecure.setVisibility(View.GONE);
            }
            if (!mRecoveryHelper.hasSdExt()) {
                cbSdext.setVisibility(View.GONE);
            }
        } else {
            cbSystem.setVisibility(View.GONE);
            cbData.setVisibility(View.GONE);
            cbCache.setVisibility(View.GONE);
            cbRecovery.setVisibility(View.GONE);
            cbBoot.setVisibility(View.GONE);
            cbSecure.setVisibility(View.GONE);
            cbSdext.setVisibility(View.GONE);
        }

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

                String text = input.getText().toString();
                text = text.replace(" ", "");

                String backupOptions = null;
                if (mRecoveryHelper.getRecovery().getId() == R.id.twrp) {
                    backupOptions = "";
                    if (cbSystem.isChecked()) {
                        backupOptions += "S";
                    }
                    if (cbData.isChecked()) {
                        backupOptions += "D";
                    }
                    if (cbCache.isChecked()) {
                        backupOptions += "C";
                    }
                    if (cbRecovery.isChecked()) {
                        backupOptions += "R";
                    }
                    if (cbBoot.isChecked()) {
                        backupOptions += "B";
                    }
                    if (cbSecure.isChecked()) {
                        backupOptions += "A";
                    }
                    if (cbSdext.isChecked()) {
                        backupOptions += "E";
                    }

                    if ("".equals(backupOptions)) {
                        return;
                    }
                }

                reboot(context, items, originalItems, wipeSystem, wipeData, wipeCaches, text,
                        backupOptions);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void showRebootDialog(final Context context, final String[] items, final String[] originalItems) {

        if (items == null || items.length == 0) {
            return;
        }

        final RecoveryInfo recovery = mRecoveryHelper.getRecovery();

        mContext = context;

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        final InstallOptionsCursor installCursor = new InstallOptionsCursor(context);

        View view = LayoutInflater.from(context).inflate(R.layout.install_dialog,
                (ViewGroup) ((Activity) context).findViewById(R.id.install_dialog_layout));
        alert.setView(view);

        final TextView tvMessage = (TextView) view.findViewById(R.id.message);
        final CheckBox cbBackup = (CheckBox) view.findViewById(R.id.backup);
        final CheckBox cbWipeSystem = (CheckBox) view.findViewById(R.id.wipesystem);
        final CheckBox cbWipeData = (CheckBox) view.findViewById(R.id.wipedata);
        final CheckBox cbWipeCaches = (CheckBox) view.findViewById(R.id.wipecaches);

        cbWipeCaches.setText(recovery.getId() == R.id.stock ? R.string.wipe_cache : R.string.wipe_caches);

        if (installCursor.getCount() > 0) {
            alert.setTitle(R.string.alert_reboot_install_title);
        } else {
            alert.setTitle(R.string.alert_reboot_only_install_title);
        }
        cbBackup.setVisibility(installCursor.hasBackup() && recovery.getId() != R.id.stock ? View.VISIBLE
                : View.GONE);
        cbWipeSystem
                .setVisibility(installCursor.hasWipeSystem() && recovery.getId() != R.id.stock ? View.VISIBLE
                        : View.GONE);
        cbWipeData.setVisibility(installCursor.hasWipeData() ? View.VISIBLE : View.GONE);
        cbWipeCaches.setVisibility(installCursor.hasWipeCaches() ? View.VISIBLE : View.GONE);
        if (items.length == 1) {
            tvMessage.setText(context.getResources().getString(
                    R.string.alert_reboot_one_message, new Object[] { items[0] }));
        } else {
            tvMessage.setText(context.getResources().getString(
                    R.string.alert_reboot_more_message, new Object[] { items.length }));
        }

        if (!Utils.weAreInAospa()) {
            cbWipeData.setChecked(true);
            cbWipeCaches.setChecked(true);
        }

        alert.setPositiveButton(R.string.alert_reboot_now, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

                if (cbBackup.isChecked() && recovery.getId() != R.id.stock) {
                    showBackupDialog(context, items, originalItems, cbWipeSystem.isChecked(),
                            cbWipeData.isChecked(), cbWipeCaches.isChecked());
                } else {
                    reboot(context, items, originalItems, cbWipeSystem.isChecked(),
                            cbWipeData.isChecked(), cbWipeCaches.isChecked(), null, null);
                }
                installCursor.close();

            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void reboot(Context context, final String[] items, final String[] originalItems,
            final boolean wipeSystem, final boolean wipeData, final boolean wipeCaches,
            final String backupFolder, final String backupOptions) {

        if (wipeSystem) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(R.string.alert_wipe_system_title);
            alert.setMessage(R.string.alert_wipe_system_message);

            alert.setPositiveButton(R.string.alert_reboot_now,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();

                            _reboot(items, originalItems, wipeSystem, wipeData, wipeCaches,
                                    backupFolder, backupOptions);

                        }
                    });

            alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
        } else {
            _reboot(items, originalItems, wipeSystem, wipeData, wipeCaches, backupFolder,
                    backupOptions);
        }

    }

    private void _reboot(String[] items, String[] originalItems, boolean wipeSystem,
            boolean wipeData, boolean wipeCaches, String backupFolder, String backupOptions) {

        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());

            os.writeBytes("rm -f /cache/recovery/command\n");
            os.writeBytes("rm -f /cache/recovery/extendedcommand\n");
            os.writeBytes("rm -f /cache/recovery/openrecoveryscript\n");

            String file = mRecoveryHelper.getCommandsFile();

            String[] commands = mRecoveryHelper.getCommands(items, originalItems, wipeSystem,
                    wipeData, wipeCaches, backupFolder, backupOptions);
            if (commands != null) {
                int size = commands.length, i = 0;
                for (; i < size; i++) {
                    String comm = "echo";
                    if (i == size - 1
                            && mRecoveryHelper.getRecovery().getId() == R.id.stock) {
                        comm = "echo -n";
                    }
                    os.writeBytes(comm + " '" + commands[i]
                            + "' >> /cache/recovery/" + file + "\n");
                }
            }

            os.writeBytes("/system/bin/touch /cache/recovery/boot\n");
            os.writeBytes("reboot recovery\n");

            os.writeBytes("sync\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();

            if (Utils.isSystemApp(mContext)) {
                ((PowerManager) mContext.getSystemService(Activity.POWER_SERVICE))
                        .reboot("recovery");
            } else {
                Runtime.getRuntime().exec("/system/bin/reboot recovery");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}