package io.github.fvarrui.reviser.csv;

import com.opencsv.bean.CsvBindByName;

public class CsvStudent {

	@CsvBindByName(column = "Nombre")
	private String name;

	@CsvBindByName(column = "Apellido(s)")
	private String surname;

	@CsvBindByName(column = "Dirección de correo")
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return getName().trim() + " " + getSurname().trim();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CsvStudent student)) {
			return false;
		}
        return student.getFullname().equals(getFullname());
	}

	@Override
	public String toString() {
		return "Student [name=" + getName() + ", surname=" + getSurname() + ", email=" + getEmail() + "]";
	}

}
