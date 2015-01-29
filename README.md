
# Anypoint Template: Workday Worker to ServiceNow Request Broadcast

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
When a new employee is hired, create 2 service requests
1. One service request for setting up a desk
2. one service request for setting up a computer

If the department is sales, make the request for Building A, if the department is anything else make their seat building B.	

The data is processed as follows:
1. Workday is polled in intervals for new modifications of employees
2. Employee data is processed to identify new hires
3. Service request for a computer is sent to ServiceNow
4. Service request for a desk is sent to ServiceNow

# Considerations <a name="considerations"/>

There are no special considerations for this template.





## ServiceNow Considerations <a name="servicenowconsiderations"/>

There may be a few things that you need to know regarding ServiceNow, in order for this template to work.


### As destination of data

There are no particular considerations for this Anypoint Template regarding ServiceNow as data destination.
## Workday Considerations <a name="workdayconsiderations"/>

### As source of data

There are no particular considerations for this Anypoint Template regarding Workday as data origin.

# Run it! <a name="runit"/>
Simple steps to get Workday Worker to ServiceNow Request Broadcast running.


## Running on premise <a name="runonopremise"/>
In this section we detail the way you have to run you Anypoint Temple on you computer.


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
Complete all properties in one of the property files, for example in [mule.prod.properties] (../blob/master/src/main/resources/mule.prod.properties) and run your app with the corresponding environment variable to use it. To follow the example, this will be `mule.env=prod`. 


## Running on CloudHub <a name="runoncloudhub"/>
While [creating your application on CloudHub](http://www.mulesoft.org/documentation/display/current/Hello+World+on+CloudHub) (Or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to be configured** as well as the **mule.env**.


### Deploying your Anypoint Template on CloudHub <a name="deployingyouranypointtemplateoncloudhub"/>
Mule Studio provides you with really easy way to deploy your Template directly to CloudHub, for the specific steps to do so please check this [link](http://www.mulesoft.org/documentation/display/current/Deploying+Mule+Applications#DeployingMuleApplications-DeploytoCloudHub)


## Properties to be configured (With examples) <a name="propertiestobeconfigured"/>
In order to use this Mule Anypoint Template you need to configure properties (Credentials, configurations, etc.) either in properties file or in CloudHub as Environment Variables. Detail list with examples:
### Application configuration
+ poll.frequencyMillis `10000`
+ poll.startDelayMillis `500`
+ watermark.default.expression `#[groovy: new Date(System.currentTimeMillis() - 10000)]`

#### WorkDay Connector configuration for company A
+ wday.user `user1@mulesoft_pt1`
+ wday.password `ExamplePassword565`
+ wday.endpoint `https://services1.workday.com/ccx/service/acme/Human_Resources/v20`

#### ServiceNow Connector configuration for company B
+ snow.user `snow_user1`
+ snow.password `ExamplePassword881`
+ snow.endpoint `https://instance.service-now.com`

+ snow.pc.assignedTo `1e826bf03710200044e0bfc8bcbe5d9c`
+ snow.desk.assignedTo `1e826bf03710200044e0bfc8bcbe5d9c`
+ snow.desk.model `5c7eb5092ba7a100c173448405da1563`
+ snow.pc.model `e212a942c0a80165008313c59764eea1`
+ snow.locationA `30fffb993790200044e0bfc8bcbe5dcc`
+ snow.locationB `8228cda2ac1d55eb7029baf443945c37`

+ snow.pc.deliveryDays `5`
+ snow.pc.price `3000`

+ snow.desk.deliveryDays `3`
+ snow.desk.price` 500`

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
This file holds the functional aspect of the template (points 2. to 4. described in the template overview. Its main component is a Batch job, and it includes *steps* for executing the broadcast operation from Workday to ServiceNow.



## endpoints.xml<a name="endpointsxml"/>
This file should contain every inbound endpoint of your integration app. It is intended to contain the application API.
In this particular template, this file contains a poll inbound endpoints that query Workday for updates using watermark.



## errorHandling.xml<a name="errorhandlingxml"/>
Contains a [Catch Exception Strategy](http://www.mulesoft.org/documentation/display/current/Catch+Exception+Strategy) that is only Logging the exception thrown (If so). As you imagine, this is the right place to handle how your integration will react depending on the different exceptions.



