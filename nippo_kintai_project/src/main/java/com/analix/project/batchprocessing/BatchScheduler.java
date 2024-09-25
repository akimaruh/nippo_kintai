package com.analix.project.batchprocessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class BatchScheduler {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

//	@Scheduled(cron = "0 50 13 * * MON-FRI")
//	@Scheduled(initialDelay = 10000, fixedRate = 10000)
	public void runBatchJob() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();
		jobLauncher.run(job, jobParameters);
	}

}
