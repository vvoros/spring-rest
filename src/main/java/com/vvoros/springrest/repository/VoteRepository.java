package com.vvoros.springrest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vvoros.springrest.domain.Vote;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {

	@Query(value = "select v.* from Option o, Vote v where o.Poll_Id = ?1 and v.Option_Id = o.Option_Id", nativeQuery = true)
	public Iterable<Vote> findByPoll(Long pollId);
	
}
