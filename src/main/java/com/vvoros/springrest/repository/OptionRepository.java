package com.vvoros.springrest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vvoros.springrest.domain.Option;

@Repository
public interface OptionRepository extends CrudRepository<Option, Long> {

}
