基于 [WeChatBc](https://github.com/meteorOSS/WeChatBc) 实现

## 功能

### 随机漂亮妹妹视频
发送 "plmm" 触发

bot将随机一个美少女跳舞的视频发送

![image](https://github.com/meteorOSS/WeChatSetu/assets/61687266/67236e40-ecab-40c9-b48f-844fb3dad471)

### pixiv图片获取

发送 "ldst" 触发

![image](https://github.com/meteorOSS/WeChatSetu/assets/61687266/b7357a39-a8a0-49e2-8fa0-e82998517157)

支持以 "ldst tag=标签1&tag=标签2" 的形式筛选图片，比如 "ldst tag=眼镜妹&tag=黑丝|白丝"

### 指令

/setu [参数] [值] 

excludeAI: **是否过滤ai作品** 选项(false,true) 默认为false

指令仅限控制台使用


## 使用方法
放入wechatbc的plugins文件夹，随后重启服务

插件不提供编译后的文件，请自行编译
