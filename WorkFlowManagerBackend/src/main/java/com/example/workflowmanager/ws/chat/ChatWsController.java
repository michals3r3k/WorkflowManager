package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.chat.MessageRepository;
import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.chat.Message;
import com.example.workflowmanager.entity.user.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Controller
public class ChatWsController
{

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public ChatWsController(final UserRepository userRepository,
        final ChatRepository chatRepository, final MessageRepository messageRepository)
    {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat/{chatId}/send-message")
    @SendTo("/topic/chat/{chatId}/messages")
    @Transactional()
    public MessageResponse sendMessage(@DestinationVariable Long chatId,
        @Payload MessageRequestWs messageWs)
    {
        final User user = userRepository.getReferenceById(messageWs.getUserId());
        final Message message = Optional.ofNullable(messageWs.getMessageId())
            .map(messageRepository::getReferenceById)
            .orElseGet(Message::new);
        final LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setContent(messageWs.getMessage());
        message.setCreator(user);
        message.setChat(chatRepository.getReferenceById(chatId));
        if(message.getFiles() == null)
        {
            message.setFiles(new HashSet<>());
        }
        messageRepository.save(message);
        return new MessageResponse(message);
    }

    public static class MessageRequestWs
    {
        private Long userId;
        private Long messageId;
        private String message;

        public MessageRequestWs()
        {
            // for Spring
        }

        public Long getUserId()
        {
            return userId;
        }

        public void setUserId(final Long userId)
        {
            this.userId = userId;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(final String message)
        {
            this.message = message;
        }

        public Long getMessageId()
        {
            return messageId;
        }

        public void setMessageId(final Long messageId)
        {
            this.messageId = messageId;
        }

    }

}
