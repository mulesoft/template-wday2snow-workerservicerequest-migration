/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.utils.DateUtil;
import org.mule.templates.utils.Employee;

import com.mulesoft.module.batch.BatchTestHelper;
import com.workday.hr.EmployeeGetType;
import com.workday.hr.EmployeeReferenceType;
import com.workday.hr.ExternalIntegrationIDReferenceDataType;
import com.workday.hr.IDType;
import com.workday.staffing.EventClassificationSubcategoryObjectIDType;
import com.workday.staffing.EventClassificationSubcategoryObjectType;
import com.workday.staffing.TerminateEmployeeDataType;
import com.workday.staffing.TerminateEmployeeRequestType;
import com.workday.staffing.TerminateEventDataType;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Template that make calls to external systems.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {
	
	private static final String TEMPLATE_PREFIX = "wday2snow-workerservicerequest-migration";
	protected static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	protected static final int TIMEOUT_MILLIS = 600;
	private static final Logger LOGGER = LogManager.getLogger(BusinessLogicIT.class);
	private final String EMAIL = "bwillis@gmailtest.com";
	
	private static String PC_MODEL;
	private static String DESK_MODEL;
	private static String ASSIGNED_TO;	
    private static String EXT_ID;
    private static String WDAY_EXT_ID;
    private static String WDAY_TERMINATION_ID;	
    private static Date startingDate;

	private BatchTestHelper helper;
	private Employee employee;
    private List<String> snowReqIds = new ArrayList<String>();	
    
    @BeforeClass
    public static void beforeTestClass() {
    	
        final Properties props = new Properties();
    	try {
    	props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		LOGGER.error("Error occured while reading mule.test.properties" + e);
    	} 
    	
    	WDAY_TERMINATION_ID = props.getProperty("wday.termination.id");
    	WDAY_EXT_ID = props.getProperty("wday.ext.id");
    	PC_MODEL = props.getProperty("snow.pc.model");
    	ASSIGNED_TO = props.getProperty("snow.desk.assignedTo");
    	DESK_MODEL = props.getProperty("snow.desk.model");
    	EXT_ID = props.getProperty("wday.ext.id");
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.HOUR_OF_DAY, -1);
    	startingDate = cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");    	
    	System.setProperty("migration.startDate", sdf.format(new Date()));
    	System.setProperty("http.port", "9090");
    }

    @Before
    public void setUp() throws Exception {
    	helper = new BatchTestHelper(muleContext);	
    	LOGGER.info("Starting date is set to: " + startingDate);
		createTestDataInSandBox();
    }

    @After
    public void tearDown() throws MuleException, Exception {
    	deleteTestDataFromSandBox();
    }    
    
    private void createTestDataInSandBox() throws MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper hireEmployeeFlow = getSubFlow("hireEmployee");
		hireEmployeeFlow.initialise();
		LOGGER.info("Creating a workday employee...");
		try {
			hireEmployeeFlow.process(getTestEvent(prepareNewHire().get(0), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private List<Object> prepareNewHire(){
		EXT_ID = TEMPLATE_PREFIX + System.currentTimeMillis();
		LOGGER.info("Employee name: " + EXT_ID);
		employee = new Employee(EXT_ID, "Willis1", EMAIL, "650-232-2323", "999 Main St", "San Francisco", "CA", "94105", "US", "o7aHYfwG", 
				new Date(), new Date(), "QA Engineer", "San_Francisco_site", "Regular", "Full Time", "Salary", "USD", "140000", "Annual", "39905", "21440", EXT_ID);
		List<Object> list = new ArrayList<Object>();
		list.add(employee);
		return list;
	}
    
	@SuppressWarnings("unchecked")
	@Test
    public void testMainFlow() throws Exception {
		Thread.sleep(10000);
		runFlow("triggerFlow");
		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_MILLIS * 1000, 500);
		helper.assertJobWasSuccessful();	
		
		
		SubflowInterceptingChainLifecycleWrapper getSnowRequestsflow = getSubFlow("getSnowRequests");
		getSnowRequestsflow.initialise();
		
		// get requests from ServiceNow
		MuleEvent response = getSnowRequestsflow.process(getTestEvent(ASSIGNED_TO, MessageExchangePattern.REQUEST_RESPONSE));
		
		List<Map<String, String>> snowRes = (List<Map<String, String>>) response.getMessage().getPayload();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int count = 0;
		for (Map<String, String> request : snowRes){
			Date requestOpenedAtDate = sdf.parse(request.get("opened_at"));
			
			if (startingDate.before(requestOpenedAtDate)){
				count++;
				snowReqIds.add(request.get("sys_id"));
				// get request items
				List<Map<String, String>> reqItems = getReqItem(request.get("sys_id"));
				Assert.assertTrue("There should be 1 request item in request in ServiceNow and there is " + reqItems.size() + ".", reqItems.size() == 1);
				for (Map<String, String> reqItem  : reqItems){
					Assert.assertTrue("There should be correct catalogue item set.", reqItem.get("cat_item").equals(PC_MODEL) || reqItem.get("cat_item").equals(DESK_MODEL));
				}
			}
		}
		Assert.assertTrue("There should be two service requests in ServiceNow, but there are " + count + ".", count == 2);		
    }
    
    @SuppressWarnings("unchecked")
	private List<Map<String, String>> getReqItem(String parentId) throws MuleException, Exception{
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("getSnowReqItems");
		flow.initialise();		
		MuleEvent response = flow.process(getTestEvent(parentId, MessageExchangePattern.REQUEST_RESPONSE));
		List<Map<String, String>> snowRes = (List<Map<String, String>>) response.getMessage().getPayload();
		
		return snowRes;
    }
    
    private void deleteTestDataFromSandBox() throws MuleException, Exception {
    	LOGGER.info("Deleting test data...");
		
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("deleteRequests");
		flow.initialise();
		SubflowInterceptingChainLifecycleWrapper flow1 = getSubFlow("deleteReqItems");
		flow1.initialise();
		
		for (String id : snowReqIds){			
			flow1.process(getTestEvent(id, MessageExchangePattern.REQUEST_RESPONSE));			
			flow.process(getTestEvent(id, MessageExchangePattern.REQUEST_RESPONSE));					
		}
				
    	// Terminate the created users in Workday
		flow = getSubFlow("getWorkdaytoTerminateFlow");
		flow.initialise();
		
		try {
			MuleEvent response = flow.process(getTestEvent(getEmployee(), MessageExchangePattern.REQUEST_RESPONSE));			
			flow = getSubFlow("terminateWorkdayEmployee");
			flow.initialise();
			flow.process(getTestEvent(prepareTerminate(response), MessageExchangePattern.REQUEST_RESPONSE));								
		} catch (Exception e) {
			e.printStackTrace();
		}		
		LOGGER.info("Deleting test data finished...");
	}
    
    private EmployeeGetType getEmployee(){
		EmployeeGetType get = new EmployeeGetType();
		EmployeeReferenceType empRef = new EmployeeReferenceType();					
		ExternalIntegrationIDReferenceDataType value = new ExternalIntegrationIDReferenceDataType();
		IDType idType = new IDType();
		value.setID(idType);
		// use an existing external ID just for matching purpose
		idType.setSystemID(WDAY_EXT_ID);
		idType.setValue(EXT_ID);			
		empRef.setIntegrationIDReference(value);
		get.setEmployeeReference(empRef);		
		return get;
	}
	
	private TerminateEmployeeRequestType prepareTerminate(MuleEvent response) throws DatatypeConfigurationException{
		TerminateEmployeeRequestType req = (TerminateEmployeeRequestType) response.getMessage().getPayload();
		TerminateEmployeeDataType eeData = req.getTerminateEmployeeData();		
		TerminateEventDataType event = new TerminateEventDataType();
		eeData.setTerminationDate(new GregorianCalendar());
		EventClassificationSubcategoryObjectType prim = new EventClassificationSubcategoryObjectType();
		List<EventClassificationSubcategoryObjectIDType> list = new ArrayList<EventClassificationSubcategoryObjectIDType>();
		EventClassificationSubcategoryObjectIDType id = new EventClassificationSubcategoryObjectIDType();
		id.setType("WID");
		id.setValue(WDAY_TERMINATION_ID);
		list.add(id);
		prim.setID(list);
		event.setPrimaryReasonReference(prim);
		eeData.setTerminateEventData(event );
		return req;		
	}
	
}