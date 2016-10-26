# NativeHotPatch
读《动态链接库加载原理及HotFix方案介绍》(http://dev.qq.com/topic/57bec216d81f2415515d3e9c )写的小demo，目前尚未实现动态加载，清除数据后提前加载是可以成功的


So动态升级方案Demo

使用方法：

- 直接运行程序显示"Bug Code"此处为调用的原链接库的方法显示的结果。

- 运行adb push ./patch.jar /sdcard/命令，将补丁包推送到sdcard目录里，然后点击click按钮，将补丁包路径注入到nativeLibraryDirectory的首部，并加载补丁包的链接库。若补丁包起作用，应显示"Patch Code"，否则为原来的"Bug Code"
