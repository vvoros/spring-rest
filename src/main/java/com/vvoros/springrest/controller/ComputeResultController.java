package com.vvoros.springrest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vvoros.springrest.domain.Vote;
import com.vvoros.springrest.dto.OptionCount;
import com.vvoros.springrest.dto.VoteResult;
import com.vvoros.springrest.repository.VoteRepository;

@RestController
public class ComputeResultController {

	@Autowired
	private VoteRepository voteRepository;
	
	@RequestMapping(value = "computeresult", method = RequestMethod.GET)
	public ResponseEntity<VoteResult> computeResult(@RequestParam Long pollId) {
		Iterable<Vote> allVotes = voteRepository.findByPoll(pollId);
		
		VoteResult voteResult = computeVoteResult(allVotes);
		
		return new ResponseEntity<>(voteResult, HttpStatus.OK);
	}
	
	private VoteResult computeVoteResult(Iterable<Vote> allVotes) {
		VoteResult voteResult = new VoteResult();
		
		int totalVotes = 0;
		Map<Long, OptionCount> tempMap = new HashMap<>();
		for (Vote vote: allVotes) {
			totalVotes++;
			
			OptionCount optionCount = tempMap.get(vote.getOption().getId());
			if (optionCount == null) {
				optionCount = new OptionCount();
				optionCount.setOptionId(vote.getOption().getId());
				tempMap.put(vote.getOption().getId(), optionCount);
			}
			optionCount.setCount(optionCount.getCount() + 1);
		}
		voteResult.setTotalVotes(totalVotes);
		voteResult.setResults(tempMap.values());
		
		return voteResult;
	}
	
}
