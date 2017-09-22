# google-drive-client-java

This example demonstrates how to build a Google Drive client application in Java.
This is a simple command line client application that eliminates unnecessary clutter and shows the basic logic to interact with Google Drive. Hopefully this will help you to understand the syntax (and semantic) of the API.

The application interacts with the <a href="https://developers.google.com/drive/web/about-sdk" target="_blank">Google Drive</a> via its REST API using the related Google Java client library. For more information, see <a href="https://developers.google.com/api-client-library/" target="_blank">Google API Client Libraries</a>.  See also, The <a href="http://acloudysky.com/?s=conundrum" target="_blank">REST API Conundrum</a>. 

The application uses a custom <a href="http://acloudysky.com/authenticate-google-cloud-service-client-application/" target="_blank">client authentication application</a> to be authorized to use the service.     


<h2>Prerequisites</h2>
You must have Maven installed. The dependencies are satisfied by building the Maven package.

<h2>Security Credentials</h2>
You need to set up your Google security credentials before the sample code is able to connect to the Google Drive Service. You can do this by creating a file named "client_secrets.json" at ~/.store/drive_sample (C:\Users\USER_NAME.store\drive_sample\ for Windows users) and saving the following information in the file:
<ol>
	<li>Client ID</li>
	<li>Client secrets</li>
	<li>Other... </li>
</ol>
You obtain this information by downloading the JSON format of the <i>client information for native application</i> of your project at: 
<a href="https://console.developers.google.com/project" target="_blank">Google console</a>. Then copy this information in the <i>secretsFile</i> such as client_secrets.json. <b>Keep this file in a safe place</b>.


<h2>Default Settings</h2>
The application assumes that a file exists that contains default settings. The file is assumed to be at ~/.store/drive_sample/sample_settings.json  (C:\Users\USER_NAME.store\drive_sample\sample_settings.json for Windows users). The settigs are in Jason format similar to the following: 

<pre>
{
  "project": "<your project ID>",
  "sourceFileID": "<default source file ID>",
  "folderID": "<default source folder ID>",
  "sourceFile": "<default source file name>",
  "textTargetFileName" : "<default target file name>",
  "email": "<your e-mail">
}
</pre>
			 
<h2>Running the Example</h2>
The application connects to <a href="https://developers.google.com/drive/" target="_blank">Google Drive</a>, and allows the user to upload a file into the account, delet a file, list the files in the account and so on. All you need to do is run it by following these steps:
<ol>
	<li>From the project, create an executable JAR</li>
	<li>From a terminal window, go to the directory containing the JAR and execute a command similar to 
	the following:   
	<pre>
  	java -jar google-drive-client-java.jar
	</pre>	
	</li>
</ol> 

<h2>License</h2>
This sample application is distributed under the <a href="http://www.apache.org/licenses/LICENSE-2.0" target="_blank">Apache License, Version 2.0</a>.

##References
* [Chrome Web Store Listing](https://developers.google.com/drive/v3/web/listing "Create a Web listing using Google Drive")
* [Drive API (v3) Quickstart](https://developers.google.com/drive/v3/web/quickstart/java "Quickstart for Java"). See how to enable the Drive REST API among other things.
* [Drive API (v3) Reference](https://developers.google.com/drive/v3/reference/ "Quickstart for Java"). This API reference is organized by resource type. Each resource type has one or more data representations and one or more methods.
* [Drive API (v3) Mime Types](https://github.com/google/google-drive-proxy/blob/master/DriveProxy/API/MimeType.cs "Mime Types"). Complete list of MIME types.
