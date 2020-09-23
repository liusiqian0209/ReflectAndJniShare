//
// Created by 刘思谦 on 2020/9/23.
//

#include "native.h"

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *env, jobject context) {
    std::string hello = "Hello World from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_countPrimeNative(JNIEnv *env, jobject context, jint target) {
    jint count = 0;
    for (int i = 2; i < target; ++i) {
        if (IsPrime(i)) {
            count++;
        }

    }
    return count;
}

bool IsPrime(jint num) {
    for (int i = 2; i < num; i++) {
        if (num % i == 0) {
            return false;
        }
        if (i * i > num) {
            return true;
        }
    }
    return true;
}
