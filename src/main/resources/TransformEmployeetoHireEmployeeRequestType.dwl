%dw 1.0
%output application/java

%function countryMapping(inputCountry) {
	workdayCountry: 'USA' when inputCountry == 'US' otherwise null
} unless inputCountry is :null otherwise workdayCountry: null

%function stateMapping(inputState) {
	workdayState: 'USA-CA' when inputState == 'CA' otherwise null
} unless inputState is :null otherwise workdayState: null

%function locationMapping(inputLocation) {
	workdayLocation: 'San_Francisco_site' when inputLocation == 'San_Francisco_site' otherwise null
} unless inputLocation is :null otherwise workdayLocation: null

%function posTimeTypeMapping(inputPosTimeType) {
	(workdayPosTimeType: 'Full_Time') when inputPosTimeType == 'Full Time',
	(workdayPosTimeType: 'Part_Time') when inputPosTimeType == 'Part Time'
} unless inputPosTimeType is :null otherwise workdayPosTimeType: null

%function payPlanMapping(payRateType) {
	(workdayPayPlan: 'SALARY_Salary_Plan') when payRateType == 'Salary',
	(workdayPayPlan: 'SALARY_Hourly_Plan') when payRateType == 'Hourly'
} unless payRateType is :null otherwise workdayPayPlan: null

%var extId = 'wday2snow-workerservicerequest-migration' ++ currentMillis()
--- 
{
	version: 'v21.1',
	businessProcessParameters : {
		autoComplete : true
	},
	hireEmployeeData : {
		applicantData: {
			externalIntegrationIDData: {
				ID: [{
					systemID	: 'Jobvite',
				    value		: extId
				    }]
			},
			personalData: {
				contactData: {
					addressData: [{
						addressLineData: [{
							type	: 'ADDRESS_LINE_1',
							value	: p('wday.addr1')

						}],
						countryReference: {
							ID: [{
								type	: 'ISO_3166-1_Alpha-3_Code',
								value	: countryMapping(p('wday.country')).workdayCountry
							}]
						},
						countryRegionReference: {
							ID: [{
								type	: 'Country_Region_ID',
								value	: stateMapping(p('wday.state')).workdayState
							}]
						},
						effectiveDate	: now as :date {class: "java.util.Calendar"},
						municipality 	: p('wday.city'),
						postalCode 		: p('wday.zip'),
						usageData: [{
							typeData:[{
								primary : true,
								typeReference: {
									ID: [{
										type	: 'Communication_Usage_Type_ID',
										value	: 'HOME'
									}]
								}
							}]
						}]
					}],
					emailAddressData 	: [{
						emailAddress	: p('wday.email'),
						usageData: [{
							public: true,
							typeData: [{
								primary: true,
								typeReference: {
									ID: [{
										type	: 'Communication_Usage_Type_ID',
										value	: 'HOME'
									}]
								}
							}]
						}]
					}],
					phoneData: [{
						internationalPhoneCode: '1',
						phoneDeviceTypeReference: {
							ID: [{
								type	: 'Phone_Device_Type_ID',
								value	: '1063.5'
							}]
						},
						phoneNumber		: p('wday.phone'),
						usageData: [{
							public: true,
							typeData: [{
								primary: true,
								typeReference: {
									ID: [{
										type	: 'Communication_Usage_Type_ID',
										value	: 'HOME'
									}]
								}
							}]
						}]
					}]
				},
				nameData: {
					legalNameData: {
						nameDetailData: {
							countryReference: {
								ID: [{
									type	: 'ISO_3166-1_Alpha-3_Code',
									value	: countryMapping(p('wday.country')).workdayCountry
								}]
							},
							firstName	: extId,
							lastName	: p('wday.familyName')
						}
					}
				}
			}
		},	
			hireDate: now as :date {class: "java.util.Calendar"},
			hireEmployeeEventData: {
				employeeExternalIDData: {
					externalID: [{
						externalID: extId,
						systemID: p('wday.ext.id')
					}]
				},
				employeeTypeReference: {
			        ID: [{
			            type: 'Employee_Type_ID',
			            value: 'Regular'
			        }]
			    },
			    firstDayOfWork: now as :date {class: "java.util.Calendar"},
			    hireReasonReference: {
			    	ID: [{
			    		type	: 'General_Event_Subcategory_ID',
			    		value	: 'Hire_Employee_New_Hire_Fill_Vacancy'
			    	}]
			    },
			    positionDetails: {
			    	positionTitle: p('wday.title'),
			    	defaultHours: 40,
			    	scheduledHours: 40,
			    	jobProfileReference: {
			    		ID: [{
			    			type: 'Job_Profile_ID',
			    			value: p('wday.jobProfile')
			    		}]
			    	},
			    	locationReference: {
			    		ID: [{
			    			type: 'Location_ID',
			    			value: locationMapping(p('wday.location')).workdayLocation
			    		}]
			    	},
			    	payRateTypeReference : {
			    		ID : [{
			    			type: 'Pay_Rate_Type_ID',
			    			value: p('wday.payRateType')
			    		}]
			    	},
			    	positionTimeTypeReference : {
			    		ID : [{
			    			type: 'Position_Time_Type_ID',
			    			value: posTimeTypeMapping(p('wday.timeType')).workdayPosTimeType
			    		}]
			    	}
			    }
			},
			organizationReference: {
				ID: [{
					type: 'Organization_Reference_ID',
					value: '50006855'
				}]
			},
			proposeCompensationForHireSubProcess: {
				businessSubProcessParameters : {
					autoComplete : true
				},
				proposeCompensationForHireData : {
					compensationGuidelinesData : {
						compensationGradeReference : {
							ID: [{
								type: 'Compensation_Grade_ID',
								value: 'Non_Management'
							}]
						},
						compensationPackageReference : {
							ID : [{
								type : 'Compensation_Package_ID',
								value: 'Non_Management_Compensation_Package'
							}]
						}
					},
				payPlanData: {
					payPlanSubData: [{
						amount: p('wday.basePay'),
						currencyReference : {
							ID : [{
								type: 'Currency_ID',
								value: p('wday.basePayCurrency')
							}]
						},
						frequencyReference : {
							ID: [{
								type : 'Frequency_ID',
								value : p('wday.basePayFreq')
							}]
						},
						payPlanReference : {
							ID : [{
								type : 'Compensation_Plan_ID',
								value: payPlanMapping(p('wday.payRateType')).workdayPayPlan
							}]
						}
					}],
					'replace' : false
				},
				primaryCompensationBasis : p('wday.basePay')
				}
			}
		}
	
	
} as :object {class: "com.workday.staffing.HireEmployeeRequestType"}