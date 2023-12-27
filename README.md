# 简介
MediaHost图床，一个存储图片与视频的图床程序。
- 前端：VUE(v2.6) + [Vuetify(v2)](https://v2.vuetifyjs.com/zh-Hans/)
- 后端：SpringBoot(v2.7.6)
- 数据库：MongoDB(v4.2.24)
### 兄弟项目
本项目的前端以及Golang版后端实现在这里：[https://github.com/chemicalfiber/MediaHost_separation](https://github.com/chemicalfiber/MediaHost_separation)

# 部署运行
首先克隆此存储库。
## 前端部署

参考[https://github.com/chemicalfiber/MediaHost_separation](https://github.com/chemicalfiber/MediaHost_separation) 的部署文档

## 后端部署
下载并安装JDK 11。

### 正确配置下列文件中的属性：
- `src/main/resources/application.properties`

```properties
# 应用服务 WEB 访问端口
server.port=8823
# 本应用程序的域名，用于上传文件后，返回文件可访问链接
media-host.self-domain=http://127.0.0.1
# 数据库相关配置
spring.data.mongodb.host=192.168.2.6
spring.data.mongodb.port=27017
spring.data.mongodb.database=media_host
# 文件大小限制
spring.servlet.multipart.max-file-size=1GB
spring.servlet.multipart.max-request-size=1GB
#配置GridFS文件存储桶名字
spring.data.mongodb.gridfs.bucket=fs
```

### 配置完成后，可以使用开发工具或maven对项目进行打包编译
