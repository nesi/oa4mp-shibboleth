package nz.ac.tuakiri.myproxy.oa4mp.server.servlet;

import java.io.IOException;
import java.io.StringReader;

import au.com.bytecode.opencsv.CSVReader;

public class ShibUsernameTransformerConfig {
	public ShibUsernameTransformerConfig(String attributesToSendString,
			boolean myproxyWorkaround, boolean requireHeader, boolean processDuringStart, boolean returnDnAsUsername) {
		if (attributesToSendString != null) {
			CSVReader reader = new CSVReader(new StringReader(
					attributesToSendString));
			try {
				attributesToSend = reader.readNext();
				reader.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			attributesToSend = null;
		}
		this.myproxyWorkaround = myproxyWorkaround;
		this.requireHeader = requireHeader;
		this.processDuringStart = processDuringStart;
		this.returnDnAsUsername = returnDnAsUsername;
	}

	private final String[] attributesToSend;
	private final boolean myproxyWorkaround;
	private final boolean requireHeader;
	private final boolean processDuringStart;
	private final boolean returnDnAsUsername;

	public boolean isMyproxyWorkaround() {
		return myproxyWorkaround;
	}

	public String[] getAttributesToSend() {
		return attributesToSend;
	}

	public boolean isRequireHeader() {
		return requireHeader;
	}
	
	public boolean isProcessDuringStart() {
		return processDuringStart;
	}

	public boolean isReturnDnAsUsername() {
		return returnDnAsUsername;
	}

}
