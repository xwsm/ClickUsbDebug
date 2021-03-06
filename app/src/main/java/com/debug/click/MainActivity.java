package com.debug.click;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.click.service.HttpService;
import com.nanchen.compresshelper.CompressHelper;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * @author M
 */
public class MainActivity extends AppCompatActivity {
    TextView textView;
    String TAG = this.getClass().getSimpleName();
    //    HttpService httpService = new HttpService(9999, this);
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        onClick();
//        onHttp();
        ip_txt = findViewById(R.id.ip_txt);
        ip_txt.setText("??????");
        ip_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test();
                //checkPermission(MainActivity.this);
            }
        });
//        handler.sendEmptyMessageDelayed(1,1000);
        ip_txt.setText("??????:" + new Date().toLocaleString().toString());

        getPermission();




        Intent intent = new Intent(MainActivity.this, NotificationService.class);
        //??????service??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

//        new ThreadClass(400,400).start();
//        startActivity(new Intent(this,TestActivity.class));


    }
    public static void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //??????
        kl.disableKeyguard();
        //???????????????????????????
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //??????PowerManager.WakeLock??????,???????????????|???????????????????????????,????????????LogCat?????????Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //????????????
        wl.acquire();
        //??????
        wl.release();
    }
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
    };

    TextView ip_txt;
    Server server;

    private void onHttp() {
        try {
            //httpService.start();
            server = AndServer.webServer(this)
                    .port(9999)
                    .timeout(20, TimeUnit.SECONDS)
                    .build();
            // startup the server.
            server.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int i = 0;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            i++;
            if (i == 10) {
                i = 1;
            }
            Log.i("ABCD", "?????????" + i);
            getScreen();

            ip_txt.setText("Num:" + new Random().nextInt(999));

            return false;
        }
    });

    public int getWindowWidthHeight(int type) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return type == 0 ? width : height;
        //return width + "*" + height;
    }

    String screen_original = "mnt/sdcard/web/" + "screen_original_1.png";

    void getScreen() {
        Log.i("ABCD", "??????2???" + i);
        screen_original = "mnt/sdcard/web/" + "screen_original_" + i + ".png";
        String result = new ExeCommand().run(
                "su", -1).run("screencap -p " + screen_original, -1).getResult();

        int j = (i > 1 ? i - 1 : 9);
        File newFile = new CompressHelper.Builder(MainActivity.this)
//                .setMaxWidth(720)  // ?????????????????????720
//                .setMaxHeight(960) // ?????????????????????960
                .setQuality(25)
                // ?????????????????????80
                .setFileName("screen_v" + j + "")
                // ?????????????????????????????????
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // ?????????????????????jpg??????
                .setDestinationDirectoryPath("mnt/sdcard/web/")
                .build()
                .compressToFile(new File(screen_original));
        handler.sendEmptyMessageDelayed(1, 1);
    }

    /*
     * ????????????app
     */
    public static void startAPP(String appPackageName) {
        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result2 = new ExeCommand().run("am force-stop " + appPackageName, 1000).getResult();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
                            context.startActivity(intent);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    String result = new ExeCommand().run("input tap " + 320 + " " + 1390, 1000).getResult();


                                }
                            }, 3000);


                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(context, "????????????", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static String getImage() {
        Log.i("ABCD", "??????3???" + i);
        int j = (i > 1 ? i - 1 : 9);

        String imgString = "mnt/sdcard/web/" + "screen_v" + j + ".jpeg";
        Log.i("ABCDiMG", imgString);
        return imgString;
    }


    void luBan() {
        try {
            String filePath = "mnt/sdcard/web/" + "screen_v" + i + ".png";
            Luban.with(this)
                    .load(screen_original)
                    .ignoreBy(20)
                    .setFocusAlpha(true)
                    .setTargetDir("mnt/sdcard/web/")
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    }).setRenameListener(new OnRenameListener() {
                @Override
                public String rename(String f) {
                    Log.i("ABCD", "" + f);
                    Log.i("ABCD", "" + filePath);
                    return "screen_v" + i + ".png";
                }
            })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO ???????????????????????????????????????????????? loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO ??????????????????????????????????????????????????????
                            Log.i("ABCD", "" + file.getPath());
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO ????????????????????????????????????
                            e.printStackTrace();
                        }
                    }).launch();


        } catch (Exception e) {

        }

    }

    void onClick() {
        textView = findViewById(R.id.text);

        if (!DebugClickService.isStart()) {
            textView.setText("????????????????????????");
        } else {
            textView.setText("????????????????????????");
        }
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.do_command).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExeCommand exeCommand = new ExeCommand();
                String result = exeCommand.run("su", 2000)
                        .run("setprop service.adb.tcp.port 5555", 2000)
                        .run("stop adbd", 2000)
                        .run("start adbd", 2000).getResult();
                Log.i(TAG, result);
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //getScreenshot();
//                String filePath = "mnt/sdcard/" + System.currentTimeMillis() + ".png";
//                new ExeCommand().run(
//                        "su", 1000).run("screencap -p " + filePath, 2000);
//            }
//        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * ????????????
     *
     * @return ?????????????????????
     */
    public static String getScreenshot() {
//        Process process = null;
        String filePath = "mnt/sdcard/" + System.currentTimeMillis() + ".png";
//        try {
//            process = Runtime.getRuntime().exec("su");
//            PrintStream outputStream = null;
//            outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
//            outputStream.println("screencap -p " + filePath);


//            outputStream.flush();
//            outputStream.close();
//            process.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(process != null){
//                process.destroy();
//            }
//        }
        return filePath;
    }


    public void getPermission() {
        String[] array = getUsesPermission();
        if (array != null && Build.VERSION.SDK_INT >= 23) {
            requestPermissions(array, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * ??????manifests???????????????
     *
     * @return
     */
    private String[] getUsesPermission() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] usesPermissionsArray = packageInfo.requestedPermissions;
            return usesPermissionsArray;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!DebugClickService.isStart()) {
                textView.setText("????????????????????????");
            } else {
                textView.setText("????????????????????????");
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}