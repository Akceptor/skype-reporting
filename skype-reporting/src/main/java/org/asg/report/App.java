package org.asg.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.skype.Skype;
import com.skype.SkypeException;

/**
 * Skype status sender :)
 */
public class App {
	private final static Logger LOGGER = Logger.getLogger(App.class);

	static Map<String, String> NICKNAMES = new HashMap<String, String>();
	static Set<String> allowedUsers;
	static Map<String, String> statuses = new HashMap<String, String>();

	public static void main(String[] args) throws SkypeException {
		LOGGER.info("Skype running: " + Skype.isRunning()); //$NON-NLS-1$
		loadProperties();
		Skype.setDaemon(false); // to prevent exiting from this progra

		Skype.addChatMessageListener(new ReportMessageAdapter());

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
