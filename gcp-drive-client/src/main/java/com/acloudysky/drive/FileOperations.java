/** 
 * LEGAL: Use and Disclaimer. 
 * This software belongs to the owner of the http://www.acloudysky.com site and supports the
 * examples described there. 
 * Unless required by applicable law or agreed to in writing, this software is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. 
 * Please, use the software accordingly and provide the proper acknowledgement to the author.
 * @author milexm@gmail.com  
 **/
package com.acloudysky.drive;

import com.acloudysky.utilities.Utility;
import com.google.api.client.http.FileContent;

import com.google.api.services.drive.Drive;

import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Export;


import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.io.FileOutputStream;

import java.io.IOException;

import java.util.Collections;

import java.util.Iterator;
import java.util.List;


/***
 * Contains the methods which issue Google Drive  API calls to perform file operations.
 * For more information see <a href="https://developers.google.com/drive/v3/reference/files" target="_blank">Files</a>.
 * @author Michael
 *
 */
public class FileOperations {

	
	// The authenticated service client authorized to use the Google Drive REST API.
	private static Drive authenticatedClient;
	private static DriveDefaultSettings defaultSettings;
	
	
	/***
	 * Internal class that contains utility methods used during the
	 * processing of file operations.
	 * @author Michael
	 *
	 */
	private static class FileUtility {
		
		private static StringBuilder buffer;
		
		
		/**
		 * Display information for the specified file.
		 * @param file The file for which to display information.
		 */
		private static void displayFileInfo(File file) {
			
		
			// Initialize the buffer to hold formatted information.
			buffer = new StringBuilder();
			buffer.append(String.format("%n=== " + "File Information" + " ==== %n"));	
			
			// Store formatted file info.
			buffer.append(String.format("%n  Name:        %s", file.getName()));
			buffer.append(String.format("%n  ID:          %s", file.getId()));
			buffer.append(String.format("%n  Mime Type:   %s", file.getMimeType()));
			buffer.append(String.format("%n  Date:        %s", file.getCreatedTime()));
			
			buffer.append(String.format("%n  Description: %s", file.getDescription()));
			
			// Display files information.
			System.out.println(buffer.toString());
		}		
		
		
	
	  /**
	   * Inserts a folder in the Google Drive at the very top of the directory structure.
	   * This folder is used by the app to perform the operations selected by the user. 
	   * <b>Note</b>. For sim0licity the folder is not created if one already exists. 
	   * To start from scratch you must delete the folder manually and assign empty string to the folder ID 
	   * in the client_deafults.json file. 
	   * @param foldername The name of the folder to create.
	   * @return The folder metadata if successful; otherwise, null.  
	   */
	  private static File createDefaultFolder(String foldername) {
		  try {
			  	
			  	File fileMetadata = new File();
			  	
			  	// Get the stored folder ID.
				String folderId = defaultSettings.getFolderID();
				
				if (!folderId.isEmpty()){
					// The folder ID exists, do not create the folder.
					System.out.println(String.format("Folder %s already exists; not created.", foldername));
				}
				else {
						// Create the folder.
						fileMetadata.setName(foldername);
						fileMetadata.setMimeType("application/vnd.google-apps.folder");
						fileMetadata = authenticatedClient.files().create(fileMetadata).execute();
						
						// Store the folder Id in the in memory defaults.  
						defaultSettings.setFolderID(fileMetadata.getId());
						
						if (Utility.isDEBUG())
							System.out.println(String.format("default folder id: %s ", defaultSettings.getFolderID()));
						
				}
				
			  	return fileMetadata;
			  	
		} catch (IOException e) {
			System.out.println(String.format("insertFolder error: %s ", e.toString()));
			return null;
		}
		  
		  
	  }
	  
		  
}		
	

	/**************************
	 * FileOperations Methods *
	 **************************/
	
	
	/**
	 * Initializes class global variables.
	 * It also inserts a folder into the Google Drive to use for file operations. 
	 * <p><b>Note</b>. The name of the folder is contained in the <i>.googles/drive_sample/default_settings.json</i> file. 
	 * @param serviceClient The authenticated service authorized to access Google Drive using its REST API.
	 * @param clientDefaultSettings The default settings for this application.  
	 */
	public static void initFileOperations(Drive serviceClient, DriveDefaultSettings clientDefaultSettings) {
		
		authenticatedClient = serviceClient;
		defaultSettings = clientDefaultSettings.readSettings();
		
		
		
		// Get folder name and its ID from the .googles/drive/client_settings.json file. 
		String folderName = defaultSettings.getFolder();
		String folderID = defaultSettings.getFolderID();
		
		if (Utility.isDEBUG()) {
			System.out.println(String.format("[initFileOperations] folder name is: %s", folderName));
			System.out.println(String.format("[initFileOperations] folder ID is: %s", folderID));
		}
			
		
		if (folderID.isEmpty()) {
			try {
					
					// Create the folder in the Google Drive
					File fileMetadata = FileUtility.createDefaultFolder(folderName);
				
					folderID = fileMetadata.getId();
					
					// Update the default settings; so we can use the ID later on for file operations.
					defaultSettings.setFolderID(folderID);
					
					// Update the default folder Id in the client_defaults.json file. 
					// defaultSettings.writeSettingsFolderID(".googleservices", "drive", "client_defaults.json", folderID);
					
					// Update the default folder Id in the client_defaults.json file. 
					defaultSettings.updateDefaultSettings("folderID", folderID);
					
					System.out.println(String.format("%s %s", fileMetadata.getName(), " created."));
					
			} catch (Exception e){ 
				System.out.println(String.format("[initFileOperations] error: %s", e.toString()));
			}
		}
		else
				System.out.println(String.format("[initFileOperations] %s already exists; not created.", folderName));
			
	}
	
	  /**
	   * Uploads the specified file.
	   *
	   * @param title Title of the file to insert, including the extension.
	   * @param description Description of the file to insert.
	   * @param parentId Parent folder's ID.
	   * @param uploadMimeType MIME type of the file to use for upload.
	   * @param downloadMimeType MIME type to use for download.
	   * @param filename Filename of the file to insert.
	   * @param defaultSettingsKey The default settings for the application.
	   * @return Inserted file metadata if successful, otherwise {@code null}.
	   ***/
	  public static File uploadFile(String title, String description,
	      String parentId, String uploadMimeType, String downloadMimeType, String filename, String defaultSettingsKey) {
	   
		// File's metadata.
	    File fileMetadata = new File();
	    fileMetadata.setName(title);
	    fileMetadata.setDescription(description);
	    // Set the MIME for the target file (in case conversion is required).
	    fileMetadata.setMimeType(uploadMimeType);
	

		String msg = 
	    		String.format("%n=== " + "Uploading file: %s" + " === %n", filename);
		System.out.println(msg);
		
	    // Set the parent folder.
	    if (parentId != null && parentId.length() > 0)
	    	fileMetadata.setParents(Collections.singletonList(parentId));
	
	
	    // File to upload
	    java.io.File fileContent = new java.io.File(filename);
	    // Set input stream based on the previous file and specify the download MIME type.
	    FileContent mediaContent = new FileContent(downloadMimeType, fileContent);
	    try 
	    {
			// Insert the file in the Drive storage. 
			Files.Create request = authenticatedClient.files().create(fileMetadata, mediaContent);
			request.getMediaHttpUploader().setDirectUploadEnabled(true);
			File file = request.execute();
			
			String fileID = file.getId();
			// Update the default folder Id in the client_defaults.json file. 
			defaultSettings.updateDefaultSettings(defaultSettingsKey, fileID);
			// Display header information, if debug is enabled.
			if (Utility.isDEBUG()){
				System.out.println(String.format("File ID:    %s", file.getId()));
				System.out.println(String.format("Mime type:  %s", file.getMimeType()));
				System.out.println(String.format("File title: %s", file.getName()));
			}
			return file;
	    } 
	    catch (IOException e) {
			System.out.println("An error occured: " + e);
			return null;
	    }
	  }
		  	  

	/**
	 * Downloads selected file into teh specified local directory.
	 * @param fileID The ID of the file to download.
	 * @param localDir The local directory where to download the file.
	 * @param localFile The name of the local file.
	 * @param downloadMimeType The format of the file when downloaded. 
	 * @throws IOException An I/O error has been detected.
	 */
	public static void  donwloadFile(String fileID, String localDir, String localFile, String downloadMimeType) throws IOException {
		
		try {
				// Get the file.
				File file = authenticatedClient.files().get(fileID).execute();
			
				// Display file information.
				 if (Utility.isDEBUG())
					 FileUtility.displayFileInfo(file);
		    
				String msg = 
			    		String.format("%n=== " + "Dowloading file: %s whose MIME is: %s" + " === %n", file.getName(), file.getMimeType());
				System.out.println(msg);	
			
				// Get local file absolute path.
				String outFilePath= Utility.getAbsoluteFilePath(localDir, localFile);
				
				// Associate output stream with local file.
				java.io.File locFile = new java.io.File(outFilePath); 
				FileOutputStream outputStream = new FileOutputStream(locFile);
	    	  
				// Download file from Drive and copy it to the local file. 
				String fileMimeType = file.getMimeType();
				
	
				if ( fileMimeType.equals(downloadMimeType))
					// No conversion is required.
					authenticatedClient.files().get(fileID).executeMediaAndDownloadTo(outputStream);
				else {
						// Coveriosn is required from Drive (Google) format to download format.
						Export request = authenticatedClient.files().export(fileID, downloadMimeType);
						request.executeMediaAndDownloadTo(outputStream);
				}
				
				outputStream.close();
	    	 	System.out.println(String.format("%s downloaded ", file.getName()));	
		        
		} 
		catch (IOException e) {System.out.println(
					   String.format("Error occurred: %s", e.getMessage()));
		}
		
	}
	
	 
	/**
	 * Lists the last modified files. 
	 * Orders the response by file/folder name in descendant order of 
	 * modification (last modified). 
	 * @param numberOfiles The number of files to display.
	 * @throws IOException An I/O error has been issued.
	 */
	public static void listFiles(int numberOfiles) throws IOException {
	  
		String pageToken = null;
		// Get the Files resource. 
	    Files files = authenticatedClient.files();
	    
	    // Get the list of the files. 
	    FileList fileList = files.list()
	        // Set the maximum number of files to return.
	    	.setPageSize(numberOfiles) 
	    	// Order by file name in descendant order of modification (last modified). 
	    	.setOrderBy("modifiedTime desc,name")
	    	
			.setPrettyPrint(true)
			// Set the space to query.
			.setSpaces("drive")
	        // Set the fields to include in the response.
	        .setFields("nextPageToken, files(id, name, parents, modifiedTime, lastModifyingUser, mimeType)")
	        // Set the token for continuing a previous list request on the next page. 
	        // This should be set to the value of 'nextPageToken' from the previous response.
	        .setPageToken(pageToken) 
	        .execute();
		      
	    	// Display the requested info for each file in the list.
		    for(File file: fileList.getFiles()) {
		    	  
		    	List<String> fileParentIDs =  file.getParents();
			    	
			    // Iterate through the list
		    	Iterator<String> iterator = fileParentIDs.iterator();
		    	
		    	String entry = "";
		    	String parentName = "" ;
		    	while (iterator.hasNext()) {	
		    		entry = iterator.next();
					File parentFolder = authenticatedClient.files().get(entry).execute();	
					parentName = parentFolder.getName();
				}
	          
		    	String userName = file.getLastModifyingUser().getDisplayName();
				System.out.printf("file: %s file id: (%s) parent: %s mmimeType: %s modified: %s by %s\n",
	                  file.getName(), file.getId(), parentName, file.getMimeType(),  file.getModifiedTime(), userName);
				
	      }	  
			  
	  }	  
	  
	  
	 /**
	 * Lists the last modified files of specified MIME type.
	 * Orders the response by file/folder name in descendant order of 
	 * modification (last modified). 
	 * @param numberOfiles The number of files to display.
	 * @param mimeType The MIME type of the files to display.
	 * @throws IOException An I/O error has been issued.
	 */
	 public static void listFilesofSpecifiedType(int numberOfiles, String mimeType) throws IOException {
		  String pageToken = null;
		  String query = "mimeType='" +  mimeType + "'";
	
		  FileList fileList = authenticatedClient.files().list()
			.setQ(query) // Set MIME type
			// Set the maximum number of files to return.
			.setPageSize(numberOfiles) 
		    // Order by file/folder name in descendant order of modification (last modified). 
		    .setOrderBy("modifiedTime desc,name")
		    .setPrettyPrint(true)
		    .setSpaces("drive")
		    // Set the fields to include in the response.
            .setFields("nextPageToken, files(id, name, parents, modifiedTime)")
            .setPageToken(pageToken) 
            .execute();
		      
		  // Display the requested info for each file in the list.
		  for(File file: fileList.getFiles()) {
		    	  
	    	List<String> fileParentIDs =  file.getParents();
		    	
		    // Iterate through the list
	    	Iterator<String> iterator = fileParentIDs.iterator();
	    	
	    	String entry = "";
	    	String parentName = "" ;
	    	while (iterator.hasNext()) {	
	    		entry = iterator.next();
				File parentFolder = authenticatedClient.files().get(entry).execute();	
				parentName = parentFolder.getName();
			}
          
			System.out.printf("file: %s file id: (%s) parent: %s modified: %s\n",
                  file.getName(), file.getId(), parentName, file.getModifiedTime());
		  }	
		 
	 }	  
	  
	

	
	  
	 /******* 
	  * Parents Operations *
	  * *******/
	
	  /**
	   * Gets the folder that contains a file.
	   * @param fileID The ID of the file whose parents must be found.
	   * @throws IOException An I/O error has been detected.
	   ***/
	  public static void getParents(String fileID) throws IOException {
			
		try {
			
				// Get the file.
				File file = authenticatedClient.files().get(fileID)
						.setFields("name, parents")
						.execute();
				
				String fileName = file.getName();
			    String msg = 
			    		String.format("%n================== " + "Getting parent of file: %s" + " ================== %n", fileName);
			    System.out.println(msg);	
			    
			    // Get the parent id list.
			    List<String> fileParentIDs =  file.getParents();
		    	
			    // Iterate thorugh the list
		    	Iterator<String> iterator = fileParentIDs.iterator();
		    	
		    	String entry = "";
		    	String parentName = "" ;
		    	while (iterator.hasNext()) {	
		    		entry = iterator.next();
					File parentFolder = authenticatedClient.files().get(entry).execute();	
					parentName = parentFolder.getName();
				}
			    
				System.out.println(String.format("%s is in: %s folder", fileName, parentName));
				
		} 
		catch (IOException e) {System.out.println(
					   String.format("Error occurred: %s", e.getMessage()));
		}
		
	}
		  
	
}
