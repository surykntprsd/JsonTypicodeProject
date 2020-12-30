package com.typicode.jsonph.testCases;

import org.testng.Assert;
import org.testng.annotations.*;

import com.typicode.jsonph.base.TestBase;

import com.jayway.jsonpath.*;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;
import org.apache.commons.validator.routines.EmailValidator;

public class TC001_Validate_Workflow extends TestBase{
	
	RequestSpecification httpRequest;
	Response response;
	int statusCode;
	int userId;
	String name = "Delphine";
	String users = "/users";
	String posts = "/posts";
	String comments = "/comments";
	
			
	
	@BeforeClass
	void initialCall() throws InterruptedException
	{
		logger.info("********* Started TC001_Validate_Workflow  **********");
		
		requestCall(users);

	}
	
	void requestCall(String uri) throws InterruptedException
	{
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
		httpRequest = RestAssured.given();
		
		// Request headers
		httpRequest.header("Content-Type", "application/json");
		httpRequest.header("Cache-Control", "no-cache");

		// Call the GET method of request
		logger.info("Call the GET Method of request");
		response = httpRequest.request(Method.GET, uri);
		
		Thread.sleep(2000);   // Can be removed, kept for stability

	}
	
	@Test
	void checkWorkFlow() throws InterruptedException
	{
		logger.info("Checking status of the response");
		statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Status Code is not 200");
        checkForUser(name);
	}
	
	
	void checkForUser(String userName) throws InterruptedException
	{	
		String responseBody = response.getBody().asString();
		Assert.assertEquals(responseBody.contains(userName) , true , "Response doesnot contain user: "+userName);
		if(responseBody.contains(userName)) {
			checkForUserId(userName);
		}
	}
	
	void checkForUserId(String userName) throws InterruptedException
	{	
		String parameter = users + "?username=" + userName;
		requestCall(parameter);
		String responseBody = response.getBody().asString();
		JSONArray arr= JsonPath.read(responseBody, "$.*.id");
		String userId = arr.get(0).toString();
		searchForPost(userId);
		
		
	}
	
	void searchForPost(String userId) throws InterruptedException
	{	
		String parameter = posts + "?userId=" + userId;
		requestCall(parameter);
		String responseBody = response.getBody().asString();
		JSONArray arr= JsonPath.read(responseBody, "$.*.id");
		parameter = comments + "?";
		for(int i=0;i<arr.size(); i++) {
			parameter += "postId=" + arr.get(i).toString();
			if(i != arr.size()-1) 
				parameter += "&";
		}
		requestCall(parameter);
		responseBody = response.getBody().asString();
		arr= JsonPath.read(responseBody, "$.*.email");
		for(int i=0;i<arr.size(); i++) {
			Assert.assertEquals(EmailValidator.getInstance().isValid(arr.get(i).toString()) , true);
		}
		
	}
	
	
	@AfterClass
	void tearDown()
	{
		logger.info("********* Finished TC001_Validate_Workflow **********");
	}

}
