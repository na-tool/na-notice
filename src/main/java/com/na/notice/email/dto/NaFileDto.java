package com.na.notice.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaFileDto {

    /**
     * 获取文件字节内容
     */
    private byte[] bytes;

    /**
     * 获取文件类型（MIME 类型）
     */
    private String contentType;
}
