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
		logger.info("Call the GET Method of request with uri " + uri);
		response = httpRequest.request(Method.GET, uri);
		
		Thread.sleep(2000);   // Can be removed, kept for stability

	}
	
	@Test
	void checkWorkFlow() throws InterruptedException
	{
		logger.info("Checking status of the response started");
		statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 200, "Status Code is not 200");
        logger.info("Checking status of the response completed");
        checkForUser(name);
	}
	
	
	void checkForUser(String userName) throws InterruptedException
	{	
		logger.info("Checking for user '" + userName + "' started");
		String responseBody = response.getBody().asString();
		Assert.assertEquals(responseBody.contains(userName) , true , "Response doesnot contain user: "+userName);
		logger.info("Checking for user completed");
		if(responseBody.contains(userName)) {
			checkForUserId(userName);
		}
	}
	
	void checkForUserId(String userName) throws InterruptedException
	{	
		logger.info("Checking for userId with user '" + userName + "' started");
		String parameter = users + "?username=" + userName;
		requestCall(parameter);
		String responseBody = response.getBody().asString();
		JSONArray arr= JsonPath.read(responseBody, "$.*.id");
		logger.info("Checking for userId with user completed");
		String userId = arr.get(0).toString();
		logger.info("UserId for user '" + userName + "' is " + userId);
		searchForPost(userId);
		
		
	}
	
	void searchForPost(String userId) throws InterruptedException
	{	
		logger.info("Searching for post with userId " + userId + " started");
		String parameter = posts + "?userId=" + userId;
		requestCall(parameter);
		String responseBody = response.getBody().asString();
		JSONArray arr= JsonPath.read(responseBody, "$.*.id");
		logger.info("Searching for post completed");
		searchForComments(arr);
		
	}
	
	void searchForComments(JSONArray arr) throws InterruptedException
	{	
		logger.info("Searching for comments started");
		String parameter = comments + "?";
		for(int i=0;i<arr.size(); i++) {
			parameter += "postId=" + arr.get(i).toString();
			if(i != arr.size()-1) 
				parameter += "&";
		}
		requestCall(parameter);
		String responseBody = response.getBody().asString();
		logger.info("Searching for comments completed");
		arr= JsonPath.read(responseBody, "$.*.email");
		validateEmail(arr);		
		
	}
	
	void validateEmail(JSONArray arr) throws InterruptedException
	{
		logger.info("Verification of email started");
		for(int i=0;i<arr.size(); i++) {
			Assert.assertEquals(EmailValidator.getInstance().isValid(arr.get(i).toString()) , true , "Email : "+ arr.get(i).toString() + "is not valid");
		}
		logger.info("Verification of email completed");
	}
	
	
	@AfterClass
	void tearDown()
	{
		logger.info("********* Finished TC001_Validate_Workflow **********");
	}

}
