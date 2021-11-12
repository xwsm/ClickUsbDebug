package com.debug.click.service;

import android.app.Service;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SwipeEvent {

    /**
     * @param fromX 起始x坐标
     * @param fromY 起始y坐标
     * @param toX 结束x坐标
     * @param toY 结束y坐标
     * @param step 单次滑动长度
     */
    public void makeSwipeDown(Context service, int fromX, int fromY, int toX, int toY, int step) {
        InputManager inputManager = (InputManager) service.getSystemService(Context.INPUT_SERVICE);
        int y = fromY;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        // 模拟down
        MotionEvent motionEvent = null;
        motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, fromX, fromY, 0);
        // 将MotionEvent的输入源设置为InputDevice.SOURCE_TOUCHSCREEN，输入源为触摸屏幕
        motionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
        // mode为1，INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT
        invokeInjectInputEventMethod(inputManager, motionEvent, 1);

        // 模拟move
        int stepCount = (fromY - toY) / step;
        for (int i = 0; i < stepCount; i++) {
            y -= step;
            motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, fromX, y, 0);
            motionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            // mode为2，INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH
            invokeInjectInputEventMethod(inputManager, motionEvent, 2);
            Log.i("cwx", "y:" + y);
        }

        // 模拟up
        if (y <= toY) {
            motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, toX, y, 0);
            motionEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
            invokeInjectInputEventMethod(inputManager, motionEvent, 2);
        }
    }

    private void invokeInjectInputEventMethod(InputManager inputManager, InputEvent event, int mode) {
        Class<?> clazz = null;
        Method injectInputEventMethod = null;
        Method recycleMethod = null;

        try {
            clazz = Class.forName("android.hardware.input.InputManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            injectInputEventMethod = clazz.getMethod("injectInputEvent", InputEvent.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            injectInputEventMethod.invoke(inputManager, event, mode);
            // 准备回收event的方法
            recycleMethod = event.getClass().getMethod("recycle");
            //执行event的recycle方法
            recycleMethod.invoke(event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}

