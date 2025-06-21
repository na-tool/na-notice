package com.na.notice.email.dto;

import com.na.notice.email.constant.NaEmailConst;
import com.na.notice.email.utils.NaFileExtensionUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 图片邮件 DTO，用于封装邮件中的图片信息，包括图片内容、类型、CID 和对应的 HTML 标签。
 */
@Data
public class NaImageMailDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件的字节内容。
     */
    private byte[] imageData;

    /**
     * 文件类型（MIME 类型），例如 image/jpeg、image/bmp、image/gif。
     */
    private String mimeType;

    /**
     * 照片 CID 编号，随机生成。
     */
    private String imageCid;

    /**
     * 转成的 HTML 样式标签，例如：{@code <img src="cid:abcd">}，abcd 应替换成 imageCid。
     */
    private String imageHtmlTag;

    /**
     * 根据邮件内容中的占位符和对应 URL 关系，将 URL 的图片资源转换成对应的 ImageMailDto 对象集合，
     * 并替换邮件内容中的占位符为对应的 HTML img 标签。
     *
     * @param content               邮件内容，含有占位符需要被替换
     * @param contentUrlRelationMap 占位符与图片 URL 的映射关系
     * @return 包含替换后的邮件内容和图片 DTO 集合的 Map，键分别为 {@link NaEmailConst#EMAIL_CONTENT} 和 {@link NaEmailConst#EMAIL_IMAGES}
     */
    public Map<String, Object> getImageMailDtoByUrlToMap(String content,
                                                         Map<String, String> contentUrlRelationMap) {
        Map<String, Object> map = new HashMap<>();
        List<NaImageMailDto> imageMailDtos = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = contentUrlRelationMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();

            // 获取网络地址的文件字节内容和文件类型（MIME 类型）
            NaFileDto naFileDto = NaFileExtensionUtil.getFileDto(value);

            // 转成 HTML 样式的 ImageMailDto 对象
            NaImageMailDto imageMailDto = getImageMailDFromUrl(naFileDto);

            // 替换邮件内容中的占位符为对应的 HTML 标签
            content = content.replace(key, imageMailDto.getImageHtmlTag());

            imageMailDtos.add(imageMailDto);
        }
        map.put(NaEmailConst.EMAIL_CONTENT, content);
        map.put(NaEmailConst.EMAIL_IMAGES, imageMailDtos);
        return map;
    }

    /**
     * 根据文件信息构造 ImageMailDto 对象，生成随机 CID 和对应的 HTML 标签。
     *
     * @param naFileDto 文件数据传输对象，包含字节数组和 MIME 类型
     * @return 当前 ImageMailDto 对象（this）
     */
    private NaImageMailDto getImageMailDFromUrl(NaFileDto naFileDto) {
        imageCid = NaFileExtensionUtil.getRandomNo(10);
        imageData = naFileDto.getBytes();
        mimeType = naFileDto.getContentType();
        imageHtmlTag = "<img src=\"cid:" + imageCid + "\">";
        return this;
    }

}
