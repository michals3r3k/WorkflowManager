package com.example.workflowmanager.rest.user;

import com.example.workflowmanager.db.user.UserRepository;
import com.example.workflowmanager.entity.user.User;
import com.example.workflowmanager.service.auth.CurrentUserService;
import com.example.workflowmanager.service.utils.ServiceResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@CrossOrigin
@RestController
public class ProfileController
{
    private static final String UPLOADED_FILE = "uploadedfile";

    private UserRepository userRepository;
    private CurrentUserService currentUserService;

    public ProfileController(UserRepository userRepository,
        CurrentUserService currentUserService)
    {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/profile/img/upload")
    public ResponseEntity<byte[]> uploadImg(@RequestParam("file") MultipartFile file) {
        try
        {
            final byte[] bytes = file.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/api/profile/img/save")
    public ResponseEntity<ServiceResult<?>> saveImg(@RequestParam("file") MultipartFile file)
    {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        try
        {
            user.setImgContent(file.getBytes());
            userRepository.save(user);
            return ResponseEntity.ok(ServiceResult.ok());
        }
        catch (Exception e)
        {
            return ResponseEntity.ok(ServiceResult.ok());
        }
    }

    @PostMapping("/api/profile/save")
    public ResponseEntity<ServiceResult<?>> saveProfile(@RequestBody ProfileEditRest editRest) {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        user.setFirstName(editRest.getFirstName());
        user.setSecondName(editRest.getSecondName());
        userRepository.save(user);
        return ResponseEntity.ok(ServiceResult.ok());
    }

    @GetMapping("/api/profile/img")
    public ResponseEntity<byte[]> getProfileImg() {
        User user = currentUserService.getCurrentUser()
            .orElseThrow(NoSuchElementException::new);
        if(user.getImgContent() != null)
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(user.getImgContent().length);
            return new ResponseEntity<>(user.getImgContent(), headers, HttpStatus.OK);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/api/profile")
    public ResponseEntity<ProfileEditRest> getProfileData()
    {
        ProfileEditRest editRest = currentUserService.getCurrentUser()
            .map(user -> new ProfileEditRest(user.getFirstName(), user.getSecondName()))
            .orElseThrow(NoSuchElementException::new);
        return ResponseEntity.ok(editRest);
    }

    public static class ProfileEditRest
    {
        private String firstName;
        private String secondName;

        public ProfileEditRest()
        {
            // for Spring
        }

        public ProfileEditRest(String firstName, String secondName)
        {
            this.firstName = firstName;
            this.secondName = secondName;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public void setFirstName(final String firstName)
        {
            this.firstName = firstName;
        }

        public String getSecondName()
        {
            return secondName;
        }

        public void setSecondName(final String secondName)
        {
            this.secondName = secondName;
        }

    }

}
