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
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatWsController
{
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

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
    public MessageResponseWS sendMessage(@DestinationVariable Long chatId,
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
        final List<FileWs> files = message.getFiles().stream()
            .map(file -> new FileWs(file.getId(), file.getName()))
            .collect(Collectors.toList());
        return new MessageResponseWS(user.getId(), user.getEmail(), now.format(DTF), messageWs.getMessage(), files);
    }

    public static class MessageResponseWS
    {
        private final Long userId;
        private final String senderName;
        private final String createTime;
        private final String message;
        private final List<FileWs> files;

        public MessageResponseWS(final Long userId, final String senderName,
            final String createTime, final String message,
            final List<FileWs> files)
        {
            this.userId = userId;
            this.senderName = senderName;
            this.createTime = createTime;
            this.message = message;
            this.files = files;
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

        public List<FileWs> getFiles()
        {
            return files;
        }

    }

    private static class FileWs
    {
        private final Long fileId;
        private final String fileName;

        private FileWs(final Long fileId, final String fileName)
        {
            this.fileId = fileId;
            this.fileName = fileName;
        }

        public Long getFileId()
        {
            return fileId;
        }

        public String getFileName()
        {
            return fileName;
        }

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
