package com.company.Service;

public interface MailService {
    public boolean send(final String recipientEmail,
                        final String subject,
                        final String textMessage);
}
