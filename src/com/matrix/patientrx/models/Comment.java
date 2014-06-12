package com.matrix.patientrx.models;

//{
//    "id": 2,
//    "case_id": 3,
//    "who": "patient",
//    "message": "its a testing message",
//    "is_new": false,
//    "created_at": "2014-06-09T13:37:25.109Z",
//    "updated_at": "2014-06-09T13:37:25.109Z",
//    "image_url": "https://patientrx-dev.s3-ap-southeast-1.amazonaws.com/images/Untitled.png?AWSAccessKeyId=AKIAJPWHVTXV6URSFHBQ&Expires=1402338208&Signature=rfHfXKBzzY4teCPKxh8QCReBq54%3D",
//    "audio_url": "https://patientrx-dev.s3-ap-southeast-1.amazonaws.com/audios/?AWSAccessKeyId=AKIAJPWHVTXV6URSFHBQ&Expires=1402338208&Signature=aoQhr%2B9Lg2aifncvXlZANncuG5E%3D"
//}
public class Comment {
	private int id;
	private int case_id;
	private String who;
	private String message;
	private Boolean is_new;
	private String created_at;
	private String updated_at;
	private String image_url;
	private String audio_url;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCase_id() {
		return case_id;
	}

	public void setCase_id(int case_id) {
		this.case_id = case_id;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIs_new() {
		return is_new;
	}

	public void setIs_new(Boolean is_new) {
		this.is_new = is_new;
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

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getAudio_url() {
		return audio_url;
	}

	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}

}
