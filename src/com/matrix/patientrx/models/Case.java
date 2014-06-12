package com.matrix.patientrx.models;

//{
//    "id": 10,
//    "patient_id": 2,
//    "docter_id": null,
//    "name": "Beeran",
//    "age": 22,
//    "gender": "Male",
//    "lat": 12.1,
//    "lag": 78.3,
//    "created_at": "2014-06-11T15:54:06.619Z",
//    "updated_at": "2014-06-11T15:54:06.619Z",
//    "total_case_comments": 0,
//    "total_new_case_comments_by_patient": 0,
//    "total_new_case_comments_by_doctor": 0,
//    "first_case_comment": null
//}
public class Case {
	private int id;
	private int patient_id;
	private int docter_id;
	private String name;
	private String gender;
	private String created_at;
	private String updated_at;
	private int total_case_comments;
	private int total_new_case_comments_by_patient;
	private int total_new_case_comments_by_doctor;
	private Comment first_case_comment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPatient_id() {
		return patient_id;
	}

	public void setPatient_id(int patient_id) {
		this.patient_id = patient_id;
	}

	public int getDocter_id() {
		return docter_id;
	}

	public void setDocter_id(int docter_id) {
		this.docter_id = docter_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public int getTotal_case_comments() {
		return total_case_comments;
	}

	public void setTotal_case_comments(int total_case_comments) {
		this.total_case_comments = total_case_comments;
	}

	public int getTotal_new_case_comments_by_patient() {
		return total_new_case_comments_by_patient;
	}

	public void setTotal_new_case_comments_by_patient(
			int total_new_case_comments_by_patient) {
		this.total_new_case_comments_by_patient = total_new_case_comments_by_patient;
	}

	public int getTotal_new_case_comments_by_doctor() {
		return total_new_case_comments_by_doctor;
	}

	public void setTotal_new_case_comments_by_doctor(
			int total_new_case_comments_by_doctor) {
		this.total_new_case_comments_by_doctor = total_new_case_comments_by_doctor;
	}

	public Comment getFirst_case_comment() {
		return first_case_comment;
	}

	public void setFirst_case_comment(Comment first_case_comment) {
		this.first_case_comment = first_case_comment;
	}

}
