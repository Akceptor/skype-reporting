package org.asg.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
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
	static boolean isSentToday = false;

	public static void main(String[] args) throws SkypeException {
		LOGGER.info("Skype running: " + Skype.isRunning()); //$NON-NLS-1$
		loadProperties();
		Skype.setDaemon(false); // to prevent exiting from this progra

		Skype.addChatMessageListener(new ChatMessageAdapter() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void chatMessageReceived(ChatMessage received) throws SkypeException {
				LOGGER.info("Got Skype message, processing... "); //$NON-NLS-1$
				if (received.getType().equals(ChatMessage.Type.SAID)) {
					if (!allowedUsers.contains(received.getSenderId())) {
						received.getSender().send("Sorry, you are not permitted to access this service"); //$NON-NLS-1$
					} else {
						if (!isSentToday) {
							// Check if status contains command
							if (received.getContent().startsWith("[")) { //$NON-NLS-1$
								String str = received.getContent().substring(1);
								String username = str.substring(0, str.indexOf("]")); //$NON-NLS-1$
								processStatus(received, username,
										received.getContent().substring(received.getContent().indexOf("]") + 1).trim()); //$NON-NLS-1$
							} else {
								processStatus(received, received.getSenderId(), received.getContent());
							}
						} else {
							received.getSender().send("Sorry, your status for today was already submitted"); //$NON-NLS-1$
						}
					}
				}
			}

		});

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

	static void processStatus(ChatMessage received, String senderId, String content) throws SkypeException {
		statuses.put(senderId, content);
		LOGGER.info("Received " + statuses.size() + " statuses for " + allowedUsers.size() + " users"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		if (statuses.size() == allowedUsers.size()) {// last user
			String totalStatus = buildStatus(statuses);
			LOGGER.info("\n" + totalStatus); //$NON-NLS-1$
			received.getSender().send(totalStatus);
			statuses.clear();
			isSentToday = true;
		} else {
			String savedMessage = "Status saved: " + received.getContent();//$NON-NLS-1$
			LOGGER.info(savedMessage);
			received.getSender().send(savedMessage);
		}
	}

	private static String buildStatus(Map<String, String> userStatuses) {
		StringBuilder statusBuilder = new StringBuilder("Status:\n\t"); //$NON-NLS-1$
		for (String userId : userStatuses.keySet()) {
			statusBuilder.append(NICKNAMES.get(userId)).append(": ").append(userStatuses.get(userId)).append("\n\t"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return statusBuilder.toString();
	}
}
