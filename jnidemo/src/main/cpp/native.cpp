//
// Created by 刘思谦 on 2020/9/23.
//

#include "native.h"
#include "JNIEnvAttachHelper.h"

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_getHelloStr(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello World from C++";
    hello_count++;

    // 1. 获取 thiz 的 class，也就是 java 中的 Class 信息
    jclass thisclazz = env->GetObjectClass(thiz);
    // 2. 根据 Class 获取 getClass 方法的 methodID，第三个参数是签名(params)return
    jmethodID mid_getClass = env->GetMethodID(thisclazz, "getClass", "()Ljava/lang/Class;");
    // 3. 执行 getClass 方法，获得 Class 对象
    jobject clazz_instance = env->CallObjectMethod(thiz, mid_getClass);
    // 4. 获取 Class 实例
    jclass clazz = env->GetObjectClass(clazz_instance);
    // 5. 根据 class  的 methodID
    jmethodID method_getName = env->GetMethodID(clazz, "getName", "()Ljava/lang/String;");
    // 6. 调用 getName 方法
    jstring name = static_cast<jstring>(env->CallObjectMethod(clazz_instance, method_getName));
    jboolean isCopy;
    LOGI("class name:%s", env->GetStringUTFChars(name, &isCopy));

    // 获取TextView field
    jfieldID field_tv_hello = env->GetFieldID(thisclazz, "tvHelloWorld",
                                              "Landroid/widget/TextView;");
    // 获取实例，这里tvHelloWorld在Java中是类私有变量，但依然可以获取到
    jobject obj_tv_hello = env->GetObjectField(thiz, field_tv_hello);
    // 调用方法
    jclass clazz_textview = env->GetObjectClass(obj_tv_hello);
    jmethodID method_measured_w = env->GetMethodID(clazz_textview, "getMeasuredWidth", "()I");
    jint measured_width = env->CallIntMethod(obj_tv_hello, method_measured_w);
    LOGI("TextView tvHelloWorld -- measuredWidth:%d", measured_width);
    // 设置TextView的值
    jmethodID method_set_text = env->GetMethodID(clazz_textview, "setText", ""
                                                                            "(Ljava/lang/CharSequence;)V");
    // format string
    char *const p_hello_chars = new char[100];
//    LOGI("size:%ld  hello_count:%d", sizeof(p_hello_chars), hello_count);
    snprintf(p_hello_chars, 100, "call getHelloStr %d time(s)", hello_count);
//    LOGI("p_hello_chars:%s", p_hello_chars);
    env->CallVoidMethod(obj_tv_hello, method_set_text, env->NewStringUTF(p_hello_chars));
    //delete
    delete[](p_hello_chars);

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

extern "C" JNIEXPORT void JNICALL
Java_cn_liusiqian_jnidemo_MainActivity_setDirectBuffer(JNIEnv *env, jobject context, jobject
jDirectBuffer, jint capacity) {
    int *buffer = (int *) env->GetDirectBufferAddress(jDirectBuffer);
    if (buffer == NULL) {
        LOGE("Get Direct Buffer Pointer failed!");
        return;
    }

    int current = 0;
    LOGI("capacity = %d", capacity);
    for (int i = 0; i < capacity / sizeof(int); i++) {
        current = buffer[i];
        LOGI("Get current value: 0x%x", current);
    }

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

void TriggerCrash(JNIEnv *env, jobject thiz, jboolean main_thread) {
    if (main_thread) {
        int *ptr = NULL;
        hello_count = ptr[2];
    } else {
        pthread_create(&my_pthread, NULL, threadRun, NULL);
    }
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("JNI_OnLoad called");
    p_javaVM = vm;

    //获取JNIEnv
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) { //从JavaVM获取JNIEnv
        return JNI_ERR;
    }

    if (env != NULL) {
        if (registerMethods(env, regClassId, regMethods, sizeof(regMethods) / sizeof
                (JNINativeMethod)) == JNI_OK) {
            LOGI("dynamic register success!");
        } else {
            LOGE("dynamic register failed!");
        }
    }

    if (env != NULL) {
        if (set_up_class_loader(env) == JNI_OK) {
            LOGI("set up class loader success");
        } else {
            LOGE("set up class loader fail");
        }
    }

    set_up_global_signal_handler();

    return JNI_VERSION_1_6;
}

void set_up_global_signal_handler() {
    struct sigaction handler;
    memset(&handler, 0, sizeof(struct sigaction));
    handler.sa_sigaction = my_singal_handler;
    handler.sa_flags = SA_RESETHAND;

    //register signal num
    #define CATCH_SIG_NUM(NUM) sigaction(NUM, &handler, &old_signalhandlers[NUM])

    CATCH_SIG_NUM(SIGQUIT);
    CATCH_SIG_NUM(SIGILL);
    CATCH_SIG_NUM(SIGABRT);
    CATCH_SIG_NUM(SIGBUS);
    CATCH_SIG_NUM(SIGFPE);
    CATCH_SIG_NUM(SIGSEGV);
    CATCH_SIG_NUM(SIGPIPE);
    CATCH_SIG_NUM(SIGSTKFLT);
    CATCH_SIG_NUM(SIGTERM);

    #undef CATCH_SIG_NUM
}

jint set_up_class_loader(JNIEnv * env) {
    jclass class_my_application = env->FindClass(applicationClassId);
    jmethodID method_get_class_loader = env->GetMethodID(class_my_application, "getClassLoader",
            "()Ljava/lang/ClassLoader;");

    //获取application实例
    jmethodID method_get_application = env->GetStaticMethodID(class_my_application, "getApp",
            "()Landroid/app/Application;");
    jobject application = env->CallStaticObjectMethod(class_my_application, method_get_application);

    //这里要使用Global Reference，否则函数返回之后，指针就失效了
    //JNIEnv::CallObjectMethod拿到的是Local Reference
    app_class_loader = env->NewGlobalRef(env->CallObjectMethod(application, method_get_class_loader));

    return app_class_loader == NULL ? JNI_ERR : JNI_OK;
}

void *threadRun(void *data) {

    LOGI("my_pthread run");

    int *ptr = NULL;
    hello_count = ptr[2];

    pthread_exit(&my_pthread);
}

void *threadReport(void *data) {

    int signum = *(int *) data;
    LOGI("my_report_pthread run  --  signum:%d", signum);

    if (p_javaVM) {
        JNIEnvAttachHelper helper(p_javaVM);
        jclass class_error_handler = find_class_complete(helper.getEnv(), errHandlerClsId);
        if (class_error_handler == NULL) {
            LOGE("Can not get error handler class");
        } else{
            jmethodID method_error_callback = helper.getEnv()->GetStaticMethodID(class_error_handler,
                                                                                 "onNativeError", "(I)V");
            if (method_error_callback == NULL) {
                LOGE("Can not get error handler method");
            } else{
                LOGI("Call Java callback method to notify native crash");
                helper.getEnv()->CallStaticVoidMethod(class_error_handler, method_error_callback, signum);
            }
        }

    } else {
        LOGE("JNI Unloaded");
    }

    pthread_exit(&my_report_pthread);
}

jclass find_class_complete(JNIEnv * env, const char * clsId) {
    if (env == NULL) {
        return NULL;
    }

    jclass class_loader = env->GetObjectClass(app_class_loader);

    jmethodID method_load_class = env->GetMethodID(class_loader, "loadClass",
            "(Ljava/lang/String;)Ljava/lang/Class;");
    jclass cls = static_cast<jclass> (env->CallObjectMethod(app_class_loader, method_load_class,
            env->NewStringUTF(clsId)));
    return cls;
}


void my_singal_handler(int signum, siginfo_t *info, void *reserved) {
    LOGI("signum: %d", signum);

    // 创建子线程上报crash
    // 在crash发生的线程上报，部分机型会有回调不到Java层的情况
    pthread_create(&my_report_pthread, NULL, threadReport, &signum);

    //调用原先的处理函数
    old_signalhandlers[signum].sa_handler(signum);
}