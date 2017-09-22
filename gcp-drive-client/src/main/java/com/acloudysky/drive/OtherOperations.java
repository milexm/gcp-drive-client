package com.acloudysky.drive;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import com.acloudysky.utilities.Utility;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.About;
import com.google.api.services.drive.Drive.Comments;
import com.google.api.services.drive.Drive.Files;

import com.google.api.services.drive.model.Comment;
import com.google.api.services.drive.model.CommentList;
import com.google.api.services.drive.model.File;

/***
 * Contains the methods which issue Google Drive API calls to perform several operations.
 * For more information see 
 * <ul>
 * 		<li> <a href="https://developers.google.com/drive/v3/reference/about" target="_blank">About</a>.</li>
 *  	<li><a href="https://developers.google.com/drive/v3/reference/comments" target="_blank">Comments</a>.</li>
 * </ul>
 * The pattern implemented in each method is as follows:
 * <ol>
 * 	<li> Get the accessor (collection of methods) for creating requests from the resource collection. </li>
 * 	<li> Get the resource data model that specifies how to parse/serialize into JSON what is transmitted 
 * 		over HTTP when working with the Drive API. 
 *      To reduce the response size, get only specific fields. In the case of the About resource you can use the following syntax: 
 *      .setFields("user, storageQuota, importFormats, exportFormats").
 *      You get the names of the fields (and capitalization) from the resource 
 *      representation. Please, refer to <a href="https://developers.google.com/drive/v3/reference/about#resource" target="_blank">About</a>. 
 *  </li>
 * </ol>
 * @author Michael
 *
 */
public class OtherOperations {
	
	// The authenticated service client authorized to use the Google Drive REST API.
	private static Drive authenticatedClient;
	
	// Buffer to hold response.
	private static StringBuilder buffer = new StringBuilder();
	
	/**
	 * Initializes class global variables.
	 * @param serviceClient The authenticated service 
	 * authorized to access Google Drive using its REST API. 
	 */
	public static void initCommentOperations(Drive serviceClient) {
		authenticatedClient = serviceClient;
		
	}
	
	/**
	 * Lists the comments for the specified file.
	 * <b>Note</b> The file type must support comments such as Google Doc file types.
	 * @param numberOfComments The number of comments to display.
	 * @param fileID The ID of the file containing the comments.
	 * @throws IOException An I/O error has been issued.
	 */
	public static void listComments(int numberOfComments, String fileID) throws IOException {
		 
		// Get the Files accessor (collection of methods).
		Files files = authenticatedClient.files();
		
		// Get the File data model.
		File file = files.get(fileID)
			.execute();
		
		// Get the Comments accessor (collection of methods).
	    Comments comments = authenticatedClient.comments();
	    
	    // Get the Comments list data model by sending a request to the Drive.
	    CommentList commentListAll = comments.list(fileID)
    		.setFields("*")
	        .execute();
	    
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		
	    // Store header information into the buffer.
		buffer.append(String.format("%n==== " + "Display all comments info for the file: %s whose MIME is: %s"  + " ==== %n", file.getName(), file.getMimeType()));	
		buffer.append(Utility.newline);
		// Read the response and buffer it. 
		buffer.append(String.format("%s", commentListAll.toPrettyString()));
		
		// Display comments information.
		System.out.println(buffer.toString());
		
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		// Store header information into the buffer.
		buffer.append(String.format("%n=== " + "Display partial comments info for the file: %s whose MIME is: %s"  + " ====%n", file.getName(), file.getMimeType()));	
		buffer.append(Utility.newline);
		// Get the comments for the specified doc file.
	    CommentList commentList = comments.list(fileID)
	    	// Set the number of comments to return.
	        .setPageSize(numberOfComments)
	        // Set the fields to return.
	        .setFields("comments(htmlContent, author, createdTime)")
	        .execute();
   
	    // Display the requested comments for the specified  file.
	    for(Comment comment: commentList.getComments()) {
	    	DateTime createdTime = comment.getCreatedTime();
	    	buffer.append(String.format("Content: %s Author: %s Time: %s  %n", 
	    			comment.getHtmlContent(), comment.getAuthor().getDisplayName(), createdTime));
	    	
	    }	  
		
	    // Display comments information.
	 	System.out.println(buffer.toString());
	}	 
	
	
	/**
	 * Gets Drive information using the About resource. 
	 * @throws IOException Error issued by the resource. 
	 */
	public static void driveInfo() throws IOException {
		 
		// Get the About accessor (collection of methods).
		About aboutResource = authenticatedClient.about();
		
		// Get the About object that specifies how to parse/serialize JSON 
		// that is transmitted over HTTP when working with the Drive API. 
		com.google.api.services.drive.model.About response = aboutResource.get()		
		.setPrettyPrint(true)
	  
		/* To reduce the response size, get only specific fields using the 
		 * following syntax: .setFields("user, storageQuota, importFormats, exportFormats").
		 * You get the names of the fields (and capitalization) from the resource 
		 * representation. Please, refer to <a href="https://developers.google.com/drive/v3/reference/about#resource" target="_blank">About</a>. 
		 * We are going to ask for all applicable fields.
		 */
		.setFields("*")
		.setPrettyPrint(true)
		.execute();
	
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		
		buffer.append(String.format("%n==== " + "About Drive Display All" + " ==== %n"));
		buffer.append(Utility.newline);
		buffer.append(String.format("%s", response.toPrettyString()));
		// Display About Drive information.
		System.out.println(buffer.toString());
		
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		
		// Get user information,
		buffer.append(String.format("%n=== " + "Display user info" + " ==== %n"));
		buffer.append(String.format("User: %s %n", response.getUser().getDisplayName()));
		buffer.append(String.format("Email: %s %n", response.getUser().getEmailAddress()));
		buffer.append(String.format("PermissionID: %s %n", response.getUser().getPermissionId()));
		buffer.append(String.format("Kind: %s %n", response.getUser().getKind()));
		buffer.append(String.format("Photo: %s %n", response.getUser().getPhotoLink()));
		
		// Display user information.
		System.out.println(buffer.toString());
		
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		buffer.append(String.format("%n==== " + "Display storage quota info" + " ==== %n"));
		// Get user's storage quota limits and usage. All fields are measured in bytes.
		String storageQuota = response.getStorageQuota().toPrettyString();
		buffer.append(String.format("%s %n",storageQuota));		
		// Display storage quota information.
		System.out.println(buffer.toString());
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		buffer.append(String.format("%n==== " + "Export Formats" + " ==== %n"));
		buffer.append(Utility.newline);
				
		// Get storage export formats.
		Map<String, List<String>> storageExportFormats = response.getExportFormats();
		
		for (Map.Entry<String, List<String>> me : storageExportFormats.entrySet()) {
			  String key = me.getKey();
			  buffer.append(String.format("%n %s : ", key));
			  List<String> valueList = me.getValue();
			  String values = "";
			  for (String s : valueList) {
				  values += " " + s + " ";
			  }
			  
			  buffer.append(String.format("[%s]", values));
		}
		// Display import formats information.
		System.out.println(buffer.toString());
		
		
		// Clear buffer of previous content.
		buffer.delete(0, buffer.length());
		buffer.append(String.format("%n=== " + "Import Formats" + " === %n"));	
		buffer.append(Utility.newline);
		
		// Get storage import formats.
		Map<String, List<String>> storageImportFormats = response.getImportFormats();
		
		for (Map.Entry<String, List<String>> me : storageImportFormats.entrySet()) {
			  String key = me.getKey();
			  buffer.append(String.format("%n %s : ", key));
			  List<String> valueList = me.getValue();
			  String values = "";
			  for (String s : valueList) {
				  values += " " + s + " ";
			  }
			  
			  buffer.append(String.format("[%s]", values));
		}
		// Display import formats information.
		System.out.println(buffer.toString());
						
	}
}
