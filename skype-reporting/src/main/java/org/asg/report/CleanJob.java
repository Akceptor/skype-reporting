package org.asg.report;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CleanJob implements Job {
	
	private final static Logger LOGGER = Logger.getLogger(CleanJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		App.statuses.clear();		
		LOGGER.info("Status Queue was just cleared");
	}
	

}
