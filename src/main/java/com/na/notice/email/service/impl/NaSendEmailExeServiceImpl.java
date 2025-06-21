package com.na.notice.email.service.impl;

import com.na.notice.email.config.NaAutoEmailConfig;
import com.na.notice.email.dto.NaImageMailDto;
import com.na.notice.email.dto.NaSendEmailParams;
import com.na.notice.email.service.INaSendEmailExeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class NaSendEmailExeServiceImpl implements INaSendEmailExeService {
    @Autowired
    private NaAutoEmailConfig autoEmailConfig;

    /**
     * 发送参数
     */
    private NaSendEmailParams naSendEmailParams;

    private Properties properties;
    private Message message;
    private Session session;
    private Transport transport;

    /**
     * 报错后 接收人
     */
    private Address[] receiveAddressEmail;

    /**
     * 报错后 抄送人
     */
    private Address[] copyAddressEmail;

    private void init(NaAutoEmailConfig autoEmailConfig) {
        if(autoEmailConfig != null){
            this.autoEmailConfig = autoEmailConfig;
        }
    }

    @Override
    public Boolean send(NaSendEmailParams naSendEmailParams,
                        NaAutoEmailConfig autoEmailConfig) throws MalformedURLException, MessagingException, GeneralSecurityException, UnsupportedEncodingException {
        init(autoEmailConfig);
        if(!this.autoEmailConfig.checkParams()){
            return false;
        }

        this.naSendEmailParams = naSendEmailParams;
        if(this.autoEmailConfig.getSingleSend()){
            /**
             * 单发
             */
            for(String toUser: naSendEmailParams.getToUser()){
                List<String> to = new ArrayList<>();
                to.add(toUser);
                NaSendEmailParams clone = NaSendEmailParams.builder()
                        .content(naSendEmailParams.getContent())
                        .attContent(naSendEmailParams.getAttContent())
                        .imageMail(naSendEmailParams.getImageMail())
                        .ccUser(naSendEmailParams.getCcUser())
                        .toUser(to)
                        .params(naSendEmailParams.getParams())
                        .build();
                sendEmail(clone);
            }
        }
        else {
            /**
             * 群发 如果接收人太多可能邮件会发不出去
             */

            // 每次发送人数
            int groupSize = this.autoEmailConfig.getReceiveGroupSize();
            int length = naSendEmailParams.getToUser().size();
            if(length < groupSize){
                sendEmail(naSendEmailParams);
            }else {
                /**
                 * 计算可以分成多少组
                 */
                int num = ( length + groupSize - 1 )/groupSize ;
                List<List<String>> currentReceiveUserEmail = new ArrayList<>(num);
                for (int i = 0; i < num; i++) {
                    // 开始位置
                    int fromIndex = i * groupSize;
                    // 结束位置
                    int toIndex = (i+1) * groupSize < length ? ( i+1 ) * groupSize : length ;
                    currentReceiveUserEmail.add(naSendEmailParams.getToUser().subList(fromIndex,toIndex)) ;
                }
                //遍历发送邮件
                for (int i = 0; i < currentReceiveUserEmail.size(); i++) {
                    NaSendEmailParams clone = SerializationUtils.clone(naSendEmailParams);
                    clone.setToUser(currentReceiveUserEmail.get(i));
                    sendEmail(clone);
                }
            }
        }

        return true;
    }

    /**
     * 向用户发送邮件
     * @param naSendEmailParams
     */
    private void sendEmail(NaSendEmailParams naSendEmailParams) throws MalformedURLException, MessagingException, UnsupportedEncodingException, GeneralSecurityException {
        sendEmailNTES();
    }

    /**
     * 通过网易邮箱发送
     */
    private void sendEmailNTES() throws MalformedURLException, MessagingException, UnsupportedEncodingException, GeneralSecurityException {
        initNTESMessage();

        /**
         * 设置发送方式与接收者
         */
        /**
         * 抄送
         */
        if(this.naSendEmailParams.getCcUser() != null &&
                !this.naSendEmailParams.getCcUser().isEmpty()){
            message.setRecipients(Message.RecipientType.CC, addressesListConvertToArray(this.naSendEmailParams.getCcUser()));
        }
        if(this.naSendEmailParams.getToUser() != null &&
                !this.naSendEmailParams.getToUser().isEmpty()){
            // message.setRecipient(Message.RecipientType.CC, new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, addressesListConvertToArray(this.naSendEmailParams.getToUser()));
        }

        /**
         * 创建 Transport用于将邮件发送
         */
        initNTESTransport();
        try {
            //Transport.send(message)
            transport.sendMessage(message, message.getAllRecipients());
        }catch (SendFailedException e) {
            /**
             * 得到有效但未能成功将消息发送到的地址
             */
            exceptionAddress(e.getValidUnsentAddresses());
        }
    }

    private Message initNTESMessage() throws UnsupportedEncodingException, MessagingException, MalformedURLException {
        initNTESSession();

        System.setProperty("mail.mime.charset", "UTF-8");

        /**
         * 创建一个Message，它相当于是邮件内容
         */
        message = new MimeMessage(session);
        /**
         * 设置发送者
         */
        message.setFrom(new InternetAddress(this.autoEmailConfig.getUserName(),
                this.autoEmailConfig.getNickName(),
                "UTF-8"));
        // 设置邮件主题
        message.setSubject(this.autoEmailConfig.getTitle());

        // 创建邮件正文
        MimeMultipart contentMultipart = createContentMultipart(this.naSendEmailParams.getContent(),
                this.naSendEmailParams.getImageMail());

        /**
         * 添加附件
         */
        if (this.naSendEmailParams.getAttContent() != null){
            if(CollectionUtils.isNotEmpty(this.naSendEmailParams.getAttContent().getAttachPath())){
                for(String attPath: this.naSendEmailParams.getAttContent().getAttachPath()){
                    MimeBodyPart attach = new MimeBodyPart();
                    DataHandler dataHandler = new DataHandler(new FileDataSource(attPath));
                    attach.setDataHandler(dataHandler);
                    attach.setFileName(MimeUtility.encodeText(dataHandler.getName()));
                    contentMultipart.addBodyPart(attach);
                }
            }
            if(CollectionUtils.isNotEmpty(this.naSendEmailParams.getAttContent().getAttachUrl())){
                for(String attUrl: this.naSendEmailParams.getAttContent().getAttachUrl()){
                    // 创建 URL 对象
                    URL url = new URL(attUrl);
                    // 创建附件
                    MimeBodyPart attach = new MimeBodyPart();
                    // 使用 URLDataSource 获取数据处理程序
                    DataHandler dataHandler = new DataHandler(new URLDataSource(url));
                    // 设置数据处理程序和文件名
                    attach.setDataHandler(dataHandler);
                    attach.setFileName(MimeUtility.encodeText(dataHandler.getName()));
                    // 将附件添加到邮件的内容部分
                    contentMultipart.addBodyPart(attach);
                }
            }
        }
        if (this.naSendEmailParams.getAttContent() != null
                || (this.naSendEmailParams.getImageMail() != null && !this.naSendEmailParams.getImageMail().isEmpty())) {
            /**
             * 关联关系
             */
            contentMultipart.setSubType("mixed");
            message.setContent(contentMultipart);
        } else {
            /**
             * 如果没有附件和图片，直接设置文本正文
             */
            message.setContent(this.naSendEmailParams.getContent(), "text/html;charset=UTF-8");
        }

        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session initNTESSession(){
        initNTESProperties();

        String username = this.autoEmailConfig.getUserName();
        String password = this.autoEmailConfig.getPassWord();

        /**
         * 创建验证器
         */
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                /**
                 * 设置发送人的账号和密码
                 */
                return new PasswordAuthentication(username, password);
            }
        };

        session = Session.getInstance(properties, auth);
        return session;
    }

    /**
     * 网易163配置
     */
    private Properties initNTESProperties(){
        properties = initProperties();
        /**
         * 设置发送的协议
         */
        properties.setProperty("mail.transport.protocol", autoEmailConfig.getProtocol());

        /**
         * 指定验证为true
         */
        properties.put("mail.smtp.auth", this.autoEmailConfig.getSsl());

        /**
         * 端口
         */
        properties.put("mail.smtp.port", this.autoEmailConfig.getPort());

//        properties.put("mail.smtp.port", 465);
//        //开启ssl
//        properties.put("mail.smtp.ssl.enable", "true");

        /**
         * 设置发送邮件的服务器
         */
        properties.put("mail.smtp.host", this.autoEmailConfig.getHost());

        if(this.autoEmailConfig.getPort() == 465){
            // 关键：启用 SSL
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.port", "465");
        }

        /**
         * 发件人的账号
         */
        properties.put("mail.user", this.autoEmailConfig.getUserName());

        /**
         * 访问SMTP服务时需要提供的密码
         */
        properties.put("mail.password", this.autoEmailConfig.getPassWord());
        return properties;
    }

    /**
     * 初始化配置数据
     * @return
     */
    private Properties initProperties(){
        return new Properties();
    }

    /**
     * 创建包含文本和图片的邮件内容
     *
     * @param content 邮件正文内容
     * @param imageMailDtoDates 图片数据列表
     * @return 包含邮件内容的 MimeMultipart 对象
     * @throws MessagingException 如果创建 MimeMultipart 过程中发生错误
     */
    private MimeMultipart createContentMultipart(String content, List<NaImageMailDto> imageMailDtoDates) throws MessagingException {
        /**
         * 创建一个多部分内容容器
         */
        MimeMultipart contentMultipart = new MimeMultipart();

        /**
         * 检查图片数据列表是否为空
         */
        if (imageMailDtoDates != null && !imageMailDtoDates.isEmpty()) {
            /**
             * 创建一个包含图片和文本内容的多部分内容容器
             */
            MimeMultipart imageAndContent = new MimeMultipart("related");

            /**
             * 遍历图片数据列表并添加到容器中
             */
            for (NaImageMailDto dto : imageMailDtoDates) {
                if (dto.getImageData() != null && StringUtils.isNotBlank(dto.getMimeType()) && StringUtils.isNotBlank(dto.getImageCid())) {
                    MimeBodyPart image = new MimeBodyPart();
                    image.setDataHandler(new DataHandler(new ByteArrayDataSource(dto.getImageData(), dto.getMimeType())));
                    image.setContentID(dto.getImageCid());
                    imageAndContent.addBodyPart(image);
                }
            }

            /**
             * 创建并添加文本内容到容器中
             */
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");
            imageAndContent.addBodyPart(text);

            /**
             * 将包含图片和文本内容的容器添加到主容器中
             */
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(imageAndContent);
            contentMultipart.addBodyPart(bodyPart);
        } else {
            /**
             * 如果没有图片，直接创建并添加文本内容到主容器中
             */
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, "text/html;charset=UTF-8");
            contentMultipart.addBodyPart(text);
        }

        return contentMultipart;
    }

    /**
     * 设置抄送人地址
     * @param copyUserEmail
     * @return
     * @throws AddressException
     */
    private Address[] addressesListConvertToArray(List<String> copyUserEmail) throws AddressException {
        Address[] addresses = new InternetAddress[copyUserEmail.size()];
        for (int i = 0; i < copyUserEmail.size(); i++) {
            addresses[i] = new InternetAddress(copyUserEmail.get(i));
        }
        return addresses;
    }

    private Transport initNTESTransport() throws MessagingException {
        /**
         * 获取发送器对象:提供指定的协议
         * Transport transport = session.getTransport("smtp");
         * Transport transport = session.getTransport();
         */
        transport = session.getTransport();
        /**
         * 设置发件人的信息
         */
        transport.connect(this.autoEmailConfig.getHost(),
                this.autoEmailConfig.getUserName(),
                this.autoEmailConfig.getPassWord());
        return transport;
    }

    /**
     * 接受者邮箱不正确，过滤后得到正确的邮箱
     * @param addresses
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    private void exceptionAddress(Address[] addresses) throws MessagingException, GeneralSecurityException, UnsupportedEncodingException, MalformedURLException {
        if (addresses.length > 0) {
            Address[] address = new InternetAddress[addresses.length];
            for (int i = 0; i < addresses.length; i++) {
                address[i] = new InternetAddress(addresses[i].toString());
            }
            receiveAddressEmail = address;
            sendMailWhenException();
        }
    }

    /**
     * 给正确的邮箱发送邮件
     * @throws GeneralSecurityException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    private void sendMailWhenException() throws GeneralSecurityException, MessagingException, UnsupportedEncodingException, MalformedURLException {
        sendMailNTESWhenException();
    }

    /**
     * 正确的网易邮箱发数据
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    private void sendMailNTESWhenException() throws MessagingException, GeneralSecurityException, UnsupportedEncodingException, MalformedURLException {
        Message message = initNTESMessage();

        /**
         * 设置发送方式与接收者
         *
         * 抄送
         */
//        if(this.sendEmailParams.getCcUser() != null &&
//                !this.sendEmailParams.getCcUser().isEmpty()){
//            message.setRecipients(Message.RecipientType.CC, addressesListConvertToArray(this.sendEmailParams.getCcUser()));
//        }
//        if (copyAddressEmail != null && copyAddressEmail.length > 0) {
//            message.setRecipients(Message.RecipientType.CC, copyAddressEmail);
//        }

        if (receiveAddressEmail != null && receiveAddressEmail.length > 0) {
            message.setRecipients(Message.RecipientType.TO, receiveAddressEmail);
        }

        /**
         * 创建 Transport用于将邮件发送
         * Transport transport = initNTESTransport();
         */
        initNTESTransport();
        try {
            // Transport.send(message)
            transport.sendMessage(message, message.getAllRecipients());
        } catch (SendFailedException e) {
            /**
             * 得到有效但未能成功将消息发送到的地址
             */
            exceptionAddress(e.getValidUnsentAddresses());
        }
    }
}
