/** 
 * LEGAL: Use and Disclaimer. 
 * This software belongs to the owner of the http://www.acloudysky.com site and supports the
 * examples described there. 
 * Unless required by applicable law or agreed to in writing, this software is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * Please, use the software accordingly and provide the proper acknowledgement to the author.
 * @author milexm@gmail.com  
 **/
package com.acloudysky.drive;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.acloudysky.utilities.Utility;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;


/*** 
 * Reads and writes the service client default settings from the related JSON file.
 * The file contains information such as project ID, default file names and so on.
 * It extends <a href="https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.20.0/com/google/api/client/json/GenericJson" target="_blank">GenericJson class</a>
 * whose Subclasses can declare fields for known data keys using the Key annotation. 
 * Each field can be of any visibility (private, package private, protected, or public) and must not be static. 
 * The following is the JSON formatted information:
 * <pre>
 * "email" : "your e-mail",
 * "folder" : "TestDriveApi",
 * "folderID" : "",
 * "mimeType" : "image/jpeg",
 * "prefix" : "your alias",
 * "project" : "your project ID",
 * "sourceImageFile" : "luigi.jpeg",
 * "sourceImageFileID" : "",
 * "sourceTextFile" : "midsummereve.txt",
 * "sourceTextFileID" : "",
 * "sourceDocFile" : "midsummereve.docx",
 * "sourceDocFileID" : "",
 * "sourcePresFile" : "midsummereve.pptx",
 * "sourcePresFileID" : "",
 * "domain" : "your domain name"
 * </pre>
 * @author Michael
 * * <b>Notes</b>
 * <ul>
 * 	<li>The folderID will be updated by the application after it creates the TestDriveApi folder in the Drive root directory.</li>
 *  <li>The sourceTextFileID and sourceImageFileID will be updated by the application after it uploads the midsummereve.txt and luigi.jpeg, respectively.
 *  The reasons for these 3 IDs is to avoid to recreate the folder and the files. This is to simplify the application; obviusely something that you do not
 *  want to do in real life where you might want to track the related versions. To start fresh, manually delete the TestDriveApi folder and the IDs in the
 *  client_defaults.json file.</li>
 * </ul>
 */
public class DriveDefaultSettings extends GenericJson {
	
	// Google services local directories.
	final static String SERVICESDIR = ".googleservices";
	final static String DATADIR = "drive";
	// Client default settings file name. 
	final static String DEFAULTSFILE = "client_defaults.json";
		
	// Common defaults.
	@Key("project")
	private String project;
	
	@Key("prefix")
	private String prefix;

	@Key("email")
	private String email;

	@Key("defaultdomain")
	private String domain;

	
	// Drive defaults.
	@Key("sourceTextFileID")
	private String sourceTextFileID;

	@Key("sourceTextFile")
	private String sourceTextFile;
	
	@Key("sourceImageFileID")
	private String sourceImageFileID;

	@Key("sourceImageFile")
	private String sourceImageFile;
		
	@Key("folderID")
	private String folderID;

	@Key("folder")
	private String folder;
	
	@Key("mimeType")
	private String mime;

	@Key("sourceDocFileID")
	private String sourceDocFileID;

	@Key("sourceDocFile")
	private String sourceDocFile;
	
	@Key("sourceSpreadFileID")
	private String sourceSpreadFileID;

	@Key("sourceSpreadFile")
	private String sourceSpreadFile;
	
	@Key("sourcePresFileID")
	private String sourcePresFileID;

	@Key("sourcePresFile")
	private String sourcePresFile;
	
	// Setters and getters. 
	public String getProject() {
		return project;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getEmail() {
		return email;
	}

	public String getDomain() {
		return domain;
	}
	
	public String getMimeType() {
		return mime;
	}
	
	
	// Text files
	public String getSourceTextFileID() {
		return sourceTextFileID;
	}

	public void setSourceTextFileID(String fileID) {
		sourceTextFileID = fileID;
	}
	
	public String getSourceTextFile() {
		return sourceTextFile;
	}
	
	// Image files
	public String getSourceImageFileID() {
		return sourceImageFileID;
	}

	public void setSourceImageFileID(String fileID) {
		sourceImageFileID = fileID;
	}

	public String getSourceImageFile() {
		return sourceImageFile;
	}
	
	
	// Doc files
	public String getSourceDocFileID() {
		return sourceDocFileID;
	}

	public void setSourceDocFileID(String fileID) {
		sourceDocFileID = fileID;
	}


	public String getSourceDocFile() {
		return sourceDocFile;
	}
	
	
	// Presentation files
	public void setSourcePresFileID(String fileID) {
		sourcePresFileID = fileID;
	}
	
	public String getSourcPresFileID() {
		return sourcePresFileID;
	}

	public String getSourcePresFile() {
		return sourcePresFile;
	}
	
	
	// Spreadsheet files
	public void setSourceSpreadFileID(String fileID) {
		sourceSpreadFileID = fileID;
	}
	
	public String getSourceSpreadFileID() {
		return sourceSpreadFileID;
	}

	public String getSourceSpreadFile() {
		return sourceSpreadFile;
	}
		
	// Parent test folder 
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String name) {
		folder = name;
	}

	public String getFolderID() {
		return folderID;
	}

	public void setFolderID(String folderID) {
		this.folderID = folderID;
	}

	
	/**
	 * Keep it to initialize parent class. 
	 */
	public DriveDefaultSettings() {
		
	}
	
	
	/***
	 * Reads sample settings contained in the supporting <i>client_defaults.json</i> file.
	 * 	<b>Note</b>. This method uses {@link com.google.api.client.json.JsonFactory} to create
	 * a DriveDefaultSettings object to read the JSON formatted information.
	 * @return The DriveDefaultSettings object
	 */
	public DriveDefaultSettings readSettings() {
		
		// Instance of the JSON factory. 
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		DriveDefaultSettings settings = null;
		
		// Load application default settings from the "client_settings.json" file
		String filePath = Utility.getAbsoluteFilePath(SERVICESDIR, DATADIR, DEFAULTSFILE);
		
		try {
				InputStream inputStream = new FileInputStream(filePath);
				// Create settings object to access the default settings.
				settings = jsonFactory.fromInputStream(inputStream, DriveDefaultSettings.class);
				if (Utility.isDEBUG()) 
					System.out.println(settings.toPrettyString());
	      } catch (IOException e) {
	        String msg = String.format("Error occurred; %s", e.getMessage());
	        System.out.println(msg);
	      }
		if (settings.getProject().startsWith("Enter ")) {
			System.out.println("Enter sample settings info in "
					+ DEFAULTSFILE);
			System.exit(1);
		}
		return settings;
	}

	
	/**
	 * Update the value of the specified key in the client_defaults.json file. 
	 * Note: Remember to delete the test folder when you are done with the application. 
	 * Also assign an empty string to to the folder and file IDs in the cient_defaults.json file.  
	 * @param key The key identifying the setting.
	 * @param value The value associated with the key.
	 */
	public void updateDefaultSettings(String key, String value) {
		
		// Instance of the JSON factory. 
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		DriveDefaultSettings settings = null;
		
		// Load application default settings from the "client_settings.json" file
		String filePath = Utility.getAbsoluteFilePath(SERVICESDIR, DATADIR, DEFAULTSFILE);
		
		try {
			InputStream inputStream = new FileInputStream(filePath);
			// Create settings object to access the default settings.
			settings = jsonFactory.fromInputStream(inputStream, DriveDefaultSettings.class);
			settings.put(key, value);
		}
		catch (IOException e) {
			String msg = String.format("Error occurred; %s", e.getMessage());
			System.out.println(msg);
		}
		
		try (FileWriter file = new FileWriter(filePath)) {

            file.write(settings.toPrettyString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
	
	}

	

} 

