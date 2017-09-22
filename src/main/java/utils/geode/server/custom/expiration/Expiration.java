package utils.geode.server.custom.expiration;

import org.apache.geode.cache.ExpirationAction;

public class Expiration {

	private String field;
	private String value;
	private ExpirationAction action;
	private int seconds;

	public Expiration(String field, String value, int seconds, ExpirationAction action) {
		this.field = field;
		this.value = value;
		this.action = action;
		this.seconds = seconds;
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}

	public ExpirationAction getAction() {
		return action;
	}

	public int getSeconds() {
		return seconds;
	}
}
