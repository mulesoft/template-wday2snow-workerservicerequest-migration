/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workday.hr.EventTargetTransactionLogEntryDataType;
import com.workday.hr.TransactionLogEntryType;
import com.workday.hr.WorkerType;

/**
 * The filter that's removing records from the payload with the same email
 * address.
 * 
 */
public class WorkersDeduplicationFilter implements Filter {

	Logger logger = LoggerFactory.getLogger(WorkersDeduplicationFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean accept(MuleMessage message) {
		if (message.getPayload() instanceof NullPayload)
			return false;

		List<WorkerType> payload = (List<WorkerType>) message.getPayload();
		List<String> emails = new ArrayList<String>();
		Iterator<WorkerType> iterator = payload.iterator();
		logger.info("total records:" + payload.size());

		while (iterator.hasNext()) {
			WorkerType next = iterator.next();
			EventTargetTransactionLogEntryDataType log = next.getWorkerData()
					.getTransactionLogEntryData();

			if (log != null) {
				boolean was = false;
				for (TransactionLogEntryType entry : log
						.getTransactionLogEntry()) {
					if (entry.getTransactionLogData().getTransactionLogDescription().startsWith("Terminate:")) {
						iterator.remove();
						was = true;
						break;
					}
				}
				if (was) {
					continue;
				}
			}

			if (next.getWorkerData().getPersonalData().getContactData()
					.getEmailAddressData().isEmpty()) {
				iterator.remove();
				continue;
			}
			final String email = next.getWorkerData().getPersonalData()
					.getContactData().getEmailAddressData().get(0).getEmailAddress();
			if (emails.contains(email)) {
				iterator.remove();
			} else {
				emails.add(email);
			}
		}

		logger.info("unique emails:" + emails.size());
		logger.info("employed workers:" + payload.size());
		return true;
	}
}