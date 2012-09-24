package com.passgen.passwordgenrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DataAccess {
	
	
	ArrayList<String> tldNamesArray;

	/***
	 * Constructor. Called for DataAccess. Sets the TldNames array from the text list inputStream.
	 * @param inputStream
	 */
	public DataAccess(InputStream tldNameStream)
	{
		this.setTldNamesArray(tldNameStream);
	}
	
	/***
	 * Method to set the tldNames array from the input Stream
	 * @param inputStream
	 */
	private void setTldNamesArray(InputStream tldNameStream) {
		
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(tldNameStream));
		tldNamesArray = new ArrayList<String>();
		
		String readLine = null;
		
		try {
			while((readLine = inputReader.readLine())!= null)
			{
				if(readLine.startsWith("//") || readLine.equals(" "))
				{
					//Log.d("readLine}", readLine);
					continue;
				}else
				{
					//Log.d("readLine", readLine);
					this.tldNamesArray.add(readLine);
				}
			}
		} catch (IOException e) {
			this.tldNamesArray.add("error");
		}
	}
	/***
	 * Method to search a particular tldNAme from the list. i.e co.in
	 * @param query
	 * @return boolean
	 */
	public boolean searchTldNames(String query)
	{
		if(!tldNamesArray.contains("error"))
		{
			for(String tldName : this.tldNamesArray)
			{
				if(query.equals(tldName))
					return true;
			}
		}
		
		return false;
	}
	

}
