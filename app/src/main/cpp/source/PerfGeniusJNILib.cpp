/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */
#include <android/log.h>
#include <dlfcn.h>
#include <iostream>
#include <iterator>
#include <jni.h>
#include <sstream>
#include <string>
#include <unistd.h>
#include <utility>
#include <vector>

#include "../include/PerfGeniusApi.h"

constexpr int INIT_SO_SUCCESS = 0;
constexpr int DLOPEN_FAIL = -1001;
constexpr int DLSYM_FAIL = -1002;
constexpr int BUILD_FUNC_FAIL = -1003;
constexpr int KIT_ERROR = -1004;

PerfGeniusApi *g_kit;

/**
 * Init so library
 */
auto InitLibSo() -> int
{
    // Call dlopen method
    void *libAccKit = dlopen("libPerfgeniusApi.so", RTLD_LAZY);
    if (libAccKit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "InitLibSo : dlopen failed");
        dlclose(libAccKit);
        return DLOPEN_FAIL;
    }

    // Call dlsym method
    typedef PerfGeniusApi *(*BuildPerfGeniusApi)();
    BuildPerfGeniusApi buildFunc = reinterpret_cast<BuildPerfGeniusApi>(dlsym(libAccKit,
                                                                              "GetPerfGeniusApiHandle"));
    if (buildFunc == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "InitLibSo : dlsym failed");
        dlclose(libAccKit);
        return DLSYM_FAIL;
    }

    // Used to get function name in PerfGeniusApi.h
    g_kit = buildFunc();
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "InitLibSo : buildFunc failed");
        dlclose(libAccKit);
        return BUILD_FUNC_FAIL;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "InitLibSo : succeeded");
    dlclose(libAccKit);
    return INIT_SO_SUCCESS;
}

/**
 * Called by a non-static method in PerfGeniusJNI
 */
extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_init(JNIEnv *env, jobject thiz) {
    // Call InitLibSo method
    int initLibSoResult = InitLibSo();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "init : initLibSoResult = %d\n",
                        initLibSoResult);
    if (initLibSoResult != INIT_SO_SUCCESS) {
        return initLibSoResult;
    }

    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "init : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }

    // Call Init method
    int initResult = g_kit->Init(env);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "init : Call Init method. initResult = %d\n", initResult);
    return initResult;
}

/**
 * Called by a static method in PerfGeniusJNI
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_getApiVersion(JNIEnv *env, jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "getApiVersion : g_kit is nullptr, please init first");
        std::string str1 = std::to_string(KIT_ERROR);
        return env->NewStringUTF(str1.c_str());
    }
    // Call GetApiVersion method
    std::string outStr;
    int result = g_kit->GetApiVersion(outStr);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "getApiVersion : Call GetApiVersion method. "
                        "result = %d, outStr = %s\n", result, outStr.c_str());

    if (result != 0) {
        std::string str2 = std::to_string(result);
        return env->NewStringUTF(str2.c_str());
    } else {
        return env->NewStringUTF(outStr.c_str());
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_setFrameRate(JNIEnv *env, jclass clazz,
                                                                jint fps) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "setFrameRate : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }

    // Call SetFrameRate method
    int result = g_kit->SetFrameRate(fps);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "setFrameRate : Call SetFrameRate method. result = %d\n", result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_resetFrameRate(JNIEnv *env, jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "resetFrameRate : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }

    // Call ResetFrameRate method
    int result = g_kit->ResetFrameRate();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "resetFrameRate : Call ResetFrameRate method. result = %d\n", result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_setScene(JNIEnv *env, jclass clazz,
                                                            jstring scene) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "setScene : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }

    // jstring --To-- std::string
    jboolean isCopy = JNI_FALSE;
    std::string sceneDescription = env->GetStringUTFChars(scene, &isCopy);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "setScene : sceneDescription = %s\n",
                        sceneDescription.c_str());

    // Call SetScene method
    int result = g_kit->SetScene(sceneDescription);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "setScene : Call SetScene method. result = %d\n", result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_getCurrentFrameRate(JNIEnv *env, jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "getCurrentFrameRate : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }

    // Call GetCurrentFrameRate method
    int result = g_kit->GetCurrentFrameRate();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "getCurrentFrameRate : Call GetCurrentFrameRate method. result = %d\n",
                        result);
    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_getSupportedFrameRate(JNIEnv *env,
                                                                         jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "getSupportedFrameRate : g_kit is nullptr, please init first");
        std::string str1 = std::to_string(KIT_ERROR);
        return env->NewStringUTF(str1.c_str());
    }
    // Call GetSupportedFrameRate method
    std::vector<int> supportedFrameRate;
    int result = g_kit->GetSupportedFrameRate(supportedFrameRate);

    // std::vector<int> --To-- std::string
    std::string supportedFrameRateResult = "";
    int number;
    for (int i = 0; i < supportedFrameRate.size(); i++) {
        number = supportedFrameRate[i];
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "supportedFrameRate[%d] = %d\n", i,
                            number);
        supportedFrameRateResult = supportedFrameRateResult + std::to_string(number) + " ";
    }
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "getSupportedFrameRate : Call GetSupportedFrameRate method. "
                        "result = %d, supportedFrameRateResult = %s\n", result,
                        supportedFrameRateResult.c_str());

    if (result != 0) {
        std::string str2 = std::to_string(result);
        return env->NewStringUTF(str2.c_str());
    } else {
        return env->NewStringUTF(supportedFrameRateResult.c_str());
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_getPerformanceLevel(JNIEnv *env, jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "getPerformanceLevel : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    // Call GetPerformanceLevel method
    int result = g_kit->GetPerformanceLevel();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "getPerformanceLevel : Call GetPerformanceLevel method. result = %d\n",
                        result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_addKeyThreads(JNIEnv *env, jclass clazz,
                                                                 jstring tids) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "addKeyThreads : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    // jstring --To-- std::string
    jboolean isCopy = JNI_FALSE;
    std::string tidStr = env->GetStringUTFChars(tids, &isCopy);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "addKeyThreads : tidStr = %s\n",
                        tidStr.c_str());

    // std::string --To-- std::vector<int>
    std::vector<int> tidVec;
    if (!tidStr.empty()) {
        int prevPos = 0;
        int pos = tidStr.find(",");
        while (pos != std::string::npos) {
            __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                                "add tid : %s\n",
                                tidStr.substr(prevPos, pos - prevPos).c_str());
            int tid = stoi(tidStr.substr(prevPos, pos - prevPos));
            tidVec.push_back(tid);
            prevPos = pos + 1;
            pos = tidStr.find(",", prevPos);
        }
    }

    // Call AddKeyThreads method
    int result = g_kit->AddKeyThreads(tidVec);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "addKeyThreads : Call AddKeyThreads method. result = %d\n", result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_removeKeyThreads(JNIEnv *env, jclass clazz,
                                                                    jstring tids) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "removeKeyThreads : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    // jstring --To-- std::string
    jboolean isCopy = JNI_FALSE;
    std::string tidStr = env->GetStringUTFChars(tids, &isCopy);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "removeKeyThreads : tidStr = %s\n",
                        tidStr.c_str());

    // std::string --To-- std::vector<int>
    std::vector<int> tidVec;
    if (!tidStr.empty()) {
        int prevPos = 0;
        int pos = tidStr.find(",");
        while (pos != std::string::npos) {
            __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                                "remove tid : %s\n",
                                tidStr.substr(prevPos, pos - prevPos).c_str());
            int tid = stoi(tidStr.substr(prevPos, pos - prevPos));
            tidVec.push_back(tid);
            prevPos = pos + 1;
            pos = tidStr.find(",", prevPos);
        }
    }

    // Call RemoveKeyThreads method
    int result = g_kit->RemoveKeyThreads(tidVec);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "removeKeyThreads : Call RemoveKeyThreads method. result = %d\n",
                        result);
    return result;
}

/**
 * C++ calls back to JAVA through JNI
 */
JavaVM *g_VM;
jclass g_class;

void SystemEventCb(int cur)
{
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "SystemEventCb : cur = %d\n", cur);

    JNIEnv *env = nullptr;
    // Get the current native thread, whether it is attached to the JVM environment
    int getEnvStat = g_VM->GetEnv((void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        // If not, attach to the jvm environment and get env
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "SystemEventCb : getEnvStat = %d\n",
                            getEnvStat);
        if (g_VM->AttachCurrentThread(&env, nullptr) != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                                "SystemEventCb : Unable to get jvm");
            return;
        }
    }

    // Get the class to be called back through the global variable g_class
    jclass aClass = g_class;
    if (aClass == 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "SystemEventCb : Unable to find class");
        g_VM->DetachCurrentThread();
        return;
    }

    // Get the method ID to be called back
    jmethodID aMethod = env->GetStaticMethodID(aClass, "systemEventCallback", "(I)V");
    if (aMethod == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "SystemEventCb : Unable to find method");
        g_VM->DetachCurrentThread();
        return;
    }

    // Execute callback
    env->CallStaticVoidMethod(aClass, aMethod, cur);
    env = nullptr;
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "SystemEventCb : Success");
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_registerSystemEventCallback(JNIEnv *env,
                                                                               jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "registerSystemEventCallback : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "registerSystemEventCallback : Start");
    // Obtaining JavaVM is the representation of the virtual machine in JNI,
    // and it will be used in other threads to call back to the java layer.
    env->GetJavaVM(&g_VM);

    // Generate a global reference and keep it for callback
    g_class = (jclass) env->NewGlobalRef(clazz);

    // Call RegisterSystemEventCallback method
    int result = g_kit->RegisterSystemEventCallback(SystemEventCb);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "registerSystemEventCallback : Call RegisterSystemEventCallback method. result = %d\n",
                        result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_unRegisterSystemEventCallback(JNIEnv *env,
                                                                                 jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "unRegisterSystemEventCallback : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    // Call UnRegisterSystemEventCallback method
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "unRegisterSystemEventCallback : Start");
    int result = g_kit->UnRegisterSystemEventCallback();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "unRegisterSystemEventCallback : Call UnRegisterSystemEventCallback method. result = %d\n",
                        result);
    return result;
}

/**
 * C++ calls back to JAVA through JNI
 */
JavaVM *g_VMPintor;
jclass g_classPintor;

void g_performanceTracerCb(std::vector<unsigned int> &data)
{
    std::string dataStr = "";
    int number;
    for (int i = 0; i < data.size(); i++) {
        number = data[i];
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "data[%d] = %d\n", i, number);
        dataStr = dataStr + std::to_string(number) + " ";
    }
    JNIEnv *env = nullptr;
    // Get the current native thread, whether it is attached to the JVM environment
    int getEnvStat = g_VMPintor->GetEnv((void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        // If not, attach to the jvm environment and get env
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "g_performanceTracerCb : getEnvStat = %d\n", getEnvStat);
        if (g_VMPintor->AttachCurrentThread(&env, nullptr) != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                                "g_performanceTracerCb : Unable to get jvm");
            return;
        }
    }
    // Get the class to be called back through the global variable g_classPintor
    jclass aClass = g_classPintor;
    if (aClass == 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "g_performanceTracerCb : Unable to find class");
        g_VMPintor->DetachCurrentThread();
        return;
    }
    // Get the method ID to be called back
    jmethodID aMethod = env->GetStaticMethodID(aClass, "performanceTracerCallback", "(Ljava/lang/String;)V");
    if (aMethod == nullptr) {
        g_VMPintor->DetachCurrentThread();
        return;
    }
    // Execute callback
    jstring jdataStr = env->NewStringUTF(dataStr.c_str());
    env->CallStaticVoidMethod(aClass, aMethod, jdataStr);
    env = nullptr;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_registerPerformanceTracer(JNIEnv *env,
                                                                             jclass clazz,
                                                                             jint sample_rate) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "registerPerformanceTracer : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib", "registerPerformanceTracer : Start");
    // Obtaining JavaVM is the representation of the virtual machine in JNI,
    // and it will be used in other threads to call back to the java layer.
    env->GetJavaVM(&g_VMPintor);
    // Generate a global reference and keep it for callback
    g_classPintor = (jclass) env->NewGlobalRef(clazz);

    // Call RegisterPerformanceTracer method
    int result = g_kit->RegisterPerformanceTracer(sample_rate, g_performanceTracerCb);
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "registerPerformanceTracer : Call RegisterPerformanceTracer method. result = %d\n",
                        result);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_huawei_perfgenius_jni_PerfGeniusJNI_unRegisterPerformanceTracer(JNIEnv *env,
                                                                               jclass clazz) {
    if (g_kit == nullptr) {
        __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                            "unRegisterPerformanceTracer : g_kit is nullptr, please init first");
        return KIT_ERROR;
    }
    // Call UnRegisterPerformanceTracer method
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "unRegisterPerformanceTracer : Start");
    int result = g_kit->UnRegisterPerformanceTracer();
    __android_log_print(ANDROID_LOG_DEBUG, "PerfGeniusJNILib",
                        "unRegisterPerformanceTracer : Call UnRegisterPerformanceTracer method. result = %d\n",
                        result);
    return result;
}