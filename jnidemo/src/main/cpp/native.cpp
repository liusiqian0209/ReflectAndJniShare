//
// Created by 刘思谦 on 2020/9/23.
//

#include "native.h"

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello World from C++";

    // 1. 获取 thiz 的 class，也就是 java 中的 Class 信息
    jclass thisclazz = env->GetObjectClass(thiz);
    // 2. 根据 Class 获取 getClass 方法的 methodID，第三个参数是签名(params)return
    jmethodID mid_getClass = env->GetMethodID(thisclazz, "getClass", "()Ljava/lang/Class;");
    // 3. 执行 getClass 方法，获得 Class 对象
    jobject clazz_instance = env->CallObjectMethod(thiz, mid_getClass);
    // 4. 获取 Class 实例
    jclass clazz = env->GetObjectClass(clazz_instance);
    // 5. 根据 class  的 methodID
    jmethodID mid_getName = env->GetMethodID(clazz, "getName", "()Ljava/lang/String;");
    // 6. 调用 getName 方法
    jstring name = static_cast<jstring>(env->CallObjectMethod(clazz_instance, mid_getName));
    jboolean isCopy;
    LOGI("class name:%s", env->GetStringUTFChars(name, &isCopy));

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

int registerMethods(JNIEnv *env, const char *className, const JNINativeMethod *methods, int
methodsLength) {
    // 1、获取Class
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_ERR;
    }
    // 2、注册方法
    if (env->RegisterNatives(clazz, methods, methodsLength) < 0) {
        return JNI_ERR;
    }
    return JNI_OK;
}

void DynamicRegistedNativeMethod(JNIEnv *env, jobject thiz, jstring value) {
    jboolean isCopy;
    LOGI("native DynamicRegistedNativeMethod -- %s", env->GetStringUTFChars(value, &isCopy));
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("JNI_OnLoad called");
    //获取JNIEnv
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) { //从JavaVM获取JNIEnv，一般使用1.4的版本
        return JNI_ERR;
    }

    if (env != NULL) {
        if (registerMethods(env, regClassName, regMethods, sizeof(regMethods) / sizeof
                (JNINativeMethod)) == JNI_OK) {
            LOGI("dynamic register success!");
        } else {
            LOGI("dynamic register failed!");
        }
    }

    return JNI_VERSION_1_6;
}
