package com.capstone.ShoppingFeedback.model;

//POJO class for Response
public class Response {
	private int response;

	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Response(int response) {
		super();
		this.response = response;
	}

	public int getResponse() {
		return response;
	}

	public void setResponse(int response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "Response [response=" + response + "]";
	}
}
