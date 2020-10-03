//
// Created by 刘思谦 on 2020/10/3.
//

#include <jni.h>
#include <android/log.h>

#ifndef REFLECTANDJNISHARE_JNIENVATTACHHELPER_H
#define REFLECTANDJNISHARE_JNIENVATTACHHELPER_H


class JNIEnvAttachHelper {
    private:

    bool needDetach;
    JNIEnv *env;
    JavaVM * p_jvm;

    public:
    JNIEnvAttachHelper(JavaVM*);

    ~JNIEnvAttachHelper();

    JNIEnv * getEnv();
};


#endif //REFLECTANDJNISHARE_JNIENVATTACHHELPER_H
