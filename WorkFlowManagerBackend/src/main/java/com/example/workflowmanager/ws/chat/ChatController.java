package com.example.workflowmanager.ws.chat;

import com.example.workflowmanager.db.chat.ChatRepository;
import com.example.workflowmanager.db.chat.FileRepository;
import com.example.workflowmanager.db.chat.MessageRepository;
import com.example.workflowmanager.entity.chat.File;
import com.example.workflowmanager.entity.chat.Message;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

}
