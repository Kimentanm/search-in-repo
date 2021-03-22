# Jetbrains Plugin: Search in Maven repository

![GitHub release (latest by date)](https://img.shields.io/github/v/release/kimentanm/search-in-mvn-repo?label=version&style=flat-square&logo=github&color=green)
![star](https://img.shields.io/github/stars/kimentanm/search-in-mvn-repo?style=flat-square&logo=github)

Jetbrains插件，支持Jetbrains全系产品。一款依赖查询工具，输入artifactId关键词，选择版本即可获得相应的依赖信息。

> 当前仅支持Maven和Gradle两种依赖

## 安装  
### 从Intellij Plugin仓库下载
在Plugin repository中搜索Search in Maven repository

### 源码编译
- 使用Gradle的`buildPlugin`脚本构建  
- 项目根路径找到build/distributions/searchin-mvn-repo-x.x.x.zip文件  
- IDE中选择`Install plugin from disk`，选择上述zip文件

## 使用
 - 插件安装好，在IDE的右侧菜单栏会出现一个`Search`菜单  
 - 先输入关键词搜索，比如输入`mybatis`，然后点击`search`按钮或者直接回车
 ![](https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210322214817.png)
 - 使用`Prev`和`Next`按钮切换分页
 - 双击某一列，搜索详细版本列表  
![](https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210322214918.png)
 - 双击某一列，弹出详情窗口，直接复制或者点击`Copy`按钮
 ![](https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210322214646.png)

## 预览
![](https://cdn.jsdelivr.net/gh/kimentanm/image-store/img/20210322215045.png)

## Change Notes
- v1.0.0   
发布的第一个版本，基础功能均已完成。  
输入ArtifactId关键字，选择版本即可查询到相关依赖信息  





