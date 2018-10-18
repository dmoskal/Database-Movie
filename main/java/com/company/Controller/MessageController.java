package com.company.Controller;

import com.company.DTO.MessageDTO;
import com.company.Entity.Message;
import com.company.Entity.User;
import com.company.Service.AuthorizationService;
import com.company.Service.MessageService;
import com.company.Service.UserService;
import com.company.Validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private Validator validator;

    @GetMapping("/messages")
    public String showMessages(@RequestParam(value = "search", required = false) final String search,
                               final HttpServletRequest request,
                               final HttpSession session,
                               final Model model) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        ((User) session.getAttribute("user")).setReceivedMessages(messageService.findAllReceivedMessages(((User) session.getAttribute("user"))));

        if(search != null) {
            model.addAttribute("listMessages", validator.filterMessages(search, ((User)session.getAttribute("user")).getReceivedMessages()));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("listMessages", ((User) session.getAttribute("user")).getReceivedMessages());
        }

        return "messages";
    }

    @PostMapping("/messages")
    public String filterMessages(@RequestParam(value = "search", required = false) final String search) throws UnsupportedEncodingException {
        return "redirect:/messages?search=" +  URLEncoder.encode(search, "UTF-8");
    }

    @GetMapping("/messages/sendMessage")
    public String messagesSendMessage(@RequestParam(value = "recipient", required = false) final String username,
                                      final Model model,
                                      final HttpServletRequest request,
                                      final HttpSession session) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        model.addAttribute("messageDTO", new MessageDTO());
        model.addAttribute("recipient", username);

        return "messagesSendMessage";
    }

    @PostMapping("/messages/sendMessage")
    public String sendMessage(@ModelAttribute("messageDTO") @Valid MessageDTO messageDTO,
                              final BindingResult bindingResult,
                              final HttpSession session,
                              final Model model) {
        model.addAttribute("messageDTO", messageDTO);
        model.addAttribute("recipient", messageDTO.getRecipient());

        if(!userService.checkRepeatedUsername(messageDTO.getRecipient())) {
            model.addAttribute("invalidRecipient", true);
            return "messagesSendMessage";
        }

        if(bindingResult.hasErrors()) {
            return "messagesSendMessage";
        }

        messageService.sendMessage((User) session.getAttribute("user"), messageDTO);

        model.addAttribute("yourMessageHasBeenSent", true);
        model.addAttribute("recipient", "");
        model.addAttribute("messageDTO", new MessageDTO());
        return "messagesSendMessage";
    }

    @GetMapping("/messages/sentMessages")
    public String messagesSentMessages(@RequestParam(value = "search", required = false) final String search,
                                       final HttpServletRequest request,
                                       final HttpSession session,
                                       final Model model) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        ((User) session.getAttribute("user")).setSentMessages(messageService.findAllSentMessages(((User) session.getAttribute("user"))));

        if(search != null) {
            model.addAttribute("listMessages", validator.filterSentMessages(search, ((User)session.getAttribute("user")).getSentMessages()));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("listMessages", ((User) session.getAttribute("user")).getSentMessages());
        }

        return "messagesSentMessages";
    }

    @PostMapping("/messages/sentMessages")
    public String filterSentMessages(@RequestParam(value = "search", required = false) final String search) throws UnsupportedEncodingException {
        return "redirect:/messages/sentMessages?search=" +  URLEncoder.encode(search, "UTF-8");
    }

    @GetMapping("/message/{id}")
    public String message(@PathVariable(value = "id") final Long id,
                          final HttpServletRequest request,
                          final HttpSession session,
                          final Model model) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        ((User) session.getAttribute("user")).setReceivedMessages(messageService.findAllReceivedMessages(((User) session.getAttribute("user"))));

        List<Message> listMessages = new ArrayList<>();
        listMessages.addAll((((User) session.getAttribute("user")).getReceivedMessages()));

        if(!validator.isYourMessage(id, listMessages)) {
            return "redirect:/";
        }

        Message message = messageService.getMessage(id);

        if(message.getDateOfRead() == null) {
            messageService.setDateOfRead(message.getId());
        }

        model.addAttribute("message", message);

        return "message";
    }

    @GetMapping("/message/sentMessage/{id}")
    public String sentMessage(@PathVariable(value = "id") final Long id,
                              final HttpServletRequest request,
                              final HttpSession session,
                              final Model model) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        ((User) session.getAttribute("user")).setSentMessages(messageService.findAllSentMessages(((User) session.getAttribute("user"))));

        List<Message> listMessages = new ArrayList<>();
        listMessages.addAll(((User) session.getAttribute("user")).getSentMessages());

        if(!validator.isYourMessage(id, listMessages)) {
            return "redirect:/";
        }

        model.addAttribute("message", messageService.getMessage(id));

        return "sentMessage";
    }

    @GetMapping("/deleteMessage")
    public String deleteMessage(@RequestParam(value = "messageID") final Long id,
                                final HttpServletRequest request,
                                final HttpSession session,
                                final RedirectAttributes redirectAttributes) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        List<Message> listMessages = new ArrayList<>();
        listMessages.addAll((((User) session.getAttribute("user")).getReceivedMessages()));

        if(!validator.isYourMessage(id, listMessages)) {
            return "redirect:/";
        }

        messageService.deleteReceivedMessage(id);

        redirectAttributes.addFlashAttribute("yourMessageHasBeenRemoved", true);

        return "redirect:/messages";
    }

    @GetMapping("/deleteSentMessage")
    public String deleteSentMessage(@RequestParam(value = "messageID") final Long id,
                                    final HttpServletRequest request,
                                    final HttpSession session,
                                    final RedirectAttributes redirectAttributes) {
        authorizationService.setRequestSessionSecurity(request, session, SecurityContextHolder.getContext());

        if(!authorizationService.isLogged()) {
            return "redirect:/login";
        }

        List<Message> listMessages = new ArrayList<>();
        listMessages.addAll((((User) session.getAttribute("user")).getSentMessages()));

        if(!validator.isYourMessage(id, listMessages)) {
            return "redirect:/";
        }

        messageService.deleteSentMessage(id);

        redirectAttributes.addFlashAttribute("yourMessageHasBeenRemoved", true);

        return "redirect:/messages/sentMessages";
    }
}
