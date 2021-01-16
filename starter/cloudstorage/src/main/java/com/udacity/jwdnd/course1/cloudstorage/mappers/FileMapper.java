package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.models.UserFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
    UserFile getFileByFileId(Integer fileId);

    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    List<UserFile> getFilesByUserId(Integer userId);

    @Select("SELECT * FROM FILES WHERE filename = #{fileName}")
    UserFile getFileByFileName(String fileName);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    int delete(Integer fileId);

    @Insert("INSERT INTO FILES (fileId, filename, contenttype, filesize, userid, filedata) VALUES (#{fileId}, #{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(UserFile userFile);
}
