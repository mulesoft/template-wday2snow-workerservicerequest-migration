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
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.context.notification.NotificationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.utils.Employee;

import com.mulesoft.module.batch.BatchTestHelper;
import com.servicenow.servicecatalog.screqitem.GetRecordsResponse.GetRecordsResult;
import com.servicenow.servicecatalog.screquest.GetRecordsResponse;
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

	private static String WDAY_EXT_ID;
	private static final String TEMPLATE_PREFIX = "wday2snow-worker-broadcast";
	private static final long TIMEOUT_MILLIS = 30000;
	private static final long DELAY_MILLIS = 500;
	protected static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	protected static final int TIMEOUT_SEC = 60;
	private static String PC_MODEL;
	private static String DESK_MODEL;
	private static String DESK_ASSIGNED_TO;
	private BatchTestHelper helper;
	
    private String EXT_ID, EMAIL = "bwillis@gmailtest.com";
	private Employee employee;
    private List<String> snowReqIds = new ArrayList<String>();
    private static String WDAY_TERMINATION_ID;
	
    private static Date startingDate;
    
    @BeforeClass
    public static void beforeTestClass() {
        System.setProperty("poll.startDelayMillis", "0");
        System.setProperty("poll.frequencyMillis", "60000");
        
        final Properties props = new Properties();
    	try {
    	props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    		System.out.println("Error occured while reading mule.test.properties" + e);
    	} 
    	
    	WDAY_TERMINATION_ID = props.getProperty("wday.termination.id");
    	WDAY_EXT_ID = props.getProperty("wday.ext.id");
    	DESK_ASSIGNED_TO = props.getProperty("snow.desk.assignedTo");
    	PC_MODEL = props.getProperty("snow.pc.model");
    	DESK_MODEL = props.getProperty("snow.desk.model");
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.HOUR_OF_DAY, -2);
    	startingDate = cal.getTime();
    }

    @Before
    public void setUp() throws Exception {
    	helper = new BatchTestHelper(muleContext);
		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();
		
		createTestDataInSandBox();
    }

    @After
    public void tearDown() throws MuleException, Exception {
    	deleteTestDataFromSandBox();
    }
    
    private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}
    
    private void createTestDataInSandBox() throws MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("hireEmployee");
		flow.initialise();
		logger.info("creating a workday employee...");
		try {
			flow.process(getTestEvent(prepareNewHire(), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private List<Object> prepareNewHire(){
		EXT_ID = TEMPLATE_PREFIX + System.currentTimeMillis();
		logger.info("employee name: " + EXT_ID);
		employee = new Employee(EXT_ID, "Willis1", EMAIL, "650-232-2323", "999 Main St", "San Francisco", "CA", "94105", "US", "o7aHYfwG", 
				"2014-04-17-07:00", "2014-04-21-07:00", "QA Engineer", "San_Francisco_site", "Regular", "Full Time", "Salary", "USD", "140000", "Annual", "39905", "21440", EXT_ID);
		List<Object> list = new ArrayList<Object>();
		list.add(employee);
		return list;
	}
    
	@Test
    public void testMainFlow() throws Exception {
		Thread.sleep(10000);
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();
		helper.awaitJobTermination(TIMEOUT_MILLIS, DELAY_MILLIS);
		helper.assertJobWasSuccessful();	
		
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("getSnowRequests");
		flow.initialise();
		Map<String, String> inputMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("starting date: " + sdf.format(startingDate));
		inputMap.put("assignedTo", DESK_ASSIGNED_TO);
		
		MuleEvent response = flow.process(getTestEvent(DESK_ASSIGNED_TO, MessageExchangePattern.REQUEST_RESPONSE));
		GetRecordsResponse snowRes = ((GetRecordsResponse)response.getMessage().getPayload());
		logger.info("snow requests: " + snowRes.getGetRecordsResult().size());
		
		int count = 0;
		for (com.servicenow.servicecatalog.screquest.GetRecordsResponse.GetRecordsResult item : snowRes.getGetRecordsResult()){
			if (startingDate.compareTo(sdf.parse(item.getOpenedAt())) < 0){
				count++;
				snowReqIds.add(item.getSysId());
				List<GetRecordsResult> reqItems = getReqItem(item.getSysId());
				Assert.assertTrue("There should be 1 request item in request in ServiceNow.", reqItems.size() == 1);
				for (com.servicenow.servicecatalog.screqitem.GetRecordsResponse.GetRecordsResult reqItem  : reqItems){
					Assert.assertTrue("There should be correct catalogue item set.", 
							reqItem.getCatItem().equals(PC_MODEL) || reqItem.getCatItem().equals(DESK_MODEL));
				}
			}
		}
		
		Assert.assertTrue("There should be two service requests in ServiceNow.", count == 2);
		
    }
    
    private List<GetRecordsResult> getReqItem(String parentId) throws MuleException, Exception{
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("getSnowReqItems");
		flow.initialise();		
		MuleEvent response = flow.process(getTestEvent(parentId, MessageExchangePattern.REQUEST_RESPONSE));
		com.servicenow.servicecatalog.screqitem.GetRecordsResponse snowRes = ((com.servicenow.servicecatalog.screqitem.GetRecordsResponse)response.getMessage().getPayload());
		
		return snowRes.getGetRecordsResult();
    }
    
    private void deleteTestDataFromSandBox() throws MuleException, Exception {
    	logger.info("deleting test data...");
		
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("deleteRequests");
		flow.initialise();
		SubflowInterceptingChainLifecycleWrapper flow1 = getSubFlow("deleteReqItems");
		flow1.initialise();
		
		for (String id : snowReqIds){			
			flow1.process(getTestEvent(id));			
			flow.process(getTestEvent(id));					
		}
				
    	// Delete the created users in Workday
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
		eeData.setTerminationDate(xmlDate(new Date()));
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
	
	private static XMLGregorianCalendar xmlDate(Date date) throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}
	
	
}
