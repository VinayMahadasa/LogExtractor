package com.example.accessingdatajpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class AccessingDataJpaApplication {

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataJpaApplication.class);
	}

	@Bean
	public CommandLineRunner demo(LogFileRepository repository) throws IOException {

		return (args) -> {

			List<LogFile> logFileList = getLogFiles();

			log.info("Converted logfile to list of Log file objects");

			List<LogFile> startedLogs = logFileList.parallelStream()
							.filter(logFile -> "STARTED".equalsIgnoreCase(logFile.getState()))
					.sorted(Comparator.comparing(LogFile::getId))
					.collect(Collectors.toList());

			List<LogFile> finishedLogs = logFileList.parallelStream()
					.filter(logFile -> "FINISHED".equalsIgnoreCase(logFile.getState()))
					.sorted(Comparator.comparing(LogFile::getId))
					.collect(Collectors.toList());

			log.info("Save list of log entity objects to h2");
			repository.saveAll(getLogFileEntities(startedLogs, finishedLogs));

			log.info("Print all stored log entity values in h2");

			for (LogFileEntity logFile : repository.findAll()) {
				log.info(String.valueOf(logFile));
			}
		};
	}

	private List<LogFileEntity> getLogFileEntities(List<LogFile> startedLogs, List<LogFile> finishedLogs) {
		List<LogFileEntity> logFileEntityList = new ArrayList<>();

		for (LogFile start: startedLogs) {
			for (LogFile end: finishedLogs) {
				if (start.getId().equalsIgnoreCase(end.getId())){
					log.info("Found end log for event id {}", start.getId());
					createLogEntity(logFileEntityList, start, end);
					break;
				}
			}
		}

		return logFileEntityList;
	}

	private void createLogEntity(List<LogFileEntity> logFileEntityList, LogFile start, LogFile end) {
		log.info("Create new entity and persist values");
		LogFileEntity logFileEntity = new LogFileEntity();
		logFileEntity.setEventId(start.getId());
		logFileEntity.setEventDuration(end.getTimestamp() - start.getTimestamp());
		logFileEntity.setType(start.getType());
		logFileEntity.setHost(start.getHost());
		logFileEntity.setAlert(logFileEntity.getEventDuration() > 4L);
		logFileEntityList.add(logFileEntity);
	}

	private List<LogFile> getLogFiles() throws URISyntaxException, IOException {
		Path path = Paths.get(getClass().getClassLoader()
				.getResource("logfile.txt").toURI());

		Stream<String> lines = Files.lines(path);
		String data = lines.collect(Collectors.joining("\n"));
		lines.close();

		String[] logLine = data.split("\n");

		List<LogFile> logFileList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		for (String s : logLine) {
			LogFile logFile = objectMapper.readValue(s, LogFile.class);
			logFileList.add(logFile);
		}
		return logFileList;
	}

}
