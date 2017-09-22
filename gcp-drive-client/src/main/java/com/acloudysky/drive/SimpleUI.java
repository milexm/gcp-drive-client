package com.acloudysky.drive;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;


import com.acloudysky.ui.UserInterface;

import com.acloudysky.utilities.Utility;

import com.google.api.services.drive.model.File;

/*** 
 * Displays the user's menu. 
 * Processes the  user's input and calls the proper method based on the user's selection. 
 * Each method calls the related Google Cloud Service REST API.
 * <b>Notes</b>. This REST API is organized by resource type. Each resource type has one or more data representations 
 * and one or more methods. The resources are as follows:
 * <ul>
 * 	<li>About. Handles information about the user, the user's Drive, and system capabilities.</li>
 *  <li>Changes. Handles information about change sto a file or team drive.</li>
 *  <li>Channels. </li>
 *  <li>Comments. Handles file comments.</li> 
 *  <li>Files. </li>
 *  <li>Permissions. Handles file permissions.</li>
 *  <li>Replies. Handles replies to file comments.</li>
 *  <li>Revisions. Handles file revisions.</li>
 *  <li>Teamdrives. Handles team drives. </li>
 * </ul>
 * For more information, see <a href="https://developers.google.com/drive/v3/reference/#resource-types" target="_blank">API Reference</a>.
 * @author Michael Miele.
 *
 */
public class SimpleUI extends UserInterface {

	
	// The default settings object.
	DriveDefaultSettings defaultSettings=null;
	
	/**
	 * Instantiates SimpleUI class along with its superclass.
	 * Initializes the defaultSettings object. Displays the user's menu. 
	 * @param clientDefaultSettings The application default settings.
	 */
	SimpleUI(DriveDefaultSettings clientDefaultSettings) {
		
		// Instantiate/initialize the UserInterface parent class.
		super();
		
		//Initializes the defaultSettings object.
		defaultSettings = clientDefaultSettings;
		
		// Display menu.
		displayMenu(driveMenuEntries);
	}
	
	/*********************
	 * Utility functions *
	 *********************/
	
	/**
	 * Reads user input.
	 * @param msg The message to display for the user. 
	 */
	private static String readUserInput(String msg) {
		
		// Open standard input.
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));

		String selection = null;
		
		// Read the selection from the command-line.
		// Need to use try/catch with the readLine() method.
		try {
			if (msg == null)
				System.out.print("\n>>> ");
			else
				System.out.print("\n" + msg);
			selection = br.readLine();
		} catch (IOException e) {
			System.out.println("IO error trying to read your input!");
			System.out.println(String.format("%s", e.getMessage()));
			System.exit(1);
		}
		
		return selection;

	}
	
	
	/**
	 * Uploads the selected file into the folder created during initialization. 
	 * Notice the file must already exist in the resources folder.
	 * The MIME types can be found at 
	 * <a href="https://github.com/google/google-drive-proxy/blob/master/DriveProxy/API/MimeType.cs" target="_blank">MIME Types</a>.
	 * See also <a href="https://developers.google.com/drive/v3/web/integrate-open" target="_blank">Open Files</a> for allowed conversions.
	 * @param fileType The type of file to upload.
	 */
	private  void uploadFile(String fileType) {
		
		String parentID, fileID="";
		// Insert a text file.
		String title="", description="", uploadMimeType="", downloadMimeType="";
		String fileName="", defaultFileID="";
		
		// Get the default folder ID from the .googles/drive/client_settings.json file. 
		parentID = defaultSettings.readSettings().getFolderID();
		
		for (int x = 0; x < mimeTypes.length; x++) 
		{
		    for (int y = 0; y < 3; y++) 
		    {
		    	if (fileType.equals(mimeTypes[x][y])) {
		    		if (Utility.isDEBUG()) 
		    			System.out.println(String.format("%s  %s %s", mimeTypes[x][y], 
		    					mimeTypes[x][y+1], mimeTypes[x][y+2]));
		    		uploadMimeType = mimeTypes[x][y+1];
					downloadMimeType = mimeTypes[x][y+2];
		    		break;
		    	}
		    }
		}
		
		// Select properties of the file to upload .
		switch(fileType.toLowerCase()) {
		
			case "txt": {
				// Get the default file ID from the .googles/drive/client_settings.json file. 
				fileID = defaultSettings.readSettings().getSourceTextFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				fileName = defaultSettings.readSettings().getSourceTextFile();
				// Set the default settings key for searching the .googles/drive/client_settings.json file. 
				defaultFileID = "sourceTextFileID";
				// Set file metadata info.
				title = "midsummereve";
				description = "poem";
				break;
			}
			
			case "docx": {
				// Get the default file ID from the .googles/drive/client_settings.json file. 
				fileID = defaultSettings.readSettings().getSourceDocFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				fileName = defaultSettings.readSettings().getSourceDocFile();
				// Set the default settings key for searching the .googles/drive/client_settings.json file. 
				defaultFileID = "sourceDocFileID";
				// Set file metadata info.
				title = "midsummereve";
				description = "poem";
				break;
			}
			case "pptx": {
				// Get the default file ID from the .googles/drive/client_settings.json file. 
				fileID = defaultSettings.readSettings().getSourcPresFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				fileName = defaultSettings.readSettings().getSourcePresFile();
				// Set the default settings key for searching the .googles/drive/client_settings.json file. 
				defaultFileID = "sourcePresFileID";
				// Set file metadata info.
				title = "midsummereve";
				description = "poem";
				break;
			}
			case "xlsx": {
				// Get the default file ID from the .googles/drive/client_settings.json file. 
				fileID = defaultSettings.readSettings().getSourceSpreadFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				fileName = defaultSettings.readSettings().getSourceSpreadFile();
				// Set the default settings key for searching the .googles/drive/client_settings.json file. 
				defaultFileID = "sourceSpreadFileID";
				// Set file metadata info.
				title = "midsummereve";
				description = "poem";
				break;
			}
			case "jpeg": {
				// Get the default file ID from the .googles/drive/client_settings.json file. 
				fileID = defaultSettings.readSettings().getSourceImageFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				fileName = defaultSettings.readSettings().getSourceImageFile();
				// Set the default settings key for searching the .googles/drive/client_settings.json file. 
				defaultFileID = "sourceImageFileID";
				// Set file metadata info.
				title = "towmater";
				description = "toy car";
				break;
			}
			default: {
				System.out.println(String.format("%s is not allowed", fileType));
				break;
			}
		}
		if (Utility.isDEBUG()) {
			System.out.println(String.format("[initFileOperations] folder ID is: %s", parentID));
			System.out.println(String.format("[initFileOperations] file ID is: %s", fileID));
		}
		
		if (fileID.isEmpty()) {
			
			try{
			
				// Insert the file.
				String filePath = null;
				URL fileUrl = null;
				
				// Get the path of the file to insert.
				try {
						// It is assumed that the file is stored in the project resources folder. 
						fileUrl = this.getClass().getResource("/" + fileName);
						filePath = fileUrl.getPath();
				
				} 
				catch (Exception e) {
					System.out.println(String.format("fileUrl error: %s", e.toString()));
				}
				
				// Upload the file.
				File file = FileOperations.uploadFile(title, description, parentID, uploadMimeType, downloadMimeType, filePath, defaultFileID);
				System.out.println(String.format("%s %s", file.getName(), " uploaded"));
				
			}
			catch (Exception e){
				System.out.println(String.format("%s", e.getMessage()));
			}
		}
		else 
			System.out.println(String.format("[SimpleUI] %s already exists; not uploaded", fileName));
		
	}
	
	/**
	 * Downloads the selected file into the local target directory.  
	 * @param fileType The type of file to download.
	 * @param fileID The ID of the file to download.
	 * @param targetDir  The local directory where to download the file. 
	 */
	private  void downloadFile(String fileType, String fileID, String targetDir) {
		
		String targetfileName="";
		String type = fileType.toLowerCase();
		String downloadMimeType = "";
		
		for (int x = 0; x < mimeTypes.length; x++) 
		{
		    for (int y = 0; y < 3; y++) 
		    {
		    	if (fileType.equals(mimeTypes[x][y])) {
		    		if (Utility.isDEBUG()) 
		    			System.out.println(String.format("%s  %s %s", mimeTypes[x][y], 
		    					mimeTypes[x][y+1], mimeTypes[x][y+2]));
					downloadMimeType = mimeTypes[x][y+2];
		    		break;
		    	}
		    }
		}
		// Select properties of the file to upload .
		switch(type) {
		
			case "txt": {
				
				if (fileID.isEmpty())
					// Get the default file ID from the .googles/drive/client_settings.json file. 
					fileID = defaultSettings.readSettings().getSourceTextFileID();
				// Get the name of the default file to download.
				targetfileName = defaultSettings.readSettings().getSourceTextFile();
				break;
			}
			
			case "docx": {
				if (fileID.isEmpty())
					// Get the default file ID from the .googles/drive/client_settings.json file. 
					fileID = defaultSettings.readSettings().getSourceDocFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				targetfileName = defaultSettings.readSettings().getSourceDocFile();
				break;
			}
			
			case "pptx": {
				if (fileID.isEmpty())
					// Get the default file ID from the .googles/drive/client_settings.json file. 
					fileID = defaultSettings.readSettings().getSourcPresFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				targetfileName = defaultSettings.readSettings().getSourcePresFile();
				break;
			}
			
			case "xlsx": {
				if (fileID.isEmpty())
					// Get the default file ID from the .googles/drive/client_settings.json file. 
					fileID = defaultSettings.readSettings().getSourceSpreadFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				targetfileName = defaultSettings.readSettings().getSourceSpreadFile();
				break;
			}
			
			case "jpeg": {
				if (fileID.isEmpty())
					// Get the default file ID from the .googles/drive/client_settings.json file. 
					fileID = defaultSettings.readSettings().getSourceImageFileID();
				// Get the default file name from the .googles/drive/client_settings.json file. 
				targetfileName = defaultSettings.readSettings().getSourceImageFile();
				break;
			}
			default: {
				System.out.println(String.format("%s is not allowed", fileType));
				break;
			}
		}
		
		try{
			
			// Copy file into the local temp directory.
			FileOperations.donwloadFile(fileID, targetDir, targetfileName, downloadMimeType);
			
		}
		catch (Exception e){
			System.out.println(String.format("%s", e.getMessage()));
		}
	}
	
	/********************************
	 * Process user input functions *
	 ********************************/
	
	/***
	 * Gets user selection and calls the related method.
	 * Loops indefinitely until the user exits the application.
	 */
	@Override
	public void processUserInput() {
		
		while (true) {
			
			// Get user input.
			String userSelection = readUserInput(null).toLowerCase();	
			// Normalize user's input.
			String normalizedUserSelection = userSelection.trim().toLowerCase();
			

			try{
				// Exit the application.
				if ("x".equals(normalizedUserSelection)){
					break;
				}
				else
					if ("m".equals(normalizedUserSelection)) {
						// Display menu
						displayMenu(driveMenuEntries);
						continue;
					}
				
			}
			catch (Exception e){
				// System.out.println(e.toString());
				System.out.println(String.format("Input %s is not allowed%n", userSelection));
				continue;
			}
			
			performOperation(normalizedUserSelection);
		}
		
	}
	
	
	/*
	 * Performs the operation selected by the user.
	 */
	private void performOperation(String selection) {
	
		// Select operation to perform.
		String fileType = "";
		String fileID = "";
		
		switch(selection) {
		
			// List all the files contained in the Drive.
			case "lf": {
			
				try{
					// List 10 files.
					FileOperations.listFiles(10);
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			// List the file of specified type contained in the Drive.
			case "lft": {
			
				try{
					String mimeType = readUserInput("Enter mimeType (enter, to use default): ");
					if (mimeType.isEmpty())
						// Assign default value.
						mimeType = defaultSettings.readSettings().getMimeType();
					// List 10 files of the specified MIME type.
					FileOperations.listFilesofSpecifiedType(10, mimeType);
				} 
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
				
			// Insert comments in specified file.
			case "test": {
			
				try{
					fileID = readUserInput("File ID (enter, to use default): ");
					if (fileID.isEmpty())
						// Assign default value.
						fileID = defaultSettings.readSettings().getSourceDocFileID();
					
					// List 10 file comments.
					OtherOperations.driveInfo();
				} 
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			// List the comments for specified file.
			case "lfc": {
			
				try{
					fileID = readUserInput("File ID (enter, to use default): ");
					if (fileID.isEmpty())
						// Assign default value.
						fileID = defaultSettings.readSettings().getSourceDocFileID();
					
					// List 10 file comments.
					OtherOperations.listComments(10, fileID);
				} 
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			// Upload file of the specified type.
			case "up_txt": 
			case "up_docx":
			case "up_pptx":
			case "up_xlsx":
			case "up_jpeg": {
				// Extract the file type from selection.
				fileType = selection.substring(3);
				uploadFile(fileType);
				break;
			}
				
			// Download file of the specified type and copy it into local temporary directory. 
			case "dw_txt": 
			case "dw_docx":
			case "dw_pptx":
			case "dw_xlsx":
			case "dw_jpeg": {
				fileType = selection.substring(3);
				fileID = readUserInput("File ID (enter, to use default): ");
				downloadFile(fileType, fileID, "temp");
				break;
			}
			
			
			// Obtain a file parent folder. 
			case "fp": {
				try{
					fileID = readUserInput("File ID (enter, to use default): ");
					if (fileID.isEmpty())
						// Assign default value.
						fileID = defaultSettings.readSettings().getSourceImageFileID();
					
					// Get the file parent folders.
					FileOperations.getParents(fileID);
					
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
				
			default: {
				System.out.println(String.format("%s is not allowed", selection));
				break;
			}
		}
				

	}

		
}
