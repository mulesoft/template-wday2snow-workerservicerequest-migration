
# Anypoint Template: Workday Worker to ServiceNow Request Migration

+ [License Agreement](#licenseagreement)
+ [Use Case](#usecase)
+ [Considerations](#considerations)
	* [ServiceNow Considerations](#servicenowconsiderations)
	* [Workday Considerations](#workdayconsiderations)
+ [Run it!](#runit)
	* [Running on premise](#runonopremise)
	* [Running on Studio](#runonstudio)
	* [Running on Mule ESB stand alone](#runonmuleesbstandalone)
	* [Running on CloudHub](#runoncloudhub)
	* [Deploying your Anypoint Template on CloudHub](#deployingyouranypointtemplateoncloudhub)
	* [Properties to be configured (With examples)](#propertiestobeconfigured)
+ [API Calls](#apicalls)
+ [Customize It!](#customizeit)
	* [config.xml](#configxml)
	* [businessLogic.xml](#businesslogicxml)
	* [endpoints.xml](#endpointsxml)
	* [errorHandling.xml](#errorhandlingxml)


# License Agreement <a name="licenseagreement"/>
Note that using this template is subject to the conditions of this [License Agreement](AnypointTemplateLicense.pdf).
Please review the terms of the license before downloading and using this template. In short, you are allowed to use the template for free with Mule ESB Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.

# Use Case <a name="usecase"/>
As a Workday admin I want to create 2 service requests in ServiceNow for every new unique worker from Workday
			
1. one service request for setting up a desk
2. one service request for setting up a computer	

As implemented, this Anypoint Template leverages the [Batch Module](http://www.mulesoft.org/documentation/display/current/Batch+Processing).
The batch job is divided into Input, Process and On Complete stages.
During the Input stage the Anypoint Template will query Workday for all the existing active workers that match the filtering criteria. The criteria is based on manipulations starting from the given date.
The Process stage will create new service request assigned to the concrete item - desk or computer in Service Now for each worker.
Finally, during the On Complete stage the Anypoint Template will print output statistics data into the console and send a notification email with the result of the batch execution.

# Considerations <a name="considerations"/>

There are a couple of things you should take into account before running this Anypoint Template:

1. **Workday email uniqueness**: The email can be repeated for two or more accounts (or missing). Therefore Workday accounts with duplicate emails or without email will be removed from processing in the Input stage.
2. **Workday termination status**: The worker in Workday can be terminated. If worker is terminated, he will be removed from processing in Input stage too.





## ServiceNow Considerations <a name="servicenowconsiderations"/>

There may be a few things that you need to know regarding ServiceNow, in order for this template to work.


### As destination of data

There are no particular considerations for this Anypoint Template regarding ServiceNow as data destination.
## Workday Considerations <a name="workdayconsiderations"/>

### As source of data

There are no particular considerations for this Anypoint Template regarding Workday as data origin.






# Run it! <a name="runit"/>
Simple steps to get Workday Worker to ServiceNow Request Migration running.
In any of the ways you would like to run this Anypoint Template this is an example of the output you'll see after hitting the HTTP endpoint:

<pre>
<h1>Batch Process initiated</h1>
<b>ID:</b>6eea3cc6-7c96-11e3-9a65-55f9f3ae584e<br/>
<b>Records to Be Processed: </b>9<br/>
<b>Start execution on: </b>Mon Sep 03 18:05:33 GMT-03:00 2015
</pre>

## Running on premise <a name="runonopremise"/>
In this section we detail the way you should run your Anypoint Template on your computer.


### Where to Download Mule Studio and Mule ESB
First thing to know if you are a newcomer to Mule is where to get the tools.

+ You can download Mule Studio from this [Location](http://www.mulesoft.com/platform/mule-studio)
+ You can download Mule ESB from this [Location](http://www.mulesoft.com/platform/soa/mule-esb-open-source-esb)


### Importing an Anypoint Template into Studio
Mule Studio offers several ways to import a project into the workspace, for instance: 

+ Anypoint Studio generated Deployable Archive (.zip)
+ Anypoint Studio Project from External Location
+ Maven-based Mule Project from pom.xml
+ Mule ESB Configuration XML from External Location

You can find a detailed description on how to do so in this [Documentation Page](http://www.mulesoft.org/documentation/display/current/Importing+and+Exporting+in+Studio).


### Running on Studio <a name="runonstudio"/>
Once you have imported you Anypoint Template into Anypoint Studio you need to follow these steps to run it:

+ Locate the properties file `mule.dev.properties`, in src/main/resources
+ Complete all the properties required as per the examples in the section [Properties to be configured](#propertiestobeconfigured)
+ Once that is done, right click on you Anypoint Template project folder 
+ Hover you mouse over `"Run as"`
+ Click on  `"Mule Application"`


### Running on Mule ESB stand alone <a name="runonmuleesbstandalone"/>
Complete all properties in one of the property files, for example in [mule.prod.properties] (../master/src/main/resources/mule.prod.properties) and run your app with the corresponding environment variable to use it. To follow the example, this will be `mule.env=prod`. 
After this, to trigger the use case you just need to hit the local HTTP endpoint with the port you configured in your configuration file. If this is, for instance, `9090` then you should hit: `http://localhost:9090/migrateworkers` and this will output a summary report and send it in the email.

## Running on CloudHub <a name="runoncloudhub"/>
While [creating your application on CloudHub](http://www.mulesoft.org/documentation/display/current/Hello+World+on+CloudHub) (Or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to be configured** as well as the **mule.env**.
Once your app is all set and started, supposing you choose as domain name `wdayworkermigration` to trigger the use case you just need to hit `http://wdayworkermigration.cloudhub.io/migrateworkers` and report will be sent to the email configured.

### Deploying your Anypoint Template on CloudHub <a name="deployingyouranypointtemplateoncloudhub"/>
Mule Studio provides you with really easy way to deploy your Template directly to CloudHub, for the specific steps to do so please check this [link](http://www.mulesoft.org/documentation/display/current/Deploying+Mule+Applications#DeployingMuleApplications-DeploytoCloudHub)


## Properties to be configured (With examples) <a name="propertiestobeconfigured"/>
In order to use this Mule Anypoint Template you need to configure properties (Credentials, configurations, etc.) either in properties file or in CloudHub as Environment Variables. Detail list with examples:
### Application configuration
### Application configuration
+ http.port `9090`
+ migration.startDate `"2015-09-03T09:10:00.000+0200"`

#### Workday Connector configuration
+ wday.user `admin@workday`
+ wday.password `secret`
+ wday.endpoint `https://impl-cc.workday.com/ccx/service/workday/Human_Resources/v21.1`

#### ServiceNow Connector 
+ snow.user `snow_user1`
+ snow.password `ExamplePassword881`
+ snow.endpoint `https://instance.service-now.com`

#### ServiceNow Items configuration
+ snow.pc.assignedTo `sysId_of_the_ServiceNow_User`
+ snow.pc.model `sysId_of_the_ServiceNow_Item1`
+ snow.desk.assignedTo `sysId_of_the_ServiceNow_User`
+ snow.desk.model `sysId_of_the_ServiceNow_Item2`
+ snow.location `sysId_of_the_ServiceNow_Location`

+ snow.pc.deliveryDays `5`
+ snow.pc.price `3000`

+ snow.desk.deliveryDays `3`
+ snow.desk.price `500`

#### SMTP Services configuration
+ smtp.host `smtp.gmail.com`
+ smtp.port `587`
+ smtp.user `sender%40gmail.com`
+ smtp.password `secret`

#### Mail details
+ mail.from `users.report%40mulesoft.com`
+ mail.to `user@mulesoft.com`
+ mail.subject `Workers Migration Report`

# API Calls <a name="apicalls"/>
There are no special considerations regarding API calls.


# Customize It!<a name="customizeit"/>
This brief guide intends to give a high level idea of how this Anypoint Template is built and how you can change it according to your needs.
As mule applications are based on XML files, this page will be organized by describing all the XML that conform the Anypoint Template.
Of course more files will be found such as Test Classes and [Mule Application Files](http://www.mulesoft.org/documentation/display/current/Application+Format), but to keep it simple we will focus on the XMLs.

Here is a list of the main XML files you'll find in this application:

* [config.xml](#configxml)
* [endpoints.xml](#endpointsxml)
* [businessLogic.xml](#businesslogicxml)
* [errorHandling.xml](#errorhandlingxml)


## config.xml<a name="configxml"/>
Configuration for Connectors and [Properties Place Holders](http://www.mulesoft.org/documentation/display/current/Configuring+Properties) are set in this file. **Even you can change the configuration here, all parameters that can be modified here are in properties file, and this is the recommended place to do it so.** Of course if you want to do core changes to the logic you will probably need to modify this file.

In the visual editor they can be found on the *Global Element* tab.


## businessLogic.xml<a name="businesslogicxml"/>
Functional aspect of the Anypoint Template is implemented on this XML, directed by one flow responsible of excecuting the logic.
For the pourpose of this particular Anypoint Template the *mainFlow* just executes the Batch Job which handles all the logic of it. The batch job call other subflows *insertPcRequest* and *insertDeskRequest* for every worker, which are responsible for creation of service request and requested items in ServiceNow.
This flow has Exception Strategy that basically consists on invoking the *defaultChoiseExceptionStrategy* defined in *errorHandling.xml* file.



## endpoints.xml<a name="endpointsxml"/>
This is the file where you will found the inbound and outbound sides of your integration app.
This Anypoint Template has only an [HTTP Inbound Endpoint](http://www.mulesoft.org/documentation/display/current/HTTP+Endpoint+Reference) as the way to trigger the use case.

**HTTP Inbound Endpoint** - Start Report Generation

+ `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
+ The path configured by default is `migrateworkers` and you are free to change it for the one you prefer.
+ The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub will then route requests from your application domain URL to the endpoint.
+ The endpoint is configured as a *request-response* since as a result of calling it the response will be the total of Workers migrated and filtered by the criteria specified.



## errorHandling.xml<a name="errorhandlingxml"/>
This is the right place to handle how your integration will react depending on the different exceptions. 
This file holds a [Choice Exception Strategy](http://www.mulesoft.org/documentation/display/current/Choice+Exception+Strategy) that is referenced by the main flow in the business logic.



