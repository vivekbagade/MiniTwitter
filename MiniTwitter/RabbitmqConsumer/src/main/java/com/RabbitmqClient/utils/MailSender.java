package com.RabbitmqClient.utils;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class MailSender implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);

    private Email email;

    private void init() throws Exception {
        email = new SimpleEmail();
        email.setHostName("smtp.directi.com");
        email.setSmtpPort(25);
        email.setFrom("cm.rts@directi.com");
        email.addTo("rohit.b@directi.com");
        email.addTo("cm-dbas@intranet.directi.com");
    }


    public MailSender(String subject, String body) {
        try {
            init();
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            email.setSubject(subject);
            email.setMsg(body);

        } catch (Exception ex) {
            LOG.error("Exception " + ex + " while initializing mail with subject "
                    + subject + " and body " + body);
        }

    }


    public void run() {
        try {
            email.send();
        } catch (Exception e) {
            LOG.error("{} - {}", e, email.getMimeMessage());
        }
    }


    /*public static void main(String[] args) {
        new MailSender(new IllegalArgumentException("Hi there!")).run();
    }*/
}
