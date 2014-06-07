package com.matrix.patientrx.models;

//{
//"patient": {
//"id": 2,
//"created_at": "2014-06-05T20:15:14.216Z",
//"updated_at": "2014-06-06T18:01:24.032Z",
//"email": "123jickson123@gmail.com",
//"name": "jickson P"
//},
//"is_new_patient": false
//}

public class LoginResponse {
	public class Patient {
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getCreated_at() {
			return created_at;
		}

		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}

		public String getUpdated_at() {
			return updated_at;
		}

		public void setUpdated_at(String updated_at) {
			this.updated_at = updated_at;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private String created_at;
		private String updated_at;
		private String email;
		private String name;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Boolean getIs_new_patient() {
		return is_new_patient;
	}

	public void setIs_new_patient(Boolean is_new_patient) {
		this.is_new_patient = is_new_patient;
	}

	private Patient patient;
	private Boolean is_new_patient;
}
