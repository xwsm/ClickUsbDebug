package com.debug.click;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //LogUtils.e("自启动了 ！！！！！");
            Log.i("ABCD","开机了");
//            Intent newIntent = new Intent(context, MainActivity.class);
//            // 要启动的Activity
//            //1.如果自启动APP，参数为需要自动启动的应用包名
//            //Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//            //这句话必须加上才能开机自动运行app的界面
//            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //2.如果自启动Activity
//            context.startActivity(newIntent);

            Intent intent2 = new Intent(context, NotificationService.class);
            //启动service服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent2);
            } else {
                context.startService(intent2);
            }
            //3.如果自启动服务
            //context.startService(newIntent);
        }
    }
}