package utils.geode.server.custom.expiration;

public class Expiration {

	private String field;
	private String value;
	private int seconds;

	public Expiration(String field, String value, int seconds) {
		this.field = field;
		this.value = value;
		this.seconds = seconds;
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}

	public int getSeconds() {
		return seconds;
	}
}
