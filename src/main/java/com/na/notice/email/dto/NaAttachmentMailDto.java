package com.na.notice.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaAttachmentMailDto {
    /**
     * 硬盘路径
     * 例如  D:\\test.txt
     */
    private List<String> attachPath;

    /**
     * 网络地址
     * 例如  https://11/22/3/1.png
     */
    private List<String> attachUrl;
}
