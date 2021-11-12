package com.debug.click;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer(8081);
            }
        }).start();

    }

    //截屏转base64字符串
    public static String cap2base64(){
        int picWidth = 1080;
        int picHeight = 1920;
        String result = "";

        System.out.println("Starting screen capture...");

        long startTime = System.currentTimeMillis();

        String surfaceClassName = " ";
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }

        try {
            Bitmap bitmap;
            bitmap = (Bitmap) Class.forName(surfaceClassName).getDeclaredMethod("screenshot", new Class[]{Integer.TYPE, Integer.TYPE}).invoke(null, new Object[]{picWidth, picHeight});

            System.out.println(bitmap.getWidth() + "x" + bitmap.getHeight());
            System.out.println(bitmap.toString());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            baos.flush();
            baos.close();

            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

            long endTime = System.currentTimeMillis();
            System.out.println("Cost: " + (endTime - startTime) + "ms");
            System.out.println("Screen capture finished.");

        } catch (IllegalAccessException e) {
            System.out.println("1 error");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("2 error");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("3 error");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("4 error");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //http server
    public void startServer(int port){
        try {
            try (ServerSocket ss = new ServerSocket(port)) {
                while (true) {
                    Socket socket = ss.accept();

                    PrintWriter pw = new PrintWriter(socket.getOutputStream());

                    pw.println("HTTP/1.1 200 OK");
                    pw.println("Content-type:text/html");
                    pw.println();
                    pw.println("<head>" +
                            "<meta charset=\"utf-8\"/>" +
                            "<meta http-equiv=\"refresh\" content=\"0.25\">" +
                            "<title>Android Screen Mirror</title>" +
                            "</head>");
                    pw.println("<h2>A screen mirror tool for android by Wanyor.</h2>" );

                    String imgBase64 = cap2base64();
                    pw.println("<img src=\"data:image/png;base64," + imgBase64 + "\" width=\"480\" height=\"800\"/>");

                    pw.flush();
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
