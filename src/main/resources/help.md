### 消息通知模块

```text
本版本发布时间为 2021-05-01  适配jdk版本为 1.8
```

#### 1 配置
##### 1.1 添加依赖
```
<dependency>
    <groupId>com.na</groupId>
    <artifactId>na-notice</artifactId>
    <version>1.0.0</version>
</dependency>
        
或者

<dependency>
    <groupId>com.na</groupId>
    <artifactId>na-notice</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../lib/na-notice-1.0.0.jar</systemPath>
</dependency>

相关依赖
<!--	邮箱依赖 	-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

##### 1.2 配置
```yaml
na:
  email:
    userName: xxxx@163.com
    passWord: xxxx
#    host: smtp.qq.com
#    port: 465
#    userName: xxxx@qq.com
#    passWord: xxx
```

##### 1.3 使用
```java
@Autowired
private ISendEmailExeService sendEmailExeService;
@Autowired
private AutoEmailConfig autoEmailConfig;


public void sendEmail() throws MalformedURLException, MessagingException, GeneralSecurityException, UnsupportedEncodingException {
    SendEmailParams params = new SendEmailParams();
    List<String> cc = new ArrayList<>();
    cc.add("shoujian@163.com");
    params.setToUser(cc);

    String content = "文本测试" +"imageHtmlTag" +"====" + "bbbb" + "b1b1"
            + "<img src='https://111/222/33/1.png'>";

    // 图片以URL形式,放在正文中, 注意上文的 bbbb,b1b1要和下面的key值一致
    Map<String,String> mapUrl = new HashMap<>();
    mapUrl.put("bbbb","https://111/222/33/1.png");
    mapUrl.put("b1b1","https://111/222/33/1.png");

    Map<String, Object> urlToMap = new ImageMailDto().getImageMailDtoByUrlToMap(content, mapUrl);

    content = urlToMap.get(EmailConst.EMAIL_CONTENT).toString();
    /**
     * 正文图片
     */
    List<ImageMailDto> imageMailDtoDates = (List<ImageMailDto>) urlToMap.get(EmailConst.EMAIL_IMAGES);

    params.setContent(content);
    params.setImageMail(imageMailDtoDates);

    /**
     * 附件
     */
    AttachmentMailDto attachmentMailDto = new AttachmentMailDto();
    List<String> path = new ArrayList<>();
    path.add("D:\\111\\22222.txt");
    attachmentMailDto.setAttachPath(path);
    List<String> url = new ArrayList<>();
    url.add("https://111/222/33/1.png");
//        url.add("https://111/222/33/1.png");
    attachmentMailDto.setAttachUrl(url);

    params.setAttContent(attachmentMailDto);

    Boolean send = sendEmailExeService.send(params, null);

}
```


# 【注意】启动类配置
```
如果你的包名不是以com.na开头的，需要配置
@ComponentScan(basePackages = {"com.na", "com.ziji.baoming"}) // 扫描多个包路径
```
