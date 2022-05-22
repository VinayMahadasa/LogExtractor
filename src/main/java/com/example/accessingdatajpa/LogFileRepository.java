package com.example.accessingdatajpa;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LogFileRepository extends CrudRepository<LogFileEntity, Long> {
}
