package org.asg.report;

import java.util.Arrays;
import java.util.List;

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
	static List<String> allowedUsers = Arrays.asList(new String[] { "akceptor.motofan.ru", "yankolyaspas" });

	public static void main(String[] args) throws SkypeException {
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

			private void processStatus(ChatMessage received) throws SkypeException {
				@SuppressWarnings("nls")
				String log = "[" + received.getTime() + "] " + received.getSenderId() + " says: "
						+ received.getContent();
				System.out.println(log);
				received.getSender().send(log);
			}
		});
	}
}
