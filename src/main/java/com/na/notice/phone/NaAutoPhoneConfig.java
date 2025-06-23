package com.na.notice.phone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "na.phone")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaAutoPhoneConfig {
    @Builder.Default
    private String type = PhoneConfigEnum.PHONE_ALIYUN.getKey();

    private String aliAccessKeyId;
    private String aliAccessKeySecret;
    private String aliSignName; // 短信签名
    private String aliTemplateCode; //短信模板


    // 验证type是否匹配枚举，并且验证字段不能为null或空
    public Boolean checked() {
        // 确保 type 在枚举中存在
        if (PhoneConfigEnum.getByKey(type) == null) {
            return false;
        }
        if(PhoneConfigEnum.PHONE_ALIYUN.getKey().equals(type)){
            // 确保 accessKeyId、accessKeySecret、signName 不为null或空字符串
            return isValidString(aliAccessKeyId) && isValidString(aliAccessKeySecret) && isValidString(aliSignName);
        }
        return false;
    }

    // 检查字符串不为 null 且不为空
    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // 定义枚举类型包含 key 和 value
    public enum PhoneConfigEnum {
        PHONE_ALIYUN("ali", "阿里云短信"),
        ;

        private final String key;
        private final String value;

        PhoneConfigEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        // 根据 key 获取对应的枚举
        public static PhoneConfigEnum getByKey(String key) {
            for (PhoneConfigEnum config : PhoneConfigEnum.values()) {
                if (config.getKey().equals(key)) {
                    return config;
                }
            }
            return null; // 如果未找到，返回 null 或抛出异常
        }
    }

}
