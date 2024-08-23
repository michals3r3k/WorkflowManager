package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatWsController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final UserRepository userRepository;

    public ChatWsController(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat/{chatId}/send-message")
    @SendTo("/topic/chat/{chatId}/messages")
    @Transactional()
    public MessageResponseWS sendMessage(@DestinationVariable Long chatId,
        @Payload MessageRequestWs message)
    {
        final User user = userRepository.getReferenceById(message.getUserId());
        return new MessageResponseWS(user.getId(), user.getEmail(), LocalDateTime.now().format(DTF), message.getMessage());
    }

    public static class MessageResponseWS
    {
        private final Long userId;
        private final String senderName;
        private final String createTime;
        private final String message;

        public MessageResponseWS(final Long userId, final String senderName,
            final String createTime, final String message)
        {
            this.userId = userId;
            this.senderName = senderName;
            this.createTime = createTime;
            this.message = message;
        }

        public Long getUserId()
        {
            return userId;
        }

        public String getSenderName()
        {
            return senderName;
        }

        public String getCreateTime()
        {
            return createTime;
        }

        public String getMessage()
        {
            return message;
        }

    }

    public static class MessageRequestWs
    {
        private Long userId;
        private String message;

        public MessageRequestWs(final Long userId, final String message)
        {
            this.userId = userId;
            this.message = message;
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

    }

}
