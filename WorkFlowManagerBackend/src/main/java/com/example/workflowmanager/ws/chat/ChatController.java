package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.chat.FileRepository;
import com.example.workflowmanager.db.chat.MessageRepository;
import com.example.workflowmanager.entity.chat.Chat;
import com.example.workflowmanager.entity.chat.File;
import com.example.workflowmanager.entity.chat.Message;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ChatController
{
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;
    private final CurrentUserService cuService;

    public ChatController(final ChatRepository chatRepository,
        final MessageRepository messageRepository,
        final FileRepository fileRepository, final CurrentUserService cuService)
    {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.fileRepository = fileRepository;
        this.cuService = cuService;
    }

    @GetMapping("/api/chat/file/{fileId}/download")
    @Transactional
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        final File file = fileRepository.getReferenceById(fileId);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
            .body(file.getData());
    }

    @GetMapping("/api/chat/file/{fileId}/img")
    @Transactional
    public ResponseEntity<byte[]> getProfileImg(@PathVariable Long fileId) {
        final File file = fileRepository.getReferenceById(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentLength(file.getData().length);
        return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
    }

    @PostMapping("/api/chat/{chatId}/file/upload")
    public ResponseEntity<Long> upload(@PathVariable Long chatId,
        @RequestParam("file") MultipartFile multipartFile)
    {
        try
        {
            final User user = cuService.getCurrentUser()
                .orElseThrow(NoSuchElementException::new);
            final LocalDateTime now = LocalDateTime.now();
            final Message message = new Message();
            message.setCreateTime(now);
            message.setChat(chatRepository.getReferenceById(chatId));
            message.setCreator(user);
            messageRepository.save(message);
            final File file = new File();
            file.setMessage(message);
            file.setData(multipartFile.getBytes());
            file.setName(multipartFile.getOriginalFilename());
            file.setContentType(multipartFile.getContentType());
            fileRepository.save(file);
            return ResponseEntity.ok(message.getId());
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/api/chat/{chatId}/init-chat")
    @Transactional
    public ResponseEntity<List<MessageRest>> initChat(@PathVariable Long chatId)
    {
        final Chat chat = chatRepository.getReferenceById(chatId);
        final List<MessageRest> messages = chat.getMessages().stream()
            .sorted(Comparator.comparing(Message::getCreateTime))
            .map(MessageRest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    public static class MessageRest
    {
        private final Message message;
        private final List<FileRest> files;

        public MessageRest(final Message message)
        {
            this.message = message;
            this.files = message.getFiles().stream()
                .map(FileRest::new)
                .collect(Collectors.toList());
        }

        public Long getCreatorId()
        {
            return message.getCreator().getId();
        }

        public String getCreatorName()
        {
            return message.getCreator().getEmail();
        }

        public String getCreateTime()
        {
            return ChatWsController.formatDate(message.getCreateTime());
        }

        public String getContent()
        {
            return message.getContent();
        }

        public List<FileRest> getFiles()
        {
            return files;
        }

    }

    public static class FileRest
    {
        private final File file;

        private FileRest(File file)
        {
            this.file = file;
        }

        public Long getId()
        {
            return file.getId();
        }

        public String getName()
        {
            return file.getName();
        }

    }

}
