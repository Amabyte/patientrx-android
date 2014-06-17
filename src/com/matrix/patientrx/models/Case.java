package com.matrix.patientrx.models;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Case implements Parcelable {
	private int id;
	private int patient_id;
	private int docter_id;
	private int age;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Case() {

	}

	public Case(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		id = in.readInt();
		patient_id = in.readInt();
		docter_id = in.readInt();
		age = in.readInt();
		name = in.readString();
		gender = in.readString();
		created_at = in.readString();
		updated_at = in.readString();
		total_case_comments = in.readInt();
		total_new_case_comments_by_patient = in.readInt();
		total_new_case_comments_by_doctor = in.readInt();
		first_case_comment = (Comment) in.readParcelable(Comment.class
				.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(patient_id);
		dest.writeInt(docter_id);
		dest.writeInt(age);
		dest.writeString(name);
		dest.writeString(gender);
		dest.writeString(created_at);
		dest.writeString(updated_at);
		dest.writeInt(total_case_comments);
		dest.writeInt(total_new_case_comments_by_patient);
		dest.writeInt(total_new_case_comments_by_doctor);
		dest.writeParcelable(first_case_comment, flags);
	}

	public static final Parcelable.Creator<Case> CREATOR = new Parcelable.Creator<Case>() {

		public Case createFromParcel(Parcel in) {
			return new Case(in);
		}

		public Case[] newArray(int size) {
			return new Case[size];
		}

	};

}
