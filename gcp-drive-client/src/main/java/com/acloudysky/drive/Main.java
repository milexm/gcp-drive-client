package com.acloudysky.drive;


import com.acloudysky.auth.AuthenticateGoogleServiceClient;
import com.acloudysky.auth.IGoogleServiceClientAuthentication;

import com.acloudysky.ui.UserInterface;

import com.acloudysky.utilities.Utility;
import com.google.api.services.drive.Drive;

/**
 * Main class for the google-drive-client console application. 
 * @author Michael
 *
 */
public class Main {

	 
	/***
     * Application entry point which displays the start greetings and performs the following main tasks:
     * <ul>
     *      <li>Gets the authenticated client object authorized to access the Google Drive service REST API.</li> 
     *		<li>Reads the default settings.</li>
     * 		<li>Instantiates the operation classes.</li>
	 * 		<li>Delegates to the SimpleUI class the display of the selection menu and the processing of the user's input.</li>
	 * </ul>
	 * <b>Notes</b>
	 * <ul>
	 * 	<li>This client application assumes that you have a folder named <i>.googleservices</i> in your home directory. In the code this folder
	 *  name is assigned to the <i>parentDir</i> variable.</li>
	 *  <li>The <i>.googleservices</i> folder contains another folder named <i>drive</i>. In the code this folder
	 *  name is assigned to the <i>dataDir</i> variable.</li>
	 *  <li>The <i>drive</i> folder contains the following files: <i>client_defaults.json</i>, <i>client_secrets.json</i> and 
	 *  <i>StoredCredential</i>. The first file contains the defaults values for the client app; the second file contains crucial  info
	 *  to authenticate this client app to allow the use of the Drive service REST API. The last file, whose name cannot be changed because defined
	 *  by OAuth2, stores the OAuth2 evaluated credentials so you are not asked to allow access every time you run the client app. If you delete the 
	 *  file you will be asked again to allow access to the Drive REST API. By the way when you allow access, select the Google ID whose drive you
	 *  want allow access to.  
	 * </ul>
	 * @see DriveDefaultSettings#readSettings()  
	 * @see FileOperations#initFileOperations(Drive, DriveDefaultSettings)
     * @see SimpleUI#SimpleUI(DriveDefaultSettings)
	 * @param args args[0] = "drive"
	 * 
	 */
	public static void main(String[] args) {
	
		// The API client name
		String client = null;
		
		// Set DEBUG flag for testing. 
		Utility.setDEBUG(false);
	
		// Display greeting message.
		UserInterface.displayWelcomeMessage("Google Drive Service");
		
		// Read input parameters.
		try {
			
				client = args[0];
		}
		catch (Exception e) {
			System.out.println("IO error trying to read application input! Assigning default value.");
			// Assign default values if none are passed.
			if (args.length==0) {
				client = "drive";
			}
			else {
				System.out.println("IO error trying to read application input!");
				System.exit(1); 
			}
		}
		
		if (Utility.isDEBUG()) {
			String startGreetings = String.format("Start %s console application", client);
			System.out.println(startGreetings);	
		}
		
		
		
		if (Utility.isDEBUG())
			Utility.getAbsoluteFilePath(".googleservices", "drive", "client_secrets.json");
		
		// Instantiate the AuthenticateGoogleServiceClient class.
		AuthenticateGoogleServiceClient serviceAuthentication = 
				new AuthenticateGoogleServiceClient(".googleservices", "drive", "client_secrets.json"); 
		
		
		// Create an authenticated client which is authorized to use Google Drive REST API.
		Drive driveServiceClient = null;
		
		try {
				String selectedScope = 
						serviceAuthentication.getScope(IGoogleServiceClientAuthentication.driveScopes, "drive");
				if (Utility.isDEBUG()) {
					// Display scopes.
					Utility.displayScopes(IGoogleServiceClientAuthentication.driveScopes);
					System.out.println("Selected scope: " + selectedScope);	
				}
				driveServiceClient = 
						serviceAuthentication.getAuthenticatedDriveClient(selectedScope);
				String service = driveServiceClient.getApplicationName();
				if (Utility.isDEBUG()) {
					System.out.println(String.format("App name is: %s", service));
				}
		}
		catch (Exception e) {
			System.out.println(String.format("Error %s during service authentication.", e.toString()));
		}
			
		
		if (driveServiceClient != null) {
			
			// Instantiate the DriveDefaultSettings class.
			DriveDefaultSettings defaultSettings = new DriveDefaultSettings();
			
						
			// Initialize Drive operations classes.
			FileOperations.initFileOperations(driveServiceClient, defaultSettings);
			OtherOperations.initCommentOperations(driveServiceClient);
			
			// Instantiate SimpleUI class and display menu.
			SimpleUI sui = new SimpleUI(defaultSettings);
			// Start loop to process user's input.
			sui.processUserInput();
		}
		else 
			String.format("Error %s", "service object is null.");

		// Display goodbye message.
		UserInterface.displayGoodbyeMessage("Google Drive Service");	
	}

	
}

