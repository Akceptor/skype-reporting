package org.asg.report;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CleanJob implements Job {

	private final static Logger LOGGER = Logger.getLogger(CleanJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		App.statuses.clear(); // Clear statuses list
		ReportMessageAdapter.isSentToday = false; // Remove 'sent' flag
		LOGGER.info("Status Queue was just cleared"); //$NON-NLS-1$
	}
}
