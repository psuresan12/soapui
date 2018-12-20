import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.eviware.soapui.impl.rest.mock.RestMockResult;
import com.eviware.soapui.impl.rest.mock.RestMockService;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.junit.Testcase;
import com.eviware.soapui.model.testsuite.*;
import com.eviware.soapui.report.JUnitReport;
import groovy.json.JsonSlurper;
import jdk.nashorn.internal.parser.JSONParser;
import net.sf.json.JSONObject;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.eviware.soapui.model.testsuite.TestRunner.Status.FAILED;

public class Main {

    public static void main(String[] args) throws Exception {
        //Initialize testStep info
        Enum testStepStatus = null;
        String testStepDetail = null;

        //Initializing Extent Reports
        ExtentReports extentReports = null;
        ExtentTest extentTest = null;
        extentReports = new ExtentReports ();
        extentReports.attachReporter(new ExtentHtmlReporter("C:\\Users\\prataps\\Downloads\\"+"testSuite-extentReports-testreport.html"));
      
       //Load the SOAPUI Project with its path
        WsdlProject soapUIProject = new WsdlProject("C:\\Users\\prataps\\Downloads\\REST-Project-2-soapui-project.xml");

        //Run the Mockservice with its name
        String mockServiceName = "RESTMockService2";
        RestMockService mockService = soapUIProject.getRestMockServiceByName(mockServiceName);
        mockService.start();

        //Load the TestSuite by Name
        String testSuiteName = "Regression";
        TestSuite testSuite = soapUIProject.getTestSuiteByName(testSuiteName);
      
        //Initilalizing TestcaseRunner to run Test case
        TestCaseRunner testCaseRunner = null;

       //Loop through all the Test cases in a suite & run.
        for (TestCase testCase:
             testSuite.getTestCaseList()) {
            testCaseRunner = testCase.run(null,false);

            //Get the result of a testcase
            System.out.println("Result of testcase "+testCase.getName()+" "+testCaseRunner.getStatus()+" with reason "+testCaseRunner.getReason());

            //Creating report for each test case with name - testsuiteName-testCaseName
            extentTest = extentReports.createTest(testSuite.getName()+"-"+testCase.getName());

           //Update Report with testcase and its status. This is the first line in the Extent report
         /* if(testCaseRunner.getStatus()!=TestCaseRunner.Status.FAILED)
              extentTest.pass("PASSED");
          else
              extentTest.fail(testCaseRunner.getReason());*/

          //Update Report with teststep and its status.
            for (TestStepResult testStepResult:
                    testCaseRunner.getResults()) {
               if(testStepResult.getStatus()!=TestStepResult.TestStepStatus.FAILED){
                   testStepStatus = Status.PASS;
                   testStepDetail = "TEST STEP:"+testStepResult.getTestStep().getName();
               }
               else{
                   testStepStatus = Status.FAIL;
                   testStepDetail = testStepResult.getError()==null? "TEST STEP:"+testStepResult.getTestStep().getName():
                           "TEST STEP:"+testStepResult.getTestStep().getName()+" ERROR:"+testStepResult.getError();
               }
               //casting to Extent Report Status
                extentTest.log((Status) testStepStatus,testStepDetail);
            }

            //Flushing the report
            extentReports.flush();
        }

       /* //Junit Report
       *//* JUnitReport report = new JUnitReport();
        report.setTestSuiteName("Regression");
        report.addTestCase(testSuite.getTestCaseList().get(0).getName(),1369627187,null);
        report.save(new File("C:\\Users\\prataps\\Downloads\\MACG\\"+"testSuite-testreport.xml"));
        report.finishReport();*/


        mockService = null;

    }
}
