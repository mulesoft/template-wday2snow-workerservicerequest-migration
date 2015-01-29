/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.workday.hr.TransactionLogEntryType;
import com.workday.hr.WorkerType;

public class TransactionsProcessor implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		WorkerType workerData = (WorkerType) eventContext.getMessage().getPayload();
		boolean wasHired = false, wasTerminated = false;
		if (workerData.getWorkerData() != null){			
			for (TransactionLogEntryType log : workerData.getWorkerData().getTransactionLogEntryData().getTransactionLogEntry()){
				if (log.getTransactionLogData().getTransactionLogDescription().startsWith("Hire:")){
					wasHired = true;
				}
				if (log.getTransactionLogData().getTransactionLogDescription().startsWith("Terminate:")){
					wasTerminated = true;
				}
			}
		}
		return wasTerminated ? false : wasHired;
	}

}
