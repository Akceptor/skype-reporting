package org.asg.report;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.skype.Skype;

/**
 * Skype status sender :)
 */
public class App {
	private final static Logger LOGGER = Logger.getLogger(App.class);

	static Map<String, String> NICKNAMES = new HashMap<String, String>();
	static Set<String> allowedUsers;
	static Map<String, String> statuses = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		initScheduler();
		LOGGER.info("Skype running: " + Skype.isRunning()); //$NON-NLS-1$
		loadProperties();
		Skype.setDaemon(false); // to prevent exiting from this progra

		Skype.addChatMessageListener(new ReportMessageAdapter());

	}

	private static void initScheduler() throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory();
	    Scheduler sched = sf.getScheduler();	    
	    JobDetail job = newJob(CleanJob.class).withIdentity("job1", "group1").build();

	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);        
	    
        SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(calendar.getTime()).
	    		withSchedule(simpleSchedule().withIntervalInHours(24).repeatForever()).build();

	    sched.scheduleJob(job, trigger);
	    sched.start();
	}

	private static void loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(App.class.getClassLoader().getResourceAsStream("users.properties")); //$NON-NLS-1$
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				NICKNAMES.put(key, value);
			}
			allowedUsers = NICKNAMES.keySet();
		} catch (Exception exception) {
			LOGGER.error(exception);
		}
	}

}
