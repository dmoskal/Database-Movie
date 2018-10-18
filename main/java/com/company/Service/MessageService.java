package com.company.Service;

import com.company.DTO.MessageDTO;
import com.company.Entity.Message;
import com.company.Entity.User;

import java.util.List;

public interface MessageService {

    // Message CRUD Repository // SPRING DATA
    public void sendMessage(final User author, final MessageDTO messageDTO);
    public Message getMessage(final Long id);
    public void setDateOfRead(final Long id);
    public void deleteReceivedMessage(final Long id);
    public void deleteSentMessage(final Long id);
    public List<Message> findAllReceivedMessages(final User recipient);
    public List<Message> findAllSentMessages(final User sender);
}
