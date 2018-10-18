package com.company.ServiceImpl;

import com.company.DAO.MessageCRUDRepository;
import com.company.DTO.MessageDTO;
import com.company.Entity.Message;
import com.company.Entity.User;
import com.company.Service.MessageService;
import com.company.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private MessageCRUDRepository messageCRUDRepository;
    private UserService userService;

    @Autowired
    public MessageServiceImpl(MessageCRUDRepository messageCRUDRepository, UserService userService) {
        this.messageCRUDRepository = messageCRUDRepository;
        this.userService = userService;
    }

    @Override
    public void sendMessage(final User author, final MessageDTO messageDTO) {
        Message message = new Message(author,
                                      userService.findUserByUsername(messageDTO.getRecipient()),
                                      messageDTO.getSubject(),
                                      messageDTO.getText());
        messageCRUDRepository.save(message);
    }

    @Override
    public Message getMessage(final Long id) {
        return messageCRUDRepository.getOne(id);
    }

    @Override
    public void setDateOfRead(Long id) {
        messageCRUDRepository.setDateOfRead(id, new Date());
    }

    @Override
    public void deleteReceivedMessage(final Long id) {
        messageCRUDRepository.deleteReceivedMessage(id, false);
    }

    @Override
    public void deleteSentMessage(final Long id) {
        messageCRUDRepository.deleteSentMessage(id, false);
    }

    @Override
    public List<Message> findAllReceivedMessages(final User recipient) {
        return messageCRUDRepository.findAllReceivedMessages(true, recipient);
    }

    @Override
    public List<Message> findAllSentMessages(final User sender) {
        return messageCRUDRepository.findAllSentMessages(true, sender);
    }
}
