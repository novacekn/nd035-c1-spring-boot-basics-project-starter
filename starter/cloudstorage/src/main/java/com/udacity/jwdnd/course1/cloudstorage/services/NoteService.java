package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public int createNote(Note note) {
        return noteMapper.insert(new Note(null, note.getNoteTitle(), note.getNoteDescription(), note.getUserId()));
    }

    public Note getNoteByNoteId(Integer noteId) {
        return noteMapper.getNoteByNoteId(noteId);
    }

    public List<Note> getNotesByUserId(Integer userId) {
        return noteMapper.getNotesByUserId(userId);
    }

    public int deleteNote(Integer noteId) {
        return noteMapper.delete(noteId);
    }

    public int editNote(Note note) {
        return noteMapper.update(note.getNoteId(), note.getNoteTitle(), note.getNoteDescription());
    }
}
