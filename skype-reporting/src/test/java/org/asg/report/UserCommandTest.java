package org.asg.report;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.skype.ChatMessage;
import com.skype.SkypeException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChatMessage.class)
public class UserCommandTest {

	static ChatMessage chatMessageWithoutCommand, chatMessageWithoutCommandButWithBrakets;
	static ChatMessage chatMessageWithCommand;

	@SuppressWarnings("nls")
	@BeforeClass
	public static void setUp() throws SkypeException {
		chatMessageWithoutCommand = PowerMockito.mock(ChatMessage.class);
		PowerMockito.when(chatMessageWithoutCommand.getSenderId()).thenReturn("user");
		PowerMockito.when(chatMessageWithoutCommand.getContent()).thenReturn("Just a status");
		chatMessageWithoutCommandButWithBrakets = PowerMockito.mock(ChatMessage.class);
		PowerMockito.when(chatMessageWithoutCommandButWithBrakets.getSenderId()).thenReturn("user");
		PowerMockito.when(chatMessageWithoutCommandButWithBrakets.getContent()).thenReturn("Just [a] status");
		chatMessageWithCommand = Mockito.mock(ChatMessage.class);
		PowerMockito.when(chatMessageWithCommand.getSenderId()).thenReturn("user");
		PowerMockito.when(chatMessageWithCommand.getContent()).thenReturn("[someUser] Just a status");
		// TODO empty username check
	}

	@SuppressWarnings({ "static-method", "nls" })
	@Test
	public void testStatusContainsUser() throws SkypeException {
		StatusMessageBean statusMessageBean = new ReportMessageAdapter()
				.prepareStatusMessageBean(chatMessageWithoutCommand);
		assertEquals(statusMessageBean.getSender(), "user");
		assertEquals(statusMessageBean.getContent(), "Just a status");
		statusMessageBean = new ReportMessageAdapter()
				.prepareStatusMessageBean(chatMessageWithoutCommandButWithBrakets);
		assertEquals(statusMessageBean.getSender(), "user");
		assertEquals(statusMessageBean.getContent(), "Just [a] status");
		statusMessageBean = new ReportMessageAdapter().prepareStatusMessageBean(chatMessageWithCommand);
		assertEquals(statusMessageBean.getSender(), "someUser");
		assertEquals(statusMessageBean.getContent(), "Just a status");
	}

}
