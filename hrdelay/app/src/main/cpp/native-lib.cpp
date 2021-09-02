#include <jni.h>
#include <iostream>
#include <sstream>
#include "include/hrdelay.h"
using namespace std;

#define MAX_DELAY_TIME 5
#define US_PER_SECOND 1000000

string HrdelaySample(float delay_s)
{
    ostringstream ss;
    struct timeval start = {0};
    struct timeval end = {0};
    if (delay_s >= MAX_DELAY_TIME) {
        ss << "Input a value less than 5!";
        return ss.str();
    }

    // use Hrdelay api and calculate the interval time
    gettimeofday(&start, NULL);
    Hrdelay(delay_s);
    gettimeofday(&end, NULL);

    long seconds = end.tv_sec - start.tv_sec;
    long micros = end.tv_usec - start.tv_usec;
    if (end.tv_usec < start.tv_usec) {
        seconds--;
        micros += US_PER_SECOND;
    }
    ss << "Delay Time = " << seconds << " s, " << micros << " us";
    return ss.str();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_perfgenius_1hrdelay_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject) {
    std::string hello = "AccKit Hrdelay Demo";
    HrdelayAutostatEnable(env);
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_perfgenius_1hrdelay_MainActivity_numFromJNI(
        JNIEnv *env,
        jobject,
        jfloat delay_s) {
    return env->NewStringUTF(HrdelaySample(delay_s).c_str());
}
