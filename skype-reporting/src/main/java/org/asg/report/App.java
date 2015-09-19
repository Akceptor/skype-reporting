package org.asg.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;

/**
 * Hello world!
 *
 */
public class App {

	@SuppressWarnings("nls")
	static Map<String, String> NICKNAMES = new HashMap<String, String>() {
		{
			put("akceptor.motofan.ru", "VO");
			put("yankolyaspas", "MY");
		}
	};
	static Set<String> allowedUsers = NICKNAMES.keySet();
	static Map<String, String> statuses = new HashMap<String, String>();

	public static void main(String[] args) throws SkypeException {
		System.out.println("Please, bring Skype to foreground!"); //$NON-NLS-1$
		Skype.setDaemon(false); // to prevent exiting from this program

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
		System.out.println(statuses);
	}

	private static String buildStatus(Map<String, String> userStatuses) {
		StringBuilder statusBuilder = new StringBuilder("Status:\n"); //$NON-NLS-1$
		for (String userId : userStatuses.keySet()) {
			statusBuilder.append(NICKNAMES.get(userId)).append(": ").append(userStatuses.get(userId)).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return statusBuilder.toString();
	}
}
