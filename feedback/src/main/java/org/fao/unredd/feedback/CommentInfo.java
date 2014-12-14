package org.fao.unredd.feedback;

public class CommentInfo {

	private int id;
	private String email;
	private String verificationCode;

	public CommentInfo(int id, String email, String verificationCode) {
		this.id = id;
		this.email = email;
		this.verificationCode = verificationCode;
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

}
