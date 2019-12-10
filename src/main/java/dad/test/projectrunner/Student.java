package dad.test.projectrunner;

public class Student {
	private String name;
	private String surname;
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
		return getName() + " " + getSurname();
	}

	@Override
	public boolean equals(Object obj) {
		Student student = (Student) obj;
		return student.getFullname().equals(getFullname());
	}

	@Override
	public String toString() {
		return "Student [name=" + name + ", surname=" + surname + ", email=" + email + "]";
	}

}
