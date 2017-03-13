package com.nolanliang.SimpleDo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;



public class FileHandler {
	
	private static final String NEW_LINE = "\n";
	
	/** saves the text to a given file 
	 * returns false if unsuccessful */
	/*
	public boolean fileSave(FileOutputStream fos, ArrayList<String> lines) {
		//OutputStreamWriter osw = new OutputStreamWriter(fos);
		String fulltext = "";
		for (int i = 0; i < lines.size(); i +=1) {
			fulltext = fulltext + NEW_LINE + lines.get(i);
		}
		if (fulltext.startsWith(NEW_LINE)) {
			fulltext = fulltext.replaceFirst(NEW_LINE, "");
		}
		try {
			fos.write(fulltext.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}*/
	
	/** Returns an array of strings from a given file */
	/*
	public ArrayList<String> fileLoad(FileInputStream fis) {		
		ArrayList<String> lines = new ArrayList<String>();
		String fulltext = "";
		
		try {
			InputStreamReader isr = new InputStreamReader(fis);
			char[] character_buffer = new char[fis.available()];
			isr.read(character_buffer);
			fulltext = new String(character_buffer);
			isr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringTokenizer tokens = new StringTokenizer(fulltext, NEW_LINE, true);
		String token = "";		
		while(tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if ( token.contentEquals("\n")) {
				lines.add("");
			} else {
				if (lines.size() < 1) { //this is needed to ensure that a null line isn't written to.
					lines.add("");
				}
				lines.set(lines.size() -1 , token.replace("\r", "")); 	    			   
			}				
		}
		return lines;
	}*/
	
	/** saves the text file to the SD card, returns true if successful */
	public boolean fileExport(String filedirectory, String filename, ArrayList<String> lines) {
		File new_directory = new File(filedirectory);
		File new_file = new File(filedirectory + filename);
		new_directory.mkdirs();
		try {
			new_file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();			
		}
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(filedirectory + filename));
			for (int i = 0; i < lines.size(); i += 1) {				
				bw.write(lines.get(i) + NEW_LINE);
			}
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/** loads a given file */
	public ArrayList<String> fileImport(String filedirectory, String filename) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filedirectory + filename));
			ArrayList<String> lines = new ArrayList<String>();
			String nextLine = br.readLine();
			while (nextLine != null) {
				lines.add(nextLine);
				nextLine = br.readLine();
			}
			br.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** returns a list of txt files within a given directory */
	public String[] listFiles(String given_directory) {
		// may need to account for null directory...
		File directory = new File(given_directory);
		String[] filelist = directory.list();
		ArrayList<String> file_array = new ArrayList<String>();
		for (int i = 0; i < filelist.length; i += 1) {
			if (filelist[i].toLowerCase().endsWith(".txt")) {
				//file_array.add(filelist[i].substring(0, filelist[i].length() - 4));
				file_array.add(filelist[i]);
			}			
		}
		filelist = new String[file_array.size()];
		file_array.toArray(filelist);
		return filelist;
	}
	
	
}
