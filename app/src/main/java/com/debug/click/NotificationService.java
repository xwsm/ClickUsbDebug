package com.debug.click;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.debug.click.service.SwipeEvent;
import com.nanchen.compresshelper.CompressHelper;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.framework.body.FileBody;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

    public String NOTIFICATION_CHANNEL_ID = "512s3";

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        try {
            Log.i("ABCD", "NotificationService oncreate");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // 从Android 8.0开始，需要注册通知通道
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "NotificationService", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(getResources().getString(R.string.app_name) + "正在运行").setContentText(new Date().toLocaleString().toString());

            Intent activityIntent = new Intent(this, EmptyActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            startForeground(new Random().nextInt(9999), builder.build());
            onHttp();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ABCD", "NotificationService ex：" + e.getMessage());
        }

    }

    int port = 9999;
    Server server;

    private void onHttp() {
        try {
            //httpService.start();
            server = AndServer.webServer(this)
                    .port(port)
                    .timeout(20, TimeUnit.SECONDS)
                    .build();
            // startup the server.
            server.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startAPP(String appPackageName) {
//        Looper.prepare();

//        String result2 = new ExeCommand().run("am force-stop " + appPackageName, -1).getResult();
//
//
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
//        context.startActivity(intent);
//
//        String result = new ExeCommand().run("input tap " + 320 + " " + 1390, 1000).getResult();
//        Looper.prepare();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////
////
//                        String result = new ExeCommand().run("input tap " + 320 + " " + 1390, 1000).getResult();
////
////
////                    }
////                }, 3000);
//
//
//            }
//        }, 1000);
//        Looper.loop();

    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
        Intent localIntent = new Intent();
        localIntent.setClass(this, NotificationService.class);
        this.startService(localIntent);
        super.onDestroy();
    }

    public static File getScreen() {
        String screen_original = "";
        try {
            screen_original = "mnt/sdcard/web/" + "screen_original_0.png";
            Log.i("ABCD", System.currentTimeMillis() + "");
            String result = new ExeCommand().run(
                    "su", -1).run("screencap -p " + screen_original, -1).getResult();
            Log.i("ABCD", System.currentTimeMillis() + "");

            File file = new File(screen_original);
            File newFile = new CompressHelper.Builder(NotificationService.context).setQuality(20).setCompressFormat(Bitmap.CompressFormat.JPEG).build().compressToFile(file);
            return newFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(screen_original);
    }

    public static int getWindowWidthHeight(int type) {
        try {
            PowerManager mPowerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            mWakeLock.acquire(60 * 1000L);
        } catch (Exception e) {

        }

        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels + getStatusBarHeight(context) + getNavigationBarHeight(context);
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
        return type == 0 ? screenWidth : screenHeight;
        //return width + "*" + height;
    }

    public final static boolean isScreenLocked() {
        android.app.KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        return !mKeyguardManager.inKeyguardRestrictedInputMode();

    }
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getNavigationBarHeight(Context activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static void toX_y(int fromX, int fromY, int toX, int toY, int step) {
        SwipeEvent swipeEvent = new SwipeEvent();
        swipeEvent.makeSwipeDown(context, fromX, fromY, toX, toY, 500);
    }
}
