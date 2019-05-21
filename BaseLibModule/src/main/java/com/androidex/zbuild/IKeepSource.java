package com.androidex.zbuild;

/**
 * 实现该接口的类不被混淆
 * 需要自己在混淆配置文件中配置
 * -keep class * implements com.androidex.zbuild.IKeepSource {*;}
 * -keep interface com.androidex.zbuild.IKeepSource {*;}
 * Created by yihaibin on 16/11/17.
 */
public interface IKeepSource {
}
