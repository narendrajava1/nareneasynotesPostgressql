/*
 *
 */
package com.easynotes.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easynotes.exception.NotesBaseException;
import com.easynotes.model.DemoProperties;
import com.easynotes.model.Error;
import com.easynotes.model.Note;
import com.easynotes.model.ServiceResponse;
import com.easynotes.repository.NoteRepository;

@RestController
@RequestMapping("/api")
public class NoteController {

	@Autowired
	DemoProperties c;
	@Autowired
	NoteRepository noteRepository;
	// Create a new Note
	@PostMapping("/notes")
	public ServiceResponse<Long> createNote(@Valid @RequestBody Note note) {
		final ServiceResponse<Long> response = new ServiceResponse<>();
		final Optional<Note> noteEnt = noteRepository.saveReturnOptional(note);
		noteEnt.ifPresent(noteRetn -> response.setData(noteRetn.getId()));


		return response;
	}

	// Delete a Note
	@DeleteMapping("/notes/{id}")
	public ServiceResponse<Long> deleteNote(
			@PathVariable(value = "id") Long noteId) {
		final ServiceResponse<Long> response = new ServiceResponse<>();

		final Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new NotesBaseException(
						String.format(c.getnote,
								"no records found related to this note id"),
						HttpStatus.NO_CONTENT.value()));
		noteRepository.delete(note);
		response.setData(note.getId());
		return response;

	}

	// Get All Notes
	@GetMapping("/notes")
	public ServiceResponse<List<Note>> getAllNotes() {
		final ServiceResponse<List<Note>> response = new ServiceResponse<>();
		final List<Note> allNotes = noteRepository.findAll();
		if (!CollectionUtils.isEmpty(allNotes)) {
			response.setData(noteRepository.findAll());
		} else {
			response.setCode(-1);
			final Error error = new Error();
			error.setMessage(String.format(c.getallnotes,
					"no records found related to this note id"));
			error.setError(HttpStatus.NO_CONTENT.name());
			response.setError(error);

		}

		return response;
	}

	// Get a Single Note
	@GetMapping("/notes/{id}")
	public ServiceResponse<Note> getNoteById(
			@PathVariable(value = "id") Long noteId)
	{
		final ServiceResponse<Note> response = new ServiceResponse<>();

		final Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new NotesBaseException(
						String.format(c.getnote,
								"no records found related to this note id"),
						HttpStatus.NO_CONTENT.value()));
		response.setData(note);
		return response;
	}

	// Update a Note
	@PutMapping("/notes/{id}")
	public ServiceResponse<Note> updateNote(
			@PathVariable(value = "id") Long noteId,
			@Valid @RequestBody Note noteDetails) {
		final ServiceResponse<Note> response = new ServiceResponse<>();

		final Note note = noteRepository.findById(noteId)
				.orElseThrow(() -> new NotesBaseException(
						String.format(c.getnote,
								"no records found related to this note id"),
						HttpStatus.NO_CONTENT.value()));

		note.setTitle(noteDetails.getTitle());
		note.setContent(noteDetails.getContent());
		note.setCreatedAt(note.getCreatedAt());
		final Note updatedNote = noteRepository.save(note);
		response.setData(updatedNote);
		return response;
	}
}
