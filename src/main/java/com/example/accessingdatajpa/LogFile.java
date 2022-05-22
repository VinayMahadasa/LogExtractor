package com.example.accessingdatajpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
public class LogFile {

	private String id;

	private String state;
	private long timestamp;
	private String type;
	private String host;

}
