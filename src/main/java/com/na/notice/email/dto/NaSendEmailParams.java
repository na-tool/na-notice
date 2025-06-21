package com.na.notice.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaSendEmailParams implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 发送的内容
     */
    private String content;

    /**
     * 附件内容
     */
    private NaAttachmentMailDto attContent;

    /**
     * 正文图片内容
     */
    private List<NaImageMailDto> imageMail;

    /**
     * 抄送人
     */
    private List<String> ccUser;

    /**
     * 接收人
     */
    private List<String> toUser;

    /**
     * 其它参数
     */
    private Map<String,Object> params;
}
