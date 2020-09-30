//
// Created by 刘思谦 on 2020/9/23.
//

#include <jni.h>
#include <string>
#include <android/log.h>

#ifndef REFLECTANDJNISHARE_NATIVE_H
#define REFLECTANDJNISHARE_NATIVE_H

#define LOG_TAG "JniDemoTAG_Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *, jobject);

extern "C" JNIEXPORT jint JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_countPrimeNative(JNIEnv *, jobject, jint);

extern "C" JNIEXPORT void JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_setDirectBuffer(JNIEnv *, jobject, jobject, jint);

bool IsPrime(jint);

int registerMethods(JNIEnv *, const char *, const JNINativeMethod *, int);

void DynamicRegistedNativeMethod(JNIEnv *, jobject, jstring);

static const char* const regClassName = "cn/liusiqian/jnidemo/MainActivity";
static const JNINativeMethod regMethods[] = {
        {"dynamicNative", "(Ljava/lang/String;)V", (void *) DynamicRegistedNativeMethod}
};

#endif //REFLECTANDJNISHARE_NATIVE_H
