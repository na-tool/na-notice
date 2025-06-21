package com.na.notice.email.utils;

import com.na.notice.email.dto.NaFileDto;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class NaFileExtensionUtil {

    public static NaFileDto getFileDto(String fileUrl){
        NaFileDto naFileDto = null;
        try{
            if(StringUtils.isNotEmpty(fileUrl )){
                naFileDto = new NaFileDto();

                /**
                 * 创建 URL 对象
                 */
                URL url = new URL(fileUrl);

                /**
                 * 打开连接
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                /**
                 * 获取文件类型（MIME 类型）
                 */
                String mimeType = connection.getContentType();

                /**
                 * 获取文件字节内容
                 */
                InputStream inputStream = connection.getInputStream();
                byte[] fileBytes = readInputStream(inputStream);

                /**
                 * 关闭连接
                 */
                connection.disconnect();

                // 处理文件字节内容和文件类型
                if (fileBytes != null && mimeType != null) {
                    /**
                     * 执行您的操作，例如保存文件到磁盘或处理文件内容
                     */
                    naFileDto.setContentType(mimeType);
                    naFileDto.setBytes(fileBytes);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return naFileDto;
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }

    public static String getRandomNo(Integer length) { // 生成随机字符串
        char[] chr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(chr[random.nextInt(36)]);
        }
        return buffer.toString();
    }
}
