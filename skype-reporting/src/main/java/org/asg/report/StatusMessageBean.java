package org.asg.report;

import com.skype.ChatMessage;

public class StatusMessageBean {

	private ChatMessage message;
	private String sender;
	private String content;

	public StatusMessageBean(ChatMessage message, String sender, String content) {
		super();
		this.message = message;
		this.sender = sender;
		this.content = content;
	}

	public ChatMessage getFullMessage() {
		return message;
	}

	public void setFullMessage(ChatMessage message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
