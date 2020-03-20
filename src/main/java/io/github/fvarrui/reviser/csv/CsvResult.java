package io.github.fvarrui.reviser.csv;

import com.opencsv.bean.CsvBindByName;

public class CsvResult {

	@CsvBindByName
	private Integer score;

	@CsvBindByName
	private String name;

	@CsvBindByName
	private String feedback;

	@CsvBindByName
	private String email;

	public CsvResult() {
	}

	public CsvResult(Integer score, String name, String feedback, String email) {
		this.score = score;
		this.name = name;
		this.feedback = feedback;
		this.email = email;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Result [score=" + getScore() + ", name=" + getName() + ", feedback=" + getFeedback() + ", email="
				+ getEmail() + "]";
	}

}
