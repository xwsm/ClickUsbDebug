package com.debug.click.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.debug.click.ExeCommand;
import com.debug.click.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * @author M
 */
public class HttpService extends NanoHTTPD {
    /**
     * 构造函数 赋值父类
     */
    public HttpService(int port, Context context) {
        super(port);
        this.context = context;
    }

    Context context;
    /**
     * 重写Serve方法，每次请求时会调用该方法
     */
    String filePath = "mnt/sdcard/web";
    String screen_compress = "mnt/sdcard/web/" + "screen_v1" + ".png";

    @Override
    public Response serve(IHTTPSession session) {
        try {
            //获取请求uri
            Log.i("ABCD", "type:" + session.getUri());
            String uri = session.getUri();
//            session.getHeaders().put("Access-Control-Allow-Headers", "");
            //resp.addHeader("Access-Control-Max-Age", "86400");
//            session.getHeaders().put("Access-Control-Max-Age", "0");
//            session.getHeaders().put("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
//            session.getHeaders().put("Access-Control-Allow-Origin", "*");
            String pathname = filePath + uri;
            if (uri.endsWith(".html") || uri.endsWith(".js")) {
                return FileStream(session, pathname);
            } else if (uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".jpeg") || uri.endsWith(".gif")) {
//            String filePath = "mnt/sdcard/web/" + "screen_v1" + ".png";
//            String result = new ExeCommand().run(
//                    "su", 1000).run("screencap -p " + filePath, 2000).getResult();
//            luBan();
//            return readImage(pathname);
            } else if (uri.endsWith("screen")) {
                return readImage(pathname);
            }
            String type = session.getParms().get("type");
            Log.i("ABCD", "type:" + type);
            if ("tap".equals(type)) {
                //点击
                String x = session.getParms().get("x");
                String y = session.getParms().get("y");
                String result = new ExeCommand().run("input tap " + x + " " + y, 1000).getResult();
            } else if ("swipe".equals(type)) {
                //触摸
                String x = session.getParms().get("x");
                String y = session.getParms().get("y");
                String x1 = session.getParms().get("x1");
                String y1 = session.getParms().get("y1");
                String duration = session.getParms().get("duration");

                String result = new ExeCommand().run("input swipe " + x + " " + y + " " + x1 + " " + y1 +" "+(duration==null?"":duration), 1000).getResult();
            } else if ("key".equals(type)) {
                //按键
                String x = session.getParms().get("x");
                String result = new ExeCommand().run("input keyevent " + x, 1000).getResult();
            }else if("input".equals(type)){
                String x = session.getParms().get("x");
                String result = new ExeCommand().run("input text " + x, 1000).getResult();
            } else if ("info".equals(type)) {
                // 通过Resources获取
                // 获取屏幕的默认分辨率
                return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", (session.getParms().get("callback") == null ? "console.log" : session.getParms().get("callback")) + "({\"msg\":\"ok\",\"w\":"+getWindowWidthHeight(0)+",\"h\":"+getWindowWidthHeight(1)+"})");
            }else if("x".equals(type)){

            }
            //这里默认把接收到的uri返回
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", (session.getParms().get("callback") == null ? "console.log" : session.getParms().get("callback")) + "({\"msg\":\"ok\"})");
        }catch (Exception e){
            e.printStackTrace();

            try {
                HttpService httpService=new HttpService(9999,context);
                httpService.start();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
       //return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "text/plain", (session.getParms().get("callback") == null ? "console.log" : session.getParms().get("callback")) + "({\"msg\":\"no\"})");
    }


    public int getWindowWidthHeight(int type) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return type==0?width:height;
        //return width + "*" + height;
    }

    void luBan() {
        String filePath = "mnt/sdcard/web/" + "screen_v1" + ".png";
        Luban.with(context)
                .load(filePath)
                .ignoreBy(20)
                .setFocusAlpha(true)
                .setTargetDir("mnt/sdcard/web/test")
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                }).setRenameListener(new OnRenameListener() {
            @Override
            public String rename(String filePath) {
                return filePath;
            }
        })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();


    }

    public Response FileStream(IHTTPSession session, String pathname) {
        try {

            FileInputStream fis = new FileInputStream(pathname);
            return NanoHTTPD.newChunkedResponse(Response.Status.OK, readHtml(pathname), fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return NanoHTTPD.newFixedLengthResponse("404 Not Found");
        }
    }

    Response readImage(String pathname) {
        FileInputStream fis = null;
        try {
            pathname = MainActivity.getImage();
            fis = new FileInputStream(pathname);
            return NanoHTTPD.newChunkedResponse(Response.Status.OK, "image/png", fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return NanoHTTPD.newFixedLengthResponse("404 Not Found");
    }

    private String readHtml(String pathname) {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(pathname), "UTF-8"));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (FileNotFoundException e) {
            //LogTools.e(TAG, "Missing operating system!");
            e.printStackTrace();
        } catch (IOException e) {
            //LogTools.e(TAG, "write error!");
            e.printStackTrace();
        }
        //LogTools.d(TAG, sb.toString());
        return sb.toString();
    }
}
