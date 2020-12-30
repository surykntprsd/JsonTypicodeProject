package com.typicode.jsonph.utilities;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Listeners extends TestListenerAdapter
{
	
	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;
	
	public void onStart(ITestContext testContext)
	{
		//specify location of the report
		htmlReporter=new ExtentHtmlReporter(System.getProperty("user.dir")+ "/Reports/testReport.html");
				
		htmlReporter.config().setDocumentTitle("Test Automation Report"); // Tile of report
		htmlReporter.config().setReportName("Json Typicode Test Report"); // name of the report
		htmlReporter.config().setTheme(Theme.STANDARD);
		
		extent=new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Project Name","Json Typicode");
		extent.setSystemInfo("Host name","jsonplaceholder");
		extent.setSystemInfo("Environemnt","QA");
		extent.setSystemInfo("User","Suryakant");
			
	}
	
	public void onTestSuccess(ITestResult result)
	{

		test=extent.createTest(result.getName()); // create new entry in the report
				
		test.log(Status.PASS, "Test Case PASSED IS " + result.getName());
	}
	
	public void onTestFailure(ITestResult result)
	{
		test=extent.createTest(result.getName()); // create new entry in the report
		
		test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getName()); // to add name in extent report
		test.log(Status.FAIL, "TEST CASE FAILED IS " + result.getThrowable()); // to add error/exception in extent report
	
	}
	
	public void onTestSkipped(ITestResult result)
	{
		test=extent.createTest(result.getName()); // create new entry in the report
		test.log(Status.SKIP, "Test Case SKIPPED IS " + result.getName());
	}
	
	public void onFinish(ITestContext testContext)
	{
		extent.flush();
	}
	
	}
