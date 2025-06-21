package com.na.notice.email.service;

import com.na.notice.email.config.NaAutoEmailConfig;
import com.na.notice.email.dto.NaSendEmailParams;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;

public interface INaSendEmailExeService {
    Boolean send(NaSendEmailParams naSendEmailParams,
                 NaAutoEmailConfig autoEmailConfig) throws MalformedURLException, MessagingException, GeneralSecurityException, UnsupportedEncodingException;
}
