package com.na.notice.email.config;

import com.na.notice.email.constant.NaEmailConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "na.email")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaAutoEmailConfig {
    /**
     * SMTP服务器地址不能为空
     */
    @Builder.Default
    private String host = NaEmailConst.NTES_SMTP_HOST;

    /**
     * SMTP服务器端口不能为空
     */
    @Builder.Default
    private Integer port = 25;

    /**
     * SMTPS（SMTP Secure）协议，即通过 SSL 加密的 SMTP 协议来发送邮件
     */
    @Builder.Default
    private String protocol = "smtp";

    /**
     * 用户名不能为空
     */
    private String userName;

    /**
     * 密码不能为空
     */
    private String passWord;

    /**
     * 发件人昵称
     */
    @Builder.Default
    private String nickName = "消息通知中心";

    /**
     * ssl  smtp是否需要认证
     */
    @Builder.Default
    private String ssl = "true";
    @Builder.Default
    private String title = "测试消息";
    private String desc;

    /**
     * 分组发送
     */
    @Builder.Default
    private Integer receiveGroupSize = 10;

    /**
     * 是否单人发送
     * true 是
     * false  群发
     */
    @Builder.Default
    private Boolean singleSend = true;

    public Boolean checkParams() {
        boolean isHost = host != null && host.length() > 0;
        boolean isPort = port != null;
        boolean isProtocol = protocol != null && protocol.length() > 0;
        boolean isUserName = userName != null && userName.length() > 0;
        boolean isPassWord = passWord != null && passWord.length() > 0;
        boolean isNickName = nickName != null && nickName.length() > 0;
        boolean isSsl = ssl != null && ssl.length() > 0;
        boolean isTitle = title != null && title.length() > 0;
        boolean isReceiveGroupSize = receiveGroupSize != null && receiveGroupSize > 0;
        return isHost && isPort && isProtocol && isUserName && isPassWord && isNickName && isSsl && isTitle && isReceiveGroupSize;
    }
}
