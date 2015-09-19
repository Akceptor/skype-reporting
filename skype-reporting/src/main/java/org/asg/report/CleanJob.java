package org.asg.report;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CleanJob implements Job {

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		App.statuses.clear();		
	}
	

}
