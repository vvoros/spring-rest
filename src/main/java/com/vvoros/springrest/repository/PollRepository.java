package com.vvoros.springrest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vvoros.springrest.domain.Poll;

@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {

}
