package org.fao.unredd.feedback;

public class CommentInfo {

	private int id;
	private String email;
	private String verificationCode;
	private String language;

	public CommentInfo(int id, String email, String verificationCode,
			String language) {
		this.id = id;
		this.email = email;
		this.verificationCode = verificationCode;
		this.language = language;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public String getMail() {
		return email;
	}

	public int getId() {
		return id;
	}

	public String getLanguage() {
		return language;
	}
}
