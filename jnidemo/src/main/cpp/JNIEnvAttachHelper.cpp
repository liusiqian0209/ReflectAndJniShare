//
// Created by 刘思谦 on 2020/10/3.
//

#include "JNIEnvAttachHelper.h"

JNIEnv *JNIEnvAttachHelper::getEnv() {
    return this->env;
}

JNIEnvAttachHelper::JNIEnvAttachHelper(JavaVM *p_javaVm) {
    __android_log_print(ANDROID_LOG_WARN, "JniDemoTAG_Helper", "constructor");

    this->p_jvm = p_javaVm;
    needDetach = false;
    // 当前线程没有attach到javaVM时，需要手动绑定
    if (p_javaVm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_EDETACHED) {
        if (p_javaVm->AttachCurrentThread(&env, NULL) == JNI_OK) {
            needDetach = true;
            __android_log_print(ANDROID_LOG_WARN, "JniDemoTAG_Helper",
                                "attach JNIEnv to current thread");
        }
    }
}

JNIEnvAttachHelper::~JNIEnvAttachHelper() {
    if (needDetach) {
        p_jvm->DetachCurrentThread();
    }
}
