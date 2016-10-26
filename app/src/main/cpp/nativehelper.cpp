//
// Created by 王楠 on 2016/10/15.
//
#ifndef __cplusplus
#define __cplusplus
#endif


#include <jni.h>
#include <stdio.h>
#include <dlfcn.h>

static char *lib_path = "/data/app/example.wangnan.com.nativepatch-1/lib/arm64/libnative-lib.so";


/**
 * 根据给出的so路径，卸载其在内存的加载
 */
extern "C"
JNIEXPORT jboolean
JNICALL
Java_example_wangnan_com_nativepatch_MainActivity_closeLibrary(
        JNIEnv *env,
        jobject /* this */) {
    void *libHandle = dlopen(lib_path, RTLD_NOLOAD);
    if (libHandle != NULL) {
        dlclose(libHandle);
        dlclose(libHandle);
        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }

}