package org.asg.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 * Skype status sender :)
 */
public class App {

	static Map<String, String> NICKNAMES = new HashMap<String, String>();
	static Set<String> allowedUsers;
	static Map<String, String> statuses = new HashMap<String, String>();

	public static void main(String[] args) throws SkypeException {
		System.out.println("Please, bring Skype to foreground!"); //$NON-NLS-1$
		loadProperties();
		Skype.setDaemon(false); // to prevent exiting from this program
		Skype.setDebug(true);

		Skype.addChatMessageListener(new ChatMessageAdapter() {
			@Override
			public void chatMessageReceived(ChatMessage received) throws SkypeException {
				if (received.getType().equals(ChatMessage.Type.SAID)) {
					if (!allowedUsers.contains(received.getSenderId())) {
						received.getSender().send("Sorry, you are not permitted to access this service"); //$NON-NLS-1$
					} else {
						processStatus(received);
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
			// TODO
			exception.printStackTrace();
		}
	}

	static void processStatus(ChatMessage received) throws SkypeException {
		statuses.put(received.getSenderId(), received.getContent());
		if (statuses.size() == allowedUsers.size()) {// last user
			System.out.println(statuses);
			received.getSender().send(buildStatus(statuses));
			statuses.clear();
		} else {
			System.out.println("Received " + statuses.size() + " statuses for " + allowedUsers.size() + " users"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			received.getSender().send("Status saved: " + received.getContent()); //$NON-NLS-1$
		}
		System.out.println(buildStatus(statuses));
	}

	private static String buildStatus(Map<String, String> userStatuses) {
		StringBuilder statusBuilder = new StringBuilder("Status:\n"); //$NON-NLS-1$
		for (String userId : userStatuses.keySet()) {
			statusBuilder.append(NICKNAMES.get(userId)).append(": ").append(userStatuses.get(userId)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return statusBuilder.toString();
	}
}
