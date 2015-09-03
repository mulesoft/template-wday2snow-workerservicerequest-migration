/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.workday.hr.EmailAddressInformationDataType;
import com.workday.hr.EventTargetTransactionLogEntryDataType;
import com.workday.hr.TransactionLogEntryType;
import com.workday.hr.WorkerType;

/**
 * The filter that's removing records from the payload with the same email
 * address.
 * 
 */
public class WorkersDeduplicationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkersDeduplicationFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean accept(MuleMessage message) {
		
		if (message.getPayload() instanceof NullPayload) {
			message.setPayload(new ArrayList<WorkerType>());
			return false;
		}
		
		final List<WorkerType> payload = (List<WorkerType>) message.getPayload();
		final Set<String> emails = new HashSet<String>();
		final Iterator<WorkerType> iterator = payload.iterator();
		LOGGER.info("Total records:" + payload.size());

		while (iterator.hasNext()) {
			final WorkerType next = iterator.next();
			final EventTargetTransactionLogEntryDataType log = next.getWorkerData().getTransactionLogEntryData();

			if (log != null) {
				boolean wasTerminated = false;
				for (TransactionLogEntryType entry : log.getTransactionLogEntry()) {
					if (entry.getTransactionLogData().getTransactionLogDescription().startsWith("Terminate:")) {
						iterator.remove();
						wasTerminated = true;
						break;
					}
				}
				if (wasTerminated) {
					continue;
				}
			}

			final List<EmailAddressInformationDataType> emailAddressData = next.getWorkerData().getPersonalData().getContactData().getEmailAddressData();
			
			if (emailAddressData.isEmpty()) {
				iterator.remove();
				continue;
			}
			
			final String email = emailAddressData.get(0).getEmailAddress();
			
			if (!emails.add(email)) {
				iterator.remove();
			}
		}

		LOGGER.info("Unique emails:" + emails.size());
		LOGGER.info("List of workers:");
		for (WorkerType workerType: payload) {
			LOGGER.info(workerType.getWorkerData().getPersonalData().getNameData().getPreferredNameData().getNameDetailData().getFormattedName());
		}
		return true;
	}
}