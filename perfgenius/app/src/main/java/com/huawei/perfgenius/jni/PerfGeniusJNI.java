/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.perfgenius.jni;

import android.util.Log;

/**
 * This PerfGeniusJNI is used for JNI calls for PerfGeniusApi
 *
 * @since 2020-08-01
 */
public class PerfGeniusJNI {
    private static final String TAG = "PerfGeniusJNI";

    // Used to load the 'native-lib' library on application startup.
    static {
        Log.i(TAG, "Load libc++_shared.so begin");
        System.loadLibrary("c++_shared");
        Log.i(TAG, "Load libPerfgeniusApi.so begin");
        System.loadLibrary("PerfgeniusApi");
        Log.i(TAG, "Load libacckitjni-lib.so begin");
        System.loadLibrary("acckitjni-lib");
        Log.i(TAG, "Load libs end");
    }

    /**
     * Init Perfgenius by JNI
     *
     * @return The flag of the call result, 0 means success
     */
    public native int init();

    /**
     * Get current perfgenius api version
     *
     * @return Api version number
     */
    public static native String getApiVersion();

    /**
     * Set fps for frame rate
     *
     * @param fps Frame rate value
     * @return The flag of the call result, 0 means success
     */
    public static native int setFrameRate(int fps);

    /**
     * Reset fps for frame rate
     *
     * @return the flag of the call result, 0 means success
     */
    public static native int resetFrameRate();

    /**
     * Set scene description
     *
     * @param scene Scene description
     * @return The flag of the call result, 0 means success
     */
    public static native int setScene(String scene);

    /**
     * Get current frame rate
     *
     * @return The flag of the call result, 0 means success
     */
    public static native int getCurrentFrameRate();

    /**
     * Get supported frame rate
     *
     * @return Supported frame rate, e.g. 10 20 30 60 90
     */
    public static native String getSupportedFrameRate();

    /**
     * Get performance level
     *
     * @return The flag of the call result, 0 means success
     */
    public static native int getPerformanceLevel();

    /**
     * Add key threads
     *
     * @param tids Thread ID, each parameter ends with a comma
     * @return The flag of the call result, 0 means success
     */
    public static native int addKeyThreads(String tids);

    /**
     * Remove key threads
     *
     * @param tids Thread ID, each parameter ends with a comma
     * @return The flag of the call result, 0 means success
     */
    public static native int removeKeyThreads(String tids);

    /**
     * Register system event callback, e.g. GPU or CPU temperature
     *
     * @return The flag of the call result, 0 means success
     */
    public static native int registerSystemEventCallback();

    /**
     * Unregister system event callback, e.g. GPU or CPU temperature
     *
     * @return The flag of the call result, 0 means success
     */
    public static native int unRegisterSystemEventCallback();

    /**
     * Register performance tracer
     *
     * @param sampleRate Sample rate value
     * @return The flag of the call result, 0 means success
     */
    public static native int registerPerformanceTracer(int sampleRate);

    /**
     * Unregister performance tracer
     *
     * @return The flag of the call result, 0 means success
     */
    public static native int unRegisterPerformanceTracer();

    /**
     * Define system event callback interface
     */
    public interface OnSystemEventListener {
        void systemEventCallback(int systemEvent);
    }

    /**
     * Define performance tracer callback interface
     */
    public interface OnPerformanceTracerListener {
        void performanceTracerCallback(String dataStr);
    }

    private static OnSystemEventListener onSystemEventListener;
    private static OnPerformanceTracerListener onPerformanceTracerListener;

    /**
     * Set system event listener
     *
     * @param listener The listener object that implements OnSystemEventListener
     */
    public static void setOnSystemEventListener(OnSystemEventListener listener) {
        Log.i(TAG, "setOnSystemEventListener() listener = " + listener);
        onSystemEventListener = listener;
    }

    /**
     * Set performance tracer listener
     *
     * @param listener The listener object that implements OnPerformanceTracerListener
     */
    public static void setOnPerformanceTracerListener(OnPerformanceTracerListener listener) {
        Log.i(TAG, "setOnPerformanceTracerLListener() listener = " + listener);
        onPerformanceTracerListener = listener;
    }

    /**
     * The system event callback method
     *
     * @param systemEvent The value of system event
     */
    public static void systemEventCallback(int systemEvent) {
        Log.i(TAG, "systemEventCallback() onSystemEventListener = " + onSystemEventListener
                + ", systemEvent = " + systemEvent);
        if (onSystemEventListener != null) {
            onSystemEventListener.systemEventCallback(systemEvent);
        }
    }

    /**
     * The performance tracer callback method
     *
     * @param dataStr The value of performance
     */
    public static void performanceTracerCallback(String dataStr) {
        Log.i(TAG, "performanceTracerCallback() onPerformanceTracerListener = " + onPerformanceTracerListener
                + ", dataStr = " + dataStr);
        if (onPerformanceTracerListener != null) {
            onPerformanceTracerListener.performanceTracerCallback(dataStr);
        }
    }
}