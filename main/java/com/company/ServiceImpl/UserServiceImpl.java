package com.company.ServiceImpl;

import com.company.DAO.UserCRUDRepository;
import com.company.DAO.UserDAO;
import com.company.DTO.ForgotPasswordDTO;
import com.company.DTO.RegisterDTO;
import com.company.Entity.Friendship;
import com.company.Entity.User;
import com.company.Service.EncodeService;
import com.company.Service.RandomNumberService;
import com.company.Service.MailService;
import com.company.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private UserCRUDRepository userCRUDRepository;
    private MailService mailService;
    private RandomNumberService randomNumberService;
    private EncodeService encodeService;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, UserCRUDRepository userCRUDRepository, MailService mailService,
                           RandomNumberService randomNumberService, EncodeService encodeService) {
        this.userDAO = userDAO;
        this.userCRUDRepository = userCRUDRepository;
        this.mailService = mailService;
        this.randomNumberService = randomNumberService;
        this.encodeService = encodeService;
    }

    @Override
    public void registerUser(final RegisterDTO registerDTO) {
        int randomActivationCode = randomNumberService.randomActivationCode();

        mailService.send(registerDTO.getEmail(), "Activation code", String.valueOf(randomActivationCode));

        userCRUDRepository.save(new User(registerDTO.getUsername(),
                                 registerDTO.getEmail(),
                                 encodeService.encode(registerDTO.getPassword()),
                                "ROLE_USER",
                                 (byte)1,
                                false,
                                 randomActivationCode));
    }

    @Override
    public User getUser(final Long id) {
        return userCRUDRepository.getOne(id);
    }

    @Override
    public List<User> getAllUsers(final int page) { return userCRUDRepository.findAll(new PageRequest(page, 9)).getContent(); }

    @Override
    public boolean checkRepeatedUsername(final String username) {
        return userDAO.checkRepeatedUsername(username);
    }

    @Override
    public boolean checkRepeatedEmail(final String email) {
        return userDAO.checkRepeatedEmail(email);
    }

    @Override
    public boolean checkActivationCode(final Long id, final int code) {
        return userDAO.checkActivationCode(id, code);
    }

    @Override
    public boolean checkChangeCode(final Long id, final int code) {
        return userDAO.checkChangeCode(id, code);
    }

    @Override
    public Long getIDByUsername(final String username) {
        return userDAO.getIDByUsername(username);
    }

    @Override
    public Long getIDByEmail(final String email) {
        return userDAO.getIDByEmail(email);
    }

    @Override
    public void setName(final Long id, final String name) {
        userCRUDRepository.setName(id, name);
    }

    @Override
    public void setSecondName(final Long id, final String secondName) { userCRUDRepository.setSecondName(id, secondName); }

    @Override
    public void setLastName(final Long id, final String lastName) {
        userCRUDRepository.setLastName(id, lastName);
    }

    @Override
    public void setSex(final Long id, final String sex) {
        userCRUDRepository.setSex(id, sex);
    }

    @Override
    public void setPassword(final Long id, final String password) {
        userCRUDRepository.setPassword(id, password);
    }

    @Override
    public void setEmail(final Long id, final String email) {
        userCRUDRepository.setEmail(id, email);
    }

    @Override
    public void setNewEmail(final Long id, final String newEmail) {
        userCRUDRepository.setNewEmail(id, newEmail);
    }

    @Override
    public void setCodeChange(final Long id, final int codeChange) {
        userCRUDRepository.setCodeChange(id, codeChange);
    }

    @Override
    public void setActivationCode(final Long id, final String email) {
        int randomActivationCode = randomNumberService.randomActivationCode();
        mailService.send(email,
                                "Activation code",
                                String.valueOf(randomActivationCode));

        userCRUDRepository.setActivationCode(id, randomActivationCode);
    }

    @Override
    public void setAvatar(final Long id, final String username, final MultipartFile avatar) {
        try {
            String nameAvatar = username + avatar.getOriginalFilename().substring(avatar.getOriginalFilename().indexOf("."));
            String filePath =  System.getProperty("user.home") + "/avatar/" + nameAvatar;
            File dest = new File(filePath);
            avatar.transferTo(dest);

            userCRUDRepository.setAvatar(id, "avatar/" + nameAvatar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUpdateDate(final Long id) {
        userCRUDRepository.setUpdateDate(id, new Date());
    }

    @Override
    public List<User> findUsersByUsernamePhrase(final String username, final int page) {
        return userCRUDRepository.findByUsernameIgnoreCaseContaining(username, new PageRequest(page, 9)).getContent();
    }

    @Override
    public List<User> findUsersByEmailPhrase(final String email, final int page) {
        return userCRUDRepository.findByEmailIgnoreCaseContaining(email, new PageRequest(page, 9)).getContent();
    }

    @Override
    public User findUserByUsername(final String username) {
        return userCRUDRepository.findOneByUsername(username);
    }

    @Override
    public void activationUser(final Long id) {
        userCRUDRepository.setActivationUser(id, true, 0);
    }

    @Override
    public void resetPassword(final ForgotPasswordDTO forgotPasswordDTO) {
        String randomNewPassword = randomNumberService.randomNewPassword();

        mailService.send(forgotPasswordDTO.getEmail(),
                                "New password",
                                randomNewPassword);

        userCRUDRepository.setPassword(getIDByEmail(forgotPasswordDTO.getEmail()),
                                       encodeService.encode(randomNewPassword));
    }

    @Override
    public Long countAllUser() {
        return userCRUDRepository.countAllUser();
    }

    @Override
    public Long countAllUserByUsernamePhrase(final String username) {
        return userCRUDRepository.countAllUserByUsernamePhrase(username);
    }

    @Override
    public Long countAllUserByEmailPhrase(final String email) {
        return userCRUDRepository.countAllUserByEmailPhrase(email);
    }

    @Override
    public List<User> getFriendsByFriendshipList(final List<Friendship> friendshipList) {
        List<User> userList = new ArrayList<>();

        for(Friendship friendship : friendshipList) {
            userList.add(userCRUDRepository.findOne(friendship.getFriendB()));
        }

        return userList;
    }
}
