//
// Created by 刘思谦 on 2020/9/23.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include <signal.h>
#include <pthread.h>

#ifndef REFLECTANDJNISHARE_NATIVE_H
#define REFLECTANDJNISHARE_NATIVE_H

#define LOG_TAG "JniDemoTAG_Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *, jobject);

extern "C" JNIEXPORT jint JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_countPrimeNative(JNIEnv *, jobject, jint);

extern "C" JNIEXPORT void JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_setDirectBuffer(JNIEnv *, jobject, jobject, jint);

bool IsPrime(jint);

int registerMethods(JNIEnv *, const char *, const JNINativeMethod *, int);

void DynamicRegistedNativeMethod(JNIEnv *, jobject, jstring);
void TriggerCrash(JNIEnv *, jobject, jboolean );
jint set_up_class_loader(JNIEnv * );
void *threadRun(void *);
jclass find_class_complete(JNIEnv *, const char *);

static const char* const regClassId = "cn/liusiqian/jnidemo/MainActivity";
static const char* const applicationClassId = "cn/liusiqian/jnidemo/MyApplication";
static const char* const errHandlerClsId = "cn/liusiqian/jnidemo/NativeErrorHandler";
static const JNINativeMethod regMethods[] = {
        {"dynamicNative", "(Ljava/lang/String;)V", (void *) DynamicRegistedNativeMethod},
        {"triggerNativeCrash", "(Z)V", (void *)TriggerCrash}
};

// params
int hello_count = 0;
JavaVM* p_javaVM;
pthread_t my_pthread, my_report_pthread;      //线程
static jobject app_class_loader;    // java class loader

// 保存之前的 signal handler
static struct sigaction old_signalhandlers[NSIG];
void set_up_global_signal_handler();
void my_singal_handler(int, siginfo_t *, void *);

#endif //REFLECTANDJNISHARE_NATIVE_H
