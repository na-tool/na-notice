package com.na.notice.email.constant;

public interface NaEmailConst {
    String NTES_SMTP_HOST = "smtp.163.com";
    String IMG_START_WITH = "image/";
    String EMAIL_CONTENT = "email_content";
    String EMAIL_IMAGES = "email_images";

    String QQ_SMTP_HOST = "smtp.qq.com";

    String SEND_CODE ="<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>验证码邮件</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            background-color: #f6f8fa;\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 500px;\n" +
            "            background-color: #fff;\n" +
            "            padding: 30px;\n" +
            "            border-radius: 6px;\n" +
            "            box-shadow: 0 2px 8px rgba(0,0,0,0.1);\n" +
            "            margin: auto;\n" +
            "        }\n" +
            "        .code {\n" +
            "            font-size: 32px;\n" +
            "            font-weight: bold;\n" +
            "            color: #1a73e8;\n" +
            "            letter-spacing: 4px;\n" +
            "            margin: 20px 0;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            font-size: 12px;\n" +
            "            color: #888;\n" +
            "            margin-top: 40px;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <h2>您的验证码</h2>\n" +
            "        <p>您好，以下是您请求的验证码，请在 5 分钟内使用：</p>\n" +
            "        <div class=\"code\">{{code}}</div>\n" +
            "        <p>如非本人操作，请忽略此邮件。</p>\n" +
            "        <div class=\"footer\">\n" +
            "            本邮件由系统自动发送，请勿回复。\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>\n";

    static String buildVerificationHtml(String code) {
        return SEND_CODE.replace("{{code}}", code);
    }
}
