package com.example.workflowmanager.service.chat;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.chat.FileRepository;
import com.example.workflowmanager.db.chat.MessageRepository;
import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.chat.File;
import com.example.workflowmanager.entity.chat.Message;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatDeleteService
{
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;

    public ChatDeleteService(final ChatRepository chatRepository,
        final MessageRepository messageRepository,
        final FileRepository fileRepository)
    {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.fileRepository = fileRepository;
    }

    public void delete(Collection<Long> chatIds)
    {
        final List<Chat> chatList = chatRepository.getList(chatIds);
        final Set<Message> messages = chatList.stream()
            .map(Chat::getMessages)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        final Set<File> files = messages.stream()
            .map(Message::getFiles)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        fileRepository.deleteAll(files);
        messageRepository.deleteAll(messages);
        chatRepository.deleteAll(chatList);
    }

}
