package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.models.Credential;
import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import com.udacity.jwdnd.course1.cloudstorage.models.UserFile;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final NoteService noteService;
    private final UserService userService;
    private final CredentialService credentialService;
    private final UserFileService userFileService;

    public HomeController(NoteService noteService, UserService userService, CredentialService credentialService, UserFileService userFileService) {
        this.noteService = noteService;
        this.userService = userService;
        this.credentialService = credentialService;
        this.userFileService = userFileService;
    }

    @GetMapping
    public String getHomePage(Authentication authentication, Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        List<Note> notes = noteService.getNotesByUserId(userId);
        List<Credential> credentials = credentialService.getCredentialsByUserId(userId);
        List<UserFile> files = userFileService.getFilesByUserId(userId);
        model.addAttribute("notes", notes);
        model.addAttribute("credentials", credentials);
        model.addAttribute("files", files);
        return "home";
    }

    /*
        NOTES
     */

    @PostMapping("/notes")
    public String postNote(Authentication authentication, Model model, Integer noteId, String noteTitle, String noteDescription) {
        int userId = userService.getUser(authentication.getName()).getUserId();

        if (noteId == null) {
            Note note = new Note(null, noteTitle, noteDescription, userId);
            int rows = noteService.createNote(note);
            if (rows > 0) {
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "Your note has been successfully created.");
            } else {
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "An error has occurred. Note was not created.");
            }
        } else {
            Note note = noteService.getNoteByNoteId(noteId);
            note.setNoteTitle(noteTitle);
            note.setNoteDescription(noteDescription);
            int rows = noteService.editNote(note);
            if (rows > 0) {
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "Your note has been successfully edited.");
            } else {
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "An error has occurred. Note was not edited.");
            }
        }
        return "result";
    }

    @GetMapping("/notes/delete/{noteId}")
    public String deleteNote(@PathVariable Integer noteId, Model model) {
        int rows = noteService.deleteNote(noteId);
        if (rows > 0) {
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "The note has been deleted successfully.");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "An error has occurred. Note was not deleted.");
        }
        return "result";
    }

    /*
        CREDENTIALS
     */

    @PostMapping("/credentials")
    public String postCredential(Authentication authentication, Model model, Integer credentialId, String url, String username, String password) {
        int userId = userService.getUser(authentication.getName()).getUserId();
        if (credentialId == null) {
            Credential credential = new Credential(null, url, username, null, password, userId);
            int rows = credentialService.createCredential(credential);
            if (rows > 0) {
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "The credential has been successfully created.");
            } else {
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "An error has occurred. Credential was not created.");
            }
        } else {
            Credential credential = credentialService.getCredentialByCredentialId(credentialId);
            credential.setUrl(url);
            credential.setUsername(username);
            credential.setPassword(password);
            int rows = credentialService.editCredential(credential);
            if (rows > 0) {
                model.addAttribute("success", true);
                model.addAttribute("successMessage", "The credential has been successfully edited.");
            } else {
                model.addAttribute("error", true);
                model.addAttribute("errorMessage", "An error has occurred. Credential was not edited.");
            }
        }
        return "result";
    }

    @GetMapping("/credentials/delete/{credentialId}")
    public String deleteCredential(@PathVariable Integer credentialId, Model model) {
        int rows = credentialService.deleteCredential(credentialId);
        if (rows > 0) {
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "The credential has been deleted successfully.");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "An error has occurred. Credential not deleted.");
        }
        return "result";
    }

    /*
        FILES
     */

    @PostMapping("/files")
    public String postFile(Authentication authentication, Model model, MultipartFile fileUpload) throws IOException {
        String error = null;
        int userId = userService.getUser(authentication.getName()).getUserId();
        if (Objects.requireNonNull(fileUpload.getOriginalFilename()).isEmpty()) {
            error = "No file has been chosen. Please choose a file to upload.";
        }
        UserFile file = new UserFile(null, fileUpload.getOriginalFilename(), fileUpload.getContentType(), fileUpload.getSize(), userId, fileUpload.getBytes());

        if (!userFileService.fileNameIsUnique(file.getFileName())) {
            error = "A file with this name already exists.";
        }

        if (error == null) {
            int row = userFileService.createFile(file);
            if (row < 0) {
                error = "An error has occurred and the file was not uploaded. Please try again.";
            }
        }

        if (error == null) {
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Your file has been successfully uploaded!");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", error);
        }

        return "result";
    }

    @GetMapping("/files/delete/{fileId}")
    public String deleteFile(@PathVariable Integer fileId, Model model) {
        int rows = userFileService.deleteFile(fileId);
        if (rows > 0) {
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "The file has been deleted successfully.");
        } else {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "An error has occurred. File not deleted.");
        }
        return "result";
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity downloadFile(@PathVariable Integer fileId) {
        UserFile userFile = userFileService.getFileByFileId(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userFile.getFileName())
                .body(userFile.getFileData());
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public String handleError(SizeLimitExceededException e, Model model) {
        model.addAttribute("statusCode", "403 FORBIDDEN");
        model.addAttribute("errorMessage", "The file you are attempting to upload is too large.");
        return "error";
    }
}
