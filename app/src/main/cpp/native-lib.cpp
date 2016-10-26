#include <jni.h>
#include <string>

extern "C"
jstring
Java_example_wangnan_com_nativepatch_MainActivity_firstStringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Bug code from C++";
    return env->NewStringUTF(hello.c_str());
}
