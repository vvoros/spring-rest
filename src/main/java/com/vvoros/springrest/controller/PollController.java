package com.vvoros.springrest.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.vvoros.springrest.domain.Poll;
import com.vvoros.springrest.exception.ResourceNotFoundException;
import com.vvoros.springrest.repository.PollRepository;

@RestController
public class PollController {

	@Autowired
	private PollRepository pollRepository;
	
	@RequestMapping(value = "/polls", method = RequestMethod.GET)
	public ResponseEntity<Iterable<Poll>> getAllPolls() {
		Iterable<Poll> allPolls = pollRepository.findAll();
		return new ResponseEntity<>(allPolls, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/polls", method = RequestMethod.POST)
	public ResponseEntity<?> createPoll(@Valid @RequestBody Poll poll) {
		poll = pollRepository.save(poll);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(poll.getId())
				.toUri());
		
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/polls/{pollId}", method = RequestMethod.GET)
	public ResponseEntity<Poll> getPoll(@PathVariable Long pollId) {
		verifyPoll(pollId);
		
		Poll poll = pollRepository.findOne(pollId);
		return new ResponseEntity<>(poll, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/polls/{pollId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updatePoll(@Valid @RequestBody Poll poll, @PathVariable Long pollId) {
		verifyPoll(pollId);
		
		pollRepository.save(poll);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/polls/{pollId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deletePoll(@PathVariable Long pollId) {
		verifyPoll(pollId);
		
		pollRepository.delete(pollId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	protected void verifyPoll(Long pollId) throws ResourceNotFoundException {
		Poll poll = pollRepository.findOne(pollId);
		if (poll == null) {
			throw new ResourceNotFoundException(String.format("Poll with id %d not found", pollId));
		}
	}
	
}
