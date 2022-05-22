package com.example.accessingdatajpa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@ToString
public class LogFileEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long logEntityId;

	private String eventId;
	private boolean alert;
	private long eventDuration;
	private String type;
	private String host;

}
