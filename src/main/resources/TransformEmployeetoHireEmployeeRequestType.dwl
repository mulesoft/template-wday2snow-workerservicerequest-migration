%dw 1.0
%input payload application/java
%output application/java
%var employee = payload as :object {class: "com.mule.templates.utils.Employee"}

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
--- 
{
	version: 'v20',
	businessProcessParameters : {
		autoComplete : true
	},
	hireEmployeeData : {
		applicantData: {
			externalIntegrationIDData: {
				ID: [{
					systemID	: 'Jobvite',
				    value		: employee.id
				    }]
			},
			personalData: {
				contactData: {
					addressData: [{
						addressLineData: [{
							type	: 'ADDRESS_LINE_1',
							value	: employee.addr1

						}],
						countryReference: {
							ID: [{
								type	: 'ISO_3166-1_Alpha-3_Code',
								value	: countryMapping(employee.country).workdayCountry
							}]
						},
						countryRegionReference: {
							ID: [{
								type	: 'Country_Region_ID',
								value	: stateMapping(employee.state).workdayState
							}]
						},
						effectiveDate	: employee.hireDate as :date,
						municipality 	: employee.city,
						postalCode 		: employee.zip,
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
						emailAddress	: employee.email,
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
						phoneNumber		: employee.phone,
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
									value	: countryMapping(employee.country).workdayCountry
								}]
							},
							firstName	: employee.givenName,
							lastName	: employee.familyName
						}
					}
				}
			}
		},	
			hireDate: employee.hireDate as :date,
			hireEmployeeEventData: {
				employeeExternalIDData: {
					externalID: [{
						externalID: employee.id,
						systemID: 'Salesforce - Chatter'
					}]
				},
				employeeTypeReference: {
			        ID: [{
			            type: 'Employee_Type_ID',
			            value: 'Regular'
			        }]
			    },
			    firstDayOfWork: employee.startDate as :date,
			    hireReasonReference: {
			    	ID: [{
			    		type	: 'General_Event_Subcategory_ID',
			    		value	: 'Hire_Employee_New_Hire_Fill_Vacancy'
			    	}]
			    },
			    positionDetails: {
			    	positionTitle: employee.title,
			    	defaultHours: 40,
			    	scheduledHours: 40,
			    	jobProfileReference: {
			    		ID: [{
			    			type: 'Job_Profile_ID',
			    			value: employee.jobProfile
			    		}]
			    	},
			    	locationReference: {
			    		ID: [{
			    			type: 'Location_ID',
			    			value: locationMapping(employee.location).workdayLocation
			    		}]
			    	},
			    	payRateTypeReference : {
			    		ID : [{
			    			type: 'Pay_Rate_Type_ID',
			    			value: employee.payRateType
			    		}]
			    	},
			    	positionTimeTypeReference : {
			    		ID : [{
			    			type: 'Position_Time_Type_ID',
			    			value: posTimeTypeMapping(employee.timeType).workdayPosTimeType
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
						amount: employee.basePay,
						currencyReference : {
							ID : [{
								type: 'Currency_ID',
								value: employee.basePayCurrency
							}]
						},
						frequencyReference : {
							ID: [{
								type : 'Frequency_ID',
								value : employee.basePayFreq
							}]
						},
						payPlanReference : {
							ID : [{
								type : 'Compensation_Plan_ID',
								value: payPlanMapping(employee.payRateType).workdayPayPlan
							}]
						}
					}],
					'replace' : false
				},
				primaryCompensationBasis : employee.basePay
				}
			}
		}
	
	
} as :object {class: "com.workday.staffing.HireEmployeeRequestType"}