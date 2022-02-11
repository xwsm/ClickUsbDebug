package com.debug.click;


import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

/**
 * @author M
 */
public class DebugClickService extends AccessibilityService {
    private final String TAG = getClass().getName();

    public static DebugClickService mService;

    /**
     * 初始化
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }

    /**
     * 实现辅助功能
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        if (rowNode == null) {
            Log.i(TAG, "noteInfo is　null");
            return;
        } else {
            List<AccessibilityNodeInfo> cancelBtn = rowNode.findAccessibilityNodeInfosByText("取消");
            List<AccessibilityNodeInfo> btn1 = rowNode.findAccessibilityNodeInfosByText("仅限充电");
            List<AccessibilityNodeInfo> btn2 = rowNode.findAccessibilityNodeInfosByText("传输文件");
            List<AccessibilityNodeInfo> btn3 = rowNode.findAccessibilityNodeInfosByText("传输照片");
            if(btn1.size()==1 && btn2.size()==1 && btn3.size()==1 && cancelBtn.size()==1){
                //充电提示取消按钮
                Toast.makeText(getApplication(), "取消充电提醒", Toast.LENGTH_SHORT).show();
                cancelBtn.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            List<AccessibilityNodeInfo> cancelBtn_usb = rowNode.findAccessibilityNodeInfosByText("取消");
            List<AccessibilityNodeInfo> submitBtn_usb = rowNode.findAccessibilityNodeInfosByText("确定");
            List<AccessibilityNodeInfo> btn1_usb = rowNode.findAccessibilityNodeInfosByText("允许USB调试吗");
            List<AccessibilityNodeInfo> btn2_usb = rowNode.findAccessibilityNodeInfosByText("这台计算机的");
            List<AccessibilityNodeInfo> btn3_usb = rowNode.findAccessibilityNodeInfosByText("一律允许使用这台计算机进行调试");
            if(btn1_usb.size()==1 && btn2_usb.size()==1 && btn3_usb.size()==1 && cancelBtn_usb.size()==1 && submitBtn_usb.size()==1){
                //USB调试确定按钮
                Toast.makeText(getApplication(), "取消调试提醒", Toast.LENGTH_SHORT).show();
                submitBtn_usb.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

        }
    }



    @Override
    public void onInterrupt() {
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }


    // 公共方法


    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return mService != null;
    }
}