/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.workday.hr.WorkerOrganizationMembershipDataType;
import com.workday.hr.WorkerType;

public class DepartmentProcessor implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		WorkerType worker = ((WorkerType) eventContext.getMessage().getPayload());
		if (worker.getWorkerData().getOrganizationData() != null)
			for (WorkerOrganizationMembershipDataType role : worker.getWorkerData().getOrganizationData().getWorkerOrganizationData()){
				if (role.getOrganizationData().getOrganizationName().toLowerCase().contains("sales")) {
					return true;
				}
			}
		return false;
	}

}
