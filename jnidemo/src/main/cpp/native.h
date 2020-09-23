//
// Created by 刘思谦 on 2020/9/23.
//

#include <jni.h>
#include <string>

#ifndef REFLECTANDJNISHARE_NATIVE_H
#define REFLECTANDJNISHARE_NATIVE_H

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *, jobject );

extern "C" JNIEXPORT jint JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_countPrimeNative(JNIEnv *, jobject, jint );

bool IsPrime(jint);

std::string jstring2str(JNIEnv* env, jstring jstr);

#endif //REFLECTANDJNISHARE_NATIVE_H
