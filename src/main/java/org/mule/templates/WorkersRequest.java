/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.workday.hr.EffectiveAndUpdatedDateTimeDataType;
import com.workday.hr.GetWorkersRequestType;
import com.workday.hr.TransactionLogCriteriaType;
import com.workday.hr.WorkerRequestCriteriaType;
import com.workday.hr.WorkerResponseGroupType;


public class WorkersRequest {

	public static GetWorkersRequestType create(String startDate) throws ParseException, DatatypeConfigurationException {
		
		GetWorkersRequestType getWorkersType = new GetWorkersRequestType();
				
		EffectiveAndUpdatedDateTimeDataType dateRangeData = new EffectiveAndUpdatedDateTimeDataType();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -3);
		dateRangeData.setUpdatedThrough(xmlDate(cal.getTime()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		dateRangeData.setUpdatedFrom(xmlDate(sdf.parse(startDate)));
		
		WorkerRequestCriteriaType crit = new WorkerRequestCriteriaType();
		List<TransactionLogCriteriaType> transactionLogCriteriaData = new ArrayList<TransactionLogCriteriaType>();
		TransactionLogCriteriaType log = new TransactionLogCriteriaType();
		log.setTransactionDateRangeData(dateRangeData);
		
		transactionLogCriteriaData.add(log);
		crit.setTransactionLogCriteriaData(transactionLogCriteriaData );
		getWorkersType.setRequestCriteria(crit);
		
		WorkerResponseGroupType resGroup = new WorkerResponseGroupType();
		resGroup.setIncludeRoles(true);	
		resGroup.setIncludePersonalInformation(true);
		resGroup.setIncludeOrganizations(true);
		resGroup.setIncludeEmploymentInformation(true);	
		resGroup.setIncludeUserAccount(true);
		resGroup.setIncludeTransactionLogData(true);
		resGroup.setIncludeManagementChainData(true);
		resGroup.setIncludeReference(true);
		
		getWorkersType.setResponseGroup(resGroup);
		
		return getWorkersType;
	}
	
	private static XMLGregorianCalendar xmlDate(Date date) throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}
		
}
