package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.chat.FileRepository;
import com.example.workflowmanager.db.chat.MessageRepository;
import com.example.workflowmanager.db.user.UserRepository;
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
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final FileRepository fileRepository;
    private final CurrentUserService cuService;

    public ChatController(final UserRepository userRepository, final ChatRepository chatRepository,
        final MessageRepository messageRepository,
        final FileRepository fileRepository,
        final CurrentUserService cuService)
    {
        this.userRepository = userRepository;
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
    public ResponseEntity<byte[]> getFileImg(@PathVariable Long fileId) {
        final File file = fileRepository.getReferenceById(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentLength(file.getData().length);
        return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
    }

    @GetMapping("/api/chat/user/{userId}/img")
    @Transactional
    public ResponseEntity<byte[]> getProfileImg(@PathVariable Long userId) {
        final User user = userRepository.getReferenceById(userId);
        if(user.getImgContent() == null)
        {
            return ResponseEntity.ok(null);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(user.getImgContent().length);
        return new ResponseEntity<>(user.getImgContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/api/chat/{chatId}/file/upload")
    @Transactional
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
    public ResponseEntity<List<MessageResponse>> initChat(@PathVariable Long chatId)
    {
        final Chat chat = chatRepository.getReferenceById(chatId);
        final List<MessageResponse> messages = chat.getMessages().stream()
            .sorted(Comparator.comparing(Message::getCreateTime))
            .map(MessageResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/api/chat/{chatId}/users")
    @Transactional
    public ResponseEntity<List<UserRest>> getUsers(@PathVariable Long chatId)
    {
        final List<UserRest> users = userRepository.getListByChatId(chatId)
            .stream()
            .map(UserRest::new)
            .sorted(Comparator.comparing(UserRest::getName,
                Comparator.naturalOrder()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    public static class UserRest
    {
        private final User user;

        private UserRest(final User user)
        {
            this.user = user;
        }

        public Long getId()
        {
            return user.getId();
        }

        public String getName()
        {
            return user.getEmail();
        }

    }

}
