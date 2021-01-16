package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.UserFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFileService {
    private final FileMapper fileMapper;

    public UserFileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public int createFile(UserFile userFile) {
        return fileMapper.insert(new UserFile(null, userFile.getFileName(), userFile.getContentType(), userFile.getFileSize(), userFile.getUserId(), userFile.getFileData()));
    }

    public UserFile getFileByFileId(Integer fileId) {
        return fileMapper.getFileByFileId(fileId);
    }

    public List<UserFile> getFilesByUserId(Integer userId) {
        return fileMapper.getFilesByUserId(userId);
    }

    public int deleteFile(Integer fileId) {
        return fileMapper.delete(fileId);
    }

    public boolean fileNameIsUnique(String fileName) {
        return fileMapper.getFileByFileName(fileName) == null;
    }
}
