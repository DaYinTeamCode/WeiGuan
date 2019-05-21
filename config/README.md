一、Versions Gradle配置：

包含Versions.gradle，提供了统一的版本管理配置。可以解决如下问题：

二、要解决的问题：

- 1、项目间、组件间 引用同一个 Versions.gradle，进而保持全局组件依赖统一。
- 2、保持 Versions.gradle 同步，不同项目或者组件间变更后，需要提交改动。
- 3、一处修改，所有工程中依赖都生效。

三、更新子模块代码

初次克隆后需要执行子模块初始化相关的命令：

```
git submodule init
git submodule update
```
