package com.romail.romail.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.romail.romail.LoginActivity;
import com.romail.romail.R;
import com.romail.romail.entity.Account;

public class AlertDialogManager {

    public static Dialog createAlertDialog(final Context context, String msg) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.dialog_alert);
        TextView message = alertDialog.findViewById(R.id.tv_dialogtitle_activity_contacts);
        message.setText(msg);
        TextView confirm = alertDialog.findViewById(R.id.tv_confirm_dialog_activity_contacts);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
                Account.clearCurrentAccount();
            }
        });
        TextView deny = alertDialog.findViewById(R.id.tv_deny_dialog_activity_contacts);
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = (int)(((Activity)context).getWindowManager().getDefaultDisplay().getWidth() * 0.8);
        alertDialog.getWindow().setAttributes(params);
        alertDialog.setCanceledOnTouchOutside(false);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        alertDialog.getWindow().setBackgroundDrawable(dw);
        alertDialog.show();
        return alertDialog;
    }

    public static void showDialog(Dialog mDialogUtils){
        if (mDialogUtils != null && mDialogUtils.isShowing()) {
            mDialogUtils.show();
        }
    }

    public static void closeDialog(Dialog mDialogUtils) {
        if (mDialogUtils != null && mDialogUtils.isShowing()) {
            mDialogUtils.dismiss();
        }
    }

}
