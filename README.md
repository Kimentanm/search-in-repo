# Jetbrains Plugin: Search in Repository

![GitHub release (latest by date)](https://img.shields.io/github/v/release/kimentanm/search-in-repo?label=version&style=flat-square&logo=github&color=green)
![star](https://img.shields.io/github/stars/kimentanm/search-in-repo?style=flat-square&logo=github)

Jetbrains插件，支持Jetbrains全系IDE。一款依赖查询工具，输入查询关键词，选择版本即可获得相应的依赖信息(支持Maven仓库和NPM仓库)。

> Maven依赖查询当前暂时仅支持Maven和Gradle两种依赖

> 本插件解决了，访问mvnrepository.com时会出现`One more step`验证的问题
> <img src="https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210324112547.png" width="50%" />

## 安装  
#### 从Intellij Plugin仓库下载
在Plugin repository中搜索Search in Repository  
[Plugin HomePage](https://plugins.jetbrains.com/plugin/16373-search-in-maven-repository)
![](https://gitee.com/Kimentanm/image-store/raw/master/img/20210326090134.png)

#### 源码编译
- 使用Gradle的`buildPlugin`脚本构建  
- 项目根路径找到build/distributions/search-in-repo-x.x.x.zip文件  
- IDE中选择`Install plugin from disk`，选择上述zip文件
- 下载zip文件 [search-in-repo-1.2.1.zip](https://upload.kimen.com.cn/#/s/y7Uz)

## 使用
 - 插件安装好，在IDE的右侧菜单栏会出现一个`Search`菜单  
 - 先输入关键词搜索，比如输入`mybatis`，然后点击`search`按钮或者直接回车
![](https://gitee.com/Kimentanm/image-store/raw/master/img/20210330140011.png)
 - 使用`Prev`和`Next`按钮切换分页
 - 双击某一列，搜索详细版本列表  
![](https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210322214918.png)
 - 双击某一列，弹出详情窗口，直接复制或者点击`Copy`按钮
![image-20210324164221706](https://gitee.com/Kimentanm/image-store/raw/master/img/20210324164221.png)

## 预览
![](https://gitee.com/Kimentanm/image-store/raw/master/img/20210330140520.png)
![](https://gitee.com/Kimentanm/image-store/raw/master/img/20210330140625.png)

## Change Notes
- v1.2.1  
  新增错误信息提示  
  fix bugs
- v1.2.0  
  新增NPM依赖查询
- v1.1.1  
  发布插件到Jetbrains插件仓库  
  增加插件英文介绍  
  解决插件适配性问题
- v1.1.0  
  新增排序条件 `relevance | popular | newest`  
  解决排序失效的bug  
  解决分页错误的bug
- v1.0.0   
  发布的第一个版本，基础功能均已完成。  
  输入ArtifactId关键字，选择版本即可查询到相关依赖信息  





