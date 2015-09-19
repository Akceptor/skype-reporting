package org.asg.report;

import static org.asg.report.App.NICKNAMES;
import static org.asg.report.App.allowedUsers;
import static org.asg.report.App.statuses;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.SkypeException;

public class ReportMessageAdapter extends ChatMessageAdapter {
	private final static Logger LOGGER = Logger.getLogger(ReportMessageAdapter.class);
	private static final String REGULAR_EXPRESSION_FOR_NAME = "^\\[([^\\]]*)\\]"; //$NON-NLS-1$
	static boolean isSentToday = false;

	@Override
	public void chatMessageReceived(ChatMessage received) throws SkypeException {
		LOGGER.info("Got Skype message, processing... "); //$NON-NLS-1$
		if (received.getType().equals(ChatMessage.Type.SAID)) {
			if (!allowedUsers.contains(received.getSenderId())) {
				received.getSender().send("Sorry, you are not permitted to access this service"); //$NON-NLS-1$
			} else {
				if (!isSentToday) {
					StatusMessageBean smBean = prepareStatusMessageBean(received);
					processStatus(smBean);
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
	 * @return
	 * @throws SkypeException
	 */
	@SuppressWarnings("static-method")
	StatusMessageBean prepareStatusMessageBean(ChatMessage received) throws SkypeException {
		Pattern pattern = Pattern.compile(REGULAR_EXPRESSION_FOR_NAME);
		Matcher matcher = pattern.matcher(received.getContent().trim());
		if (matcher.find()) {
			String username = matcher.group(1);
			return new StatusMessageBean(received, username,
					received.getContent().replaceFirst(REGULAR_EXPRESSION_FOR_NAME, "").trim()); //$NON-NLS-1$
		} else {
			return new StatusMessageBean(received, received.getSenderId(), received.getContent());
		}
	}

	/**
	 * Processes status message and puts it into the statuses map
	 * 
	 * @param received
	 *            {@link StatusMessageBean} containing current user data
	 * @throws SkypeException
	 */
	static void processStatus(StatusMessageBean received) throws SkypeException {
		statuses.put(received.getSender(), received.getContent());
		LOGGER.info("Received " + statuses.size() + " statuses for " + allowedUsers.size() + " users"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		if (statuses.size() == allowedUsers.size()) {// last user
			String totalStatus = buildStatus(statuses);
			LOGGER.info("\n" + totalStatus); //$NON-NLS-1$
			received.getFullMessage().getSender().send(totalStatus);
			statuses.clear();
			isSentToday = true;
		} else {
			String savedMessage = "Status saved: " + received.getContent();//$NON-NLS-1$
			LOGGER.info(savedMessage);
			received.getFullMessage().getSender().send(savedMessage);
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
