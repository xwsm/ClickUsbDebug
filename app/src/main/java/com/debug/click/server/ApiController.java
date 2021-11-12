package com.debug.click.server;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.debug.click.ExeCommand;
import com.debug.click.MainActivity;
import com.debug.click.NotificationService;
import com.debug.click.service.SwipeEvent;
import com.nanchen.compresshelper.CompressHelper;
import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.ResponseBody;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;

import java.io.File;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author M
 */
@RestController
@CrossOrigin
//@RequestMapping(path = "/")
public class ApiController {
    @GetMapping("/")
    public String login(HttpRequest request) {
        String uri = request.getURI();
        String type = request.getQuery("type");
        Log.i("ABCD", "type:" + type);
        if ("tap".equals(type)) {
            //点击
            String x = request.getQuery("x");
            String y = request.getQuery("y");
            String result = new ExeCommand().run("input tap " + x + " " + y, 1000).getResult();
        } else if ("swipe".equals(type)) {
            //触摸
            String x = request.getQuery("x");
            String y = request.getQuery("y");
            String x1 = request.getQuery("x1");
            String y1 = request.getQuery("y1");
            String duration = request.getQuery("duration");
//            NotificationService.toX_y(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(x1),Integer.parseInt(y1),1000);
            String result = new ExeCommand().run("input swipe " + x + " " + y + " " + x1 + " " + y1 + " " + (duration == null ? "" : duration), 1000).getResult();
        } else if ("key".equals(type)) {
            //按键
            String x = request.getQuery("x");
            String result = new ExeCommand().run("input keyevent " + x, 1000).getResult();
            if ("26".equals(x)) {
                new ExeCommand().run("am force-stop info.dvkr.screenstream", 1000).getResult();
                new ExeCommand().run("am start info.dvkr.screenstream/.ui.StartActivity", 1000).getResult();
            }

        } else if ("input".equals(type)) {
            String x = request.getQuery("x");
            String result = new ExeCommand().run("input text " + x, 1000).getResult();
        } else if ("info".equals(type)) {
            // 通过Resources获取
            // 获取屏幕的默认分辨率
            return (request.getQuery("callback") == null ? "console.log" : request.getQuery("callback")) + "({\"msg\":\"ok\",\"w\":" + NotificationService.getWindowWidthHeight(0) + ",\"h\":" + NotificationService.getWindowWidthHeight(1) + "})";
        } else if ("open".equals(type)) {
            NotificationService.startAPP("app.eleven.com.fastfiletransfer");
        } else if ("adb".equals(type)) {
            String x = request.getQuery("x");
            try {
                String result = new ExeCommand().run(x, 1000).getResult();
            } catch (Exception e) {
                return (request.getQuery("callback") == null ? "console.log" : request.getQuery("callback")) + "({\"msg\":\"错误:" + e.getMessage() + "\"})";
            }
        } else if ("screen".equals("type")) {
            String screen_original = "mnt/sdcard/web/" + "screen_original_0.png";
            String result = new ExeCommand().run(
                    "su", -1).run("screencap -p " + screen_original, -1).getResult();

        } else if ("screen".equals("type")) {

        }


        return (request.getQuery("callback") == null ? "console.log" : request.getQuery("callback")) + "({\"msg\":\"ok\"})";
    }


    @ResponseBody
    @GetMapping(path = "/screen", produces = "image/png")
    public FileBody getScreen(HttpResponse response) {
        Log.i("getScreen1", System.currentTimeMillis() + "");
        String screen_original = "";
        Log.i("ABCD22", System.currentTimeMillis() + "");
        try {
            screen_original = "mnt/sdcard/web/" + "screen_original_0.png";
//            Log.i("ABCD", System.currentTimeMillis() + "");
//            String result = new ExeCommand().run(
//                    "su", -1).run("screencap -p " + screen_original, -1).getResult();
//            Log.i("ABCD", System.currentTimeMillis() + "");

//            File file = new File(screen_original);
//            File newFile = new CompressHelper.Builder(NotificationService.context).setQuality(20).setCompressFormat(Bitmap.CompressFormat.JPEG).build().compressToFile(file);
            return new FileBody(NotificationService.getScreen());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileBody(new File(screen_original));
//        String img=MainActivity.getImage();
//        Log.i("ABCD",""+img);
//        File file=new File(img);
//        return new FileBody(file);
    }

    @ResponseBody
    @GetMapping(path = "/screen2", produces = "image/png")
    public FileBody getScreen2(HttpResponse response) {
        Log.i("getScreen2", System.currentTimeMillis() + "");
        String screen_original = "";
        Log.i("ABCD22", System.currentTimeMillis() + "");
        try {
            screen_original = "mnt/sdcard/web/" + "screen_original_0.png";
//            Log.i("ABCD", System.currentTimeMillis() + "");
//            String result = new ExeCommand().run(
//                    "su", -1).run("screencap -p " + screen_original, -1).getResult();
//            Log.i("ABCD", System.currentTimeMillis() + "");

//            File file = new File(screen_original);
//            File newFile = new CompressHelper.Builder(NotificationService.context).setQuality(20).setCompressFormat(Bitmap.CompressFormat.JPEG).build().compressToFile(file);
            return new FileBody(NotificationService.getScreen());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileBody(new File(screen_original));
//        String img=MainActivity.getImage();
//        Log.i("ABCD",""+img);
//        File file=new File(img);
//        return new FileBody(file);
    }


}
