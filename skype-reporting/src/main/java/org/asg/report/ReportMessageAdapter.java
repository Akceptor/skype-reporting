package org.asg.report;

import static org.asg.report.App.NICKNAMES;
import static org.asg.report.App.allowedUsers;
import static org.asg.report.App.statuses;

import java.util.Map;

import org.apache.log4j.Logger;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.SkypeException;

public class ReportMessageAdapter extends ChatMessageAdapter {
	private final static Logger LOGGER = Logger.getLogger(ReportMessageAdapter.class);
	static boolean isSentToday = false;

	@Override
	public void chatMessageReceived(ChatMessage received) throws SkypeException {
		LOGGER.info("Got Skype message, processing... "); //$NON-NLS-1$
		if (received.getType().equals(ChatMessage.Type.SAID)) {
			if (!allowedUsers.contains(received.getSenderId())) {
				received.getSender().send("Sorry, you are not permitted to access this service"); //$NON-NLS-1$
			} else {
				if (!isSentToday) {
					checkIfContainsCommandAndProcess(received);
				} else {
					received.getSender().send("Sorry, your status for today was already submitted"); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Checks if the message contains some command and processes it according
	 * the rule
	 * 
	 * @param received
	 *            Skype message
	 * @throws SkypeException
	 */
	@SuppressWarnings("static-method")
	private void checkIfContainsCommandAndProcess(ChatMessage received) throws SkypeException {
		if (received.getContent().startsWith("[")) { //$NON-NLS-1$
			// TODO remove that ugly substring stuff. Use RegExp instead

			String str = received.getContent().substring(1);
			String username = str.substring(0, str.indexOf("]")); //$NON-NLS-1$
			processStatus(received, username,
					received.getContent().substring(received.getContent().indexOf("]") + 1).trim()); //$NON-NLS-1$
		} else {
			processStatus(received, received.getSenderId(), received.getContent());
		}
	}

	/**
	 * Processes status message and puts it into the statuses map
	 * 
	 * @param received
	 *            {@link ChatMessage} containing current user status
	 * @param senderId
	 *            sender's Skype name. Normally it is retrieved from the
	 *            {@link ChatMessage} but also could be specified via [username]
	 *            construction (in case we need to send message on behalf
	 *            different user)
	 * @param content
	 *            sender's status message Normally it is retrieved from the
	 *            {@link ChatMessage} but should be specified if [username]
	 *            construction is used (to remove [username] clause from the
	 *            message)
	 * @throws SkypeException
	 */
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

	/**
	 * Builds the status string to be sent to the last user (or client)
	 * 
	 * @param userStatuses
	 *            statuses for all team mates
	 * @return formatted status string
	 */
	private static String buildStatus(Map<String, String> userStatuses) {
		StringBuilder statusBuilder = new StringBuilder("Status:\n\t"); //$NON-NLS-1$
		for (String userId : userStatuses.keySet()) {
			statusBuilder.append(NICKNAMES.get(userId)).append(": ").append(userStatuses.get(userId)).append("\n\t"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return statusBuilder.toString();
	}

}
