package com.debug.click;


import android.accessibilityservice.AccessibilityService;
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

//        List<CharSequence> charSequences = event.getText();
//        String allString = "";
//        for (CharSequence sequence : charSequences) {
//            allString = allString + "\n" + sequence.toString();
//        }
//        MainActivity.setText(allString + "-" + event.getPackageName());
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        if (rowNode == null) {
            Log.i(TAG, "noteInfo is　null");
            return;
        } else {
//            recycle(rowNode);
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

//            setKeyCount = 0;
//            setKeyCount2 = 0;
//            int count = getHandleKeyViewTextCount(rowNode);
//            if (count == 3) {
//                Log.i("ABCSS","充电提示");
//                clickChargeView(rowNode);
//            }
//
//            int count2 = getHandleKeyViewTextCount2(rowNode);
//            if (count2 == 3) {
//                Log.i("ABCSS","USB调试提示");
//                clickDebugView(rowNode);
//            }

        }
    }

    int setKeyCount = 0;

    int getHandleKeyViewTextCount(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            if (info.getText() != null && (info.getText().toString().contains("仅限充电") || info.getText().toString().contains("传输文件") || info.getText().toString().contains("传输照片"))) {
                setKeyCount++;
            }
//            if (info.getText() != null && info.getText().toString().contains("取消")) {
//                Log.i(TAG, "click:" + info.getWindowId());
//                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//            if (info.getText() != null && info.getText().toString().contains("确定")) {
//                Log.i(TAG, "click:" + info.getWindowId());
//                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    getHandleKeyViewTextCount(info.getChild(i));
                }
            }
        }
        return setKeyCount;

    }

    int setKeyCount2 = 0;

    int getHandleKeyViewTextCount2(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            if (info.getText() != null && (info.getText().toString().contains("允许USB调试吗") || info.getText().toString().contains("这台计算机的") || info.getText().toString().contains("一律允许使用这台计算机进行调试"))) {
                setKeyCount2++;
            }
//            if (info.getText() != null && info.getText().toString().contains("取消")) {
//                Log.i(TAG, "click:" + info.getWindowId());
//                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//            if (info.getText() != null && info.getText().toString().contains("确定")) {
//                Log.i(TAG, "click:" + info.getWindowId());
//                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    getHandleKeyViewTextCount2(info.getChild(i));
                }
            }
        }
        return setKeyCount2;

    }

    void clickChargeView(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            if (info.getText() != null && info.getText().toString().contains("取消")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    clickChargeView(info.getChild(i));
                }
            }
        }
    }

    void clickDebugView(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            if (info.getText() != null && info.getText().toString().contains("确定")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    clickDebugView(info.getChild(i));
                }
            }
        }
    }


    /**
     * 获取到页面布局
     *
     * @param info
     */
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());

            if (info.getText() != null && info.getText().toString().contains("取消")) {
                Log.i(TAG, "click:" + info.getWindowId());
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            if (info.getText() != null && info.getText().toString().contains("确定")) {
                Log.i(TAG, "click:" + info.getWindowId());
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(getApplication(), "%>_<%\r\n红包功能被迫中断", Toast.LENGTH_SHORT).show();
        Log.i("ABCD", "红包功能被迫中断");
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplication(), "%>_<%\r\n红包功能已关闭", Toast.LENGTH_SHORT).show();
        Log.i("ABCD", "红包功能已关闭");
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