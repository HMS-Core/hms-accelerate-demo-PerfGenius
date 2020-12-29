/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.perfgenius.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.perfgenius.R;
import com.huawei.perfgenius.jni.PerfGeniusJNI;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This PerfGeniusApiActivity is used to test PerfGeniusApi.
 *
 * @since 2020-08-01
 */
public class PerfGeniusApiActivity extends AppCompatActivity implements View.OnClickListener,
            PerfGeniusJNI.OnSystemEventListener, PerfGeniusJNI.OnPerformanceTracerListener {
    private static final String TAG = "PerfGeniusApiActivity";
    private static final String SYSTEM_EVENT = "system_event";
    private static final String PERFORMANCE_TRACER = "performance_tracer";
    private static final String UNREGISTER_SYSTEM_EVENT = "unregister_system_event";
    private static final String UNREGISTER_PERFORMANCE_TRACER = "unregister_performance_tracer";
    private static final int MESSAGE_SYSTEM_EVENT = 1;
    private static final int MESSAGE_PERFORMANCE_TRACER = 2;
    private static final int MESSAGE_UNREGISTER_SYSTEM_EVENT = 3;
    private static final int MESSAGE_UNREGISTER_PERFORMANCE_TRACER = 4;
    private static final int THREAD_POOL_CAPACITY = 100;
    private static final int THREAD_POOL_CORE_POOL_SIZE = 2;
    private static final int THREAD_POOL_MAX_POOL_SIZE = 5;
    private static final long THREAD_POOL_KEEP_ALIVE_TIME = 10L;
    private static final int TOAST_X_OFFSET = 0;
    private static final int TOAST_Y_OFFSET = 24;

    private TextView resultTextView;
    private EditText setFrameRateEdittext;
    private EditText setSceneEditText;
    private EditText tidsEditText;
    private EditText sampleRateEditText;
    private Button getApiVersionButton;
    private Button setFrameRateButton;
    private Button resetFrameRateButton;
    private Button setSceneButton;
    private Button getCurrentFrameRateButton;
    private Button getSupportedFrameRateButton;
    private Button getPerformanceLevelButton;
    private Button addKeyThreadsButton;
    private Button removeKeydThreads;
    private Button registerSystemEventCallbackButton;
    private Button unRegisterSystemEventCallbackButton;
    private Button registerPerformanceTracerButton;
    private Button unRegisterPerformanceTracerButton;

    private StringBuilder showResult = new StringBuilder();
    private Myhandler mHandler = new Myhandler();
    private ThreadPoolExecutor unRegisterThreadPool;

    class Myhandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SYSTEM_EVENT:
                    int systemEvent = msg.getData().getInt(SYSTEM_EVENT);
                    Log.i(TAG, "handleMessage() systemEvent = " + systemEvent);
                    showResult.append("systemEvent:").append(systemEvent).append("    ");
                    resultTextView.setText(showResult);
                    break;
                case MESSAGE_PERFORMANCE_TRACER:
                    String dataStr = msg.getData().getString(PERFORMANCE_TRACER);
                    Log.i(TAG, "handleMessage() dataStr = " + dataStr);
                    showResult.append("dataStr:").append(dataStr).append("    ");
                    resultTextView.setText(showResult);
                    break;
                case MESSAGE_UNREGISTER_SYSTEM_EVENT:
                    int resultEvent = msg.getData().getInt(UNREGISTER_SYSTEM_EVENT);
                    Log.i(TAG, "handleMessage() UnRegisterSystemEventCallback: result = " + resultEvent);
                    showResult.append("UnRegisterSystemEventCallback:").append(resultEvent).append("    ");
                    resultTextView.setText(showResult);
                    break;
                case MESSAGE_UNREGISTER_PERFORMANCE_TRACER:
                    int resultTracer = msg.getData().getInt(UNREGISTER_PERFORMANCE_TRACER);
                    Log.i(TAG, "handleMessage() UnRegisterPerformanceTracer: result = " + resultTracer);
                    showResult.append("UnRegisterPerformanceTracer:").append(resultTracer).append("    ");
                    resultTextView.setText(showResult);
                    break;
                default:
                    Log.w(TAG, "Handle an error message. msg = " + msg);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfgenius_api);
        Log.i(TAG, "onCreate()");
        initView();
        initData();
    }

    private void initView() {
        // Show result & Clear result
        resultTextView = findViewById(R.id.result_textview);
        Button clearResultButton = findViewById(R.id.clear_result_button);
        clearResultButton.setOnClickListener(this);

        // Get api version
        getApiVersionButton = findViewById(R.id.get_api_version_button);
        getApiVersionButton.setOnClickListener(this);

        // Set FrameRate & Get FrameRate
        setFrameRateEdittext = findViewById(R.id.set_frame_rate_edittext);
        setSceneEditText = findViewById(R.id.set_scene_edittext);
        setFrameRateButton = findViewById(R.id.set_frame_rate_button);
        setFrameRateButton.setOnClickListener(this);

        resetFrameRateButton = findViewById(R.id.reset_frame_rate_button);
        resetFrameRateButton.setOnClickListener(this);

        setSceneButton = findViewById(R.id.set_scene_button);
        setSceneButton.setOnClickListener(this);

        getCurrentFrameRateButton = findViewById(R.id.get_current_frame_rate_button);
        getCurrentFrameRateButton.setOnClickListener(this);

        getSupportedFrameRateButton = findViewById(R.id.get_supported_frame_rate_button);
        getSupportedFrameRateButton.setOnClickListener(this);

        // get PerformanceLevel
        getPerformanceLevelButton = findViewById(R.id.get_performance_level_button);
        getPerformanceLevelButton.setOnClickListener(this);

        // Add & Remove KeyThreads
        tidsEditText = findViewById(R.id.tids_edittext);
        addKeyThreadsButton = findViewById(R.id.add_key_threads_button);
        addKeyThreadsButton.setOnClickListener(this);
        removeKeydThreads = findViewById(R.id.remove_key_threads_button);
        removeKeydThreads.setOnClickListener(this);

        // SystemEvent Callback
        registerSystemEventCallbackButton = findViewById(R.id.register_system_event_callback_button);
        registerSystemEventCallbackButton.setOnClickListener(this);
        unRegisterSystemEventCallbackButton = findViewById(R.id.unregister_system_event_callback_button);
        unRegisterSystemEventCallbackButton.setOnClickListener(this);

        // PerformanceTracer Callback
        sampleRateEditText = findViewById(R.id.sample_rate_edittext);
        registerPerformanceTracerButton = findViewById(R.id.register_performance_tracer_button);
        registerPerformanceTracerButton.setOnClickListener(this);
        unRegisterPerformanceTracerButton = findViewById(R.id.unregister_performance_tracer_button);
        unRegisterPerformanceTracerButton.setOnClickListener(this);
    }

    private void initData() {
        // Init Perfgenius by JNI
        PerfGeniusJNI perfGeniusJNI = new PerfGeniusJNI();
        int so_Init_Result = perfGeniusJNI.init();
        Log.i(TAG, "Call Init method. result = " + so_Init_Result);
        if (so_Init_Result != 0) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Init failed. so_Init_Result = " + so_Init_Result + ", Don't test any API", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            disEnableAllBTN();
        }

        // Create ThreadPoolExecutor
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(THREAD_POOL_CAPACITY);
        unRegisterThreadPool = new ThreadPoolExecutor(THREAD_POOL_CORE_POOL_SIZE, THREAD_POOL_MAX_POOL_SIZE, THREAD_POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS, blockingQueue);
    }

    private void disEnableAllBTN() {
        getApiVersionButton.setEnabled(false);
        setFrameRateButton.setEnabled(false);
        resetFrameRateButton.setEnabled(false);
        setSceneButton.setEnabled(false);
        getCurrentFrameRateButton.setEnabled(false);
        getSupportedFrameRateButton.setEnabled(false);
        getPerformanceLevelButton.setEnabled(false);
        addKeyThreadsButton.setEnabled(false);
        removeKeydThreads.setEnabled(false);
        registerSystemEventCallbackButton.setEnabled(false);
        unRegisterSystemEventCallbackButton.setEnabled(false);
        registerPerformanceTracerButton.setEnabled(false);
        unRegisterPerformanceTracerButton.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_result_button:
                onClearResultClicked();
                break;
            case R.id.get_api_version_button:
                onGetApiVersionClicked();
                break;
            case R.id.set_frame_rate_button:
                onSetFrameRateClicked();
                break;
            case R.id.reset_frame_rate_button:
                onResetFrameRateClicked();
                break;
            case R.id.set_scene_button:
                onSetSceneClicked();
                break;
            case R.id.get_current_frame_rate_button:
                onGetCurrentFrameRateClicked();
                break;
            case R.id.get_supported_frame_rate_button:
                onGetSupportedFrameRateClicked();
                break;
            case R.id.get_performance_level_button:
                onGetPerformanceLevelClicked();
                break;
            case R.id.add_key_threads_button:
                onAddKeyThreadsClicked();
                break;
            case R.id.remove_key_threads_button:
                onRemoveKeyThreadsClicked();
                break;
            case R.id.register_system_event_callback_button:
                onRegisterSystemEventCallbackClicked();
                break;
            case R.id.unregister_system_event_callback_button:
                onUnRegisterSystemEventCallbackClicked();
                break;
            case R.id.register_performance_tracer_button:
                onRegisterPerformanceTracerClicked();
                break;
            case R.id.unregister_performance_tracer_button:
                onUnRegisterPerformanceTracerClicked();
                break;
            default:
                Log.w(TAG, "Clicked an error button: view = " + view);
                break;
        }
    }

    private void onClearResultClicked() {
        Log.i(TAG, "onClearResultClicked() showResult = " + showResult);
        showResult.delete(0, showResult.length());
        resultTextView.setText(showResult);
    }

    private void onGetApiVersionClicked() {
        String result = PerfGeniusJNI.getApiVersion();
        Log.i(TAG, "onGetApiVersionClicked() result = " + result);
        showResult.append("GetApiVersion:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onSetFrameRateClicked() {
        String frameRateText = setFrameRateEdittext.getText().toString();
        if (frameRateText.isEmpty()) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input FrameRate first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        int frameRate = Integer.parseInt(frameRateText);
        int result = PerfGeniusJNI.setFrameRate(frameRate);
        Log.i(TAG, "onSetFrameRateClicked() frameRate = " + frameRate + ", result = " + result);
        showResult.append("SetFrameRate:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onResetFrameRateClicked() {
        int result = PerfGeniusJNI.resetFrameRate();
        Log.i(TAG, "onResetFrameRateClicked() result = " + result);
        showResult.append("ResetFrameRate:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onSetSceneClicked() {
        String sceneText = setSceneEditText.getText().toString();
        if (sceneText.isEmpty()) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input scene first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        int result = PerfGeniusJNI.setScene(sceneText);
        Log.i(TAG, "onSetSceneClicked()  sceneText = " + sceneText + ", result = " + result);
        showResult.append("SetScene:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onGetCurrentFrameRateClicked() {
        int result = PerfGeniusJNI.getCurrentFrameRate();
        Log.i(TAG, "onGetCurrentFrameRateClicked() result = " + result);
        showResult.append("GetCurrentFrameRate:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onGetSupportedFrameRateClicked() {
        String result = PerfGeniusJNI.getSupportedFrameRate();
        Log.i(TAG, "onGetSupportedFrameRateClicked() result = " + result);
        showResult.append("GetSupportedFrameRate:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onGetPerformanceLevelClicked() {
        int result = PerfGeniusJNI.getPerformanceLevel();
        Log.i(TAG, "onGetPerformanceLevelClicked() result = " + result);
        showResult.append("GetPerformanceLevel:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onAddKeyThreadsClicked() {
        String tidsText = tidsEditText.getText().toString();
        if (tidsText.isEmpty()) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input KeyThreads first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        if (!checkTids(tidsText)) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input the correct KeyThreads.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        int result = PerfGeniusJNI.addKeyThreads(tidsText);
        Log.i(TAG, "onAddKeyThreadsClicked() tidsText = " + tidsText + ", result = " + result);
        showResult.append("AddKeyThreads:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private void onRemoveKeyThreadsClicked() {
        String tidsText = tidsEditText.getText().toString();
        if (tidsText.isEmpty()) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input KeyThreads first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        if (!checkTids(tidsText)) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input the correct KeyThreads.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        int result = PerfGeniusJNI.removeKeyThreads(tidsText);
        Log.i(TAG, "onRemoveKeyThreadsClicked() tidsText = " + tidsText + ", result = " + result);
        showResult.append("RemoveKeyThreads:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    private boolean checkTids(String tidsText) {
        String regex = "^[\\d,]+$";
        if (!tidsText.matches(regex)) {
            return false;
        }

        String[] tids = tidsText.split(",");
        int len = tids.length;
        for (int i = 0; i < len; i++) {
            if (tids[i].length() > 9) {
                return false;
            }
        }
        return true;
    }

    private void onRegisterSystemEventCallbackClicked() {
        // Set SystemEvent Listener
        PerfGeniusJNI.setOnSystemEventListener(this);

        int result = PerfGeniusJNI.registerSystemEventCallback();
        Log.i(TAG, "onRegisterSystemEventCallbackClicked() result = " + result);
        showResult.append("RegisterSystemEventCallback:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    @Override
    public void systemEventCallback(int systemEvent) {
        Log.i(TAG, "systemEventCallback() systemEvent = " + systemEvent);

        // Send the message to the UI thread
        Message message = Message.obtain();
        message.what = MESSAGE_SYSTEM_EVENT;
        Bundle bundle = new Bundle();
        bundle.putInt(SYSTEM_EVENT, systemEvent);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void onUnRegisterSystemEventCallbackClicked() {
        Log.i(TAG, "onUnRegisterSystemEventCallbackClicked()");
        Runnable runnable = () -> {
            int result = PerfGeniusJNI.unRegisterSystemEventCallback();
            Log.i(TAG, "onUnRegisterSystemEventCallbackClicked() result = " + result);

            // Send the message to the UI thread
            Message message = Message.obtain();
            message.what = MESSAGE_UNREGISTER_SYSTEM_EVENT;
            Bundle bundle = new Bundle();
            bundle.putInt(UNREGISTER_SYSTEM_EVENT, result);
            message.setData(bundle);
            mHandler.sendMessage(message);
        };
        unRegisterThreadPool.execute(runnable);
    }

    private void onRegisterPerformanceTracerClicked() {
        String sampleRateText = sampleRateEditText.getText().toString();
        if (sampleRateText.isEmpty()) {
            Toast toast = Toast.makeText(PerfGeniusApiActivity.this,
                    "Please input sampleRate first.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, TOAST_X_OFFSET, TOAST_Y_OFFSET);
            toast.show();
            return;
        }

        // Set PerformanceTracer Listener
        PerfGeniusJNI.setOnPerformanceTracerListener(this);

        int sampleRate = Integer.parseInt(sampleRateText);
        int result = PerfGeniusJNI.registerPerformanceTracer(sampleRate);
        Log.i(TAG, "onRegisterPerformanceTracerClicked() result = " + result);
        showResult.append("RegisterPerformanceTracer:").append(result).append("    ");
        resultTextView.setText(showResult);
    }

    @Override
    public void performanceTracerCallback(String dataStr) {
        Log.i(TAG, "performanceTracerCallback() dataStr = " + dataStr);

        // Send the message to the UI thread
        Message message = Message.obtain();
        message.what = MESSAGE_PERFORMANCE_TRACER;
        Bundle bundle = new Bundle();
        bundle.putString(PERFORMANCE_TRACER, dataStr);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void onUnRegisterPerformanceTracerClicked() {
        Log.i(TAG, "onUnRegisterPerformanceTracerClicked()");
        Runnable runnable = () -> {
            int result = PerfGeniusJNI.unRegisterPerformanceTracer();
            Log.i(TAG, "onUnRegisterPerformanceTracerClicked() result = " + result);

            // Send the message to the UI thread
            Message message = Message.obtain();
            message.what = MESSAGE_UNREGISTER_PERFORMANCE_TRACER;
            Bundle bundle = new Bundle();
            bundle.putInt(UNREGISTER_PERFORMANCE_TRACER, result);
            message.setData(bundle);
            mHandler.sendMessage(message);
        };
        unRegisterThreadPool.execute(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        unRegisterThreadPool.shutdown();
    }
}