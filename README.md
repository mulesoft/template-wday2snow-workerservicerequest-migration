
# Anypoint Template: Workday Worker to ServiceNow Request Migration

<!-- Header (start) -->
Moves a large set of workers from Workday to ServiceNow where they are created as Service Requests. You can trigger this manually or programmatically with an HTTP call. This means that you can leverage this integration anytime you need a Service Request for a batch of users in ServiceNow. This can be useful in situations where you are changing infrastructure, office space, dealing with an acquisition, etc. This template uses batch to efficiently process many records at a time.

## Workday Requirement

Install Workday HCM - Human Resources module that you can find on the [Workday connector](https://www.mulesoft.com/exchange/com.mulesoft.connectors/mule-workday-connector/) page.

![81414f73-1410-404b-83a2-3cd5b1c4acaf-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/81414f73-1410-404b-83a2-3cd5b1c4acaf-image.png)
<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.
# Use Case
<!-- Use Case (start) -->
As a Workday admin I want to create two service requests in ServiceNow for every new unique worker from Workday:

1. Service request for setting up a desk.
2. Service request for setting up a computer.

As implemented, this template leverages the Mule batch module.
The batch job is divided into Input, Process and On Complete stages.
During the Input stage the template queries Workday for all the existing active workers that match the filtering criteria. The criteria is based on manipulations starting from the given date.
The Process stage creates new service request assigned to the concrete item - desk or computer in Service Now for each worker.
Finally, during the On Complete stage the template displays output statistics data in the console and sends a notification email with the result of the batch execution.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
There are a couple of things you should take into account before running this template:

1. **Workday email uniqueness**: The email can be repeated for two or more accounts (or missing). Therefore Workday accounts with duplicate emails or without email are removed from processing in the Input stage.
2. **Workday termination status**: A worker in Workday can be terminated. If a worker is terminated, the worker is removed from processing in the Input stage.
<!-- Considerations (end) -->

## ServiceNow Considerations

Here's what you need to know to get this template to work with ServiceNow.

### As a Data Destination

There are no considerations with using ServiceNow as a data destination.
## Workday Considerations

### As a Data Source

There are no considerations with using Workday as a data origin.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->
In any of the ways you would like to run this template this is an example of the output you'll see after browse toting the HTTP endpoint:

```
{
    "Message": "Batch Process initiated",
    "ID": "0ddb8240-ac54-11e8-b0d1-2ac63fa6f77a",
    "RecordCount": 3,
    "StartExecutionOn": "2018-08-30T14:56:04Z"
}
```
<!-- Run it (end) -->

## Run On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

- [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
- [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Import a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Run on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.

<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Run on Mule Standalone

Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`.

Complete all properties in one of the property files, for example in mule.prod.properties and run your app with the corresponding environment variable to use it. To follow the example, use `mule.env=prod`.

After this, to trigger the use case, browse to the local HTTP connector with the port you configured in your file. If this is, for instance, `9090` then browse to: `http://localhost:9090/migrate` and this outputs a summary report and sends it in the email.

## Run on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->
While creating your application on CloudHub (or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in "Properties to Configure: as well as the **mule.env**.
Once your app is all set up and started, supposing you choose `wdayworkermigration` as domain name to trigger the use case, you just need to browse to `http://wdayworkermigration.cloudhub.io/migrate` and report is sent to the email configured.
<!-- Running on Cloudhub (end) -->

### Deploy a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.

### Application Configuration
<!-- Application Configuration (start) -->
### Application Configuration

- http.port `9090`
- page.size `10`
- migration.startDate `"2019-09-03T09:10:00.000+0200"`

#### Workday Connector Configuration

- wday.username `admin@workday`
- wday.password `secret`
- wday.tenant `tenant`
- wday.hostname `wd2-impl-services1.workday.com`

#### ServiceNow Connector

- snow.user `snow_user1`
- snow.password `ExamplePassword881`
- snow.endpoint `https://instance.service-now.com`

#### ServiceNow Items Configuration

- snow.pc.assignedTo `sysId_of_the_ServiceNow_User`
- snow.pc.model `sysId_of_the_ServiceNow_Item1`
- snow.pc.deliveryDays `5`
- snow.pc.price `3000`

- snow.desk.assignedTo `sysId_of_the_ServiceNow_User`
- snow.desk.model `sysId_of_the_ServiceNow_Item2`
- snow.desk.deliveryDays `3`
- snow.desk.price `500`

- snow.location `sysId_of_the_ServiceNow_Location`
- snow.version `snow_version`


#### SMTP Services Configuration

- smtp.host `smtp.gmail.com`
- smtp.port `465`
- smtp.user `sender%40gmail.com`
- smtp.password `secret`

#### Mail Details

- mail.from `users.report%40mulesoft.com`
- mail.to `user@mulesoft.com`
- mail.subject `Workers Migration Report`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
There are no special considerations regarding API calls.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml
<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The functional aspect of this template is implemented in this XML file, directed by a flow responsible for executing the logic. For the purpose of this template, the *mainFlow* just executes a batch job. which handles all its logic.
<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This file provides the inbound and outbound sides of your integration app.
This template has only an HTTP Listener as the way to trigger the use case.

**HTTP Inbound Endpoint** - Start Report Generation

- `${http.port}` is set as a property to be defined either on a property file or in CloudHub environment variables.
- The path configured by default is `migrate` and you are free to change it for the one you prefer.
- The host name for all endpoints in your CloudHub configuration should be defined as `localhost`. CloudHub routes requests from your application domain URL to the endpoint.
- The endpoint is configured as a *request-response* since as a result of calling it, the response is the total of workers migrated and filtered by the criteria specified.
<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->
