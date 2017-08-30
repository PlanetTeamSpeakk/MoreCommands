package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

public class Downloader {

	public static void downloadDependency(String url, String name) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		String fileLocation = "mods/" + name;
		if (!new File("mods/").isDirectory()) new File("mods/").mkdirs();
		if (!new File(fileLocation).exists()) {
			System.out.println("Could not find " + name + ", downloading it now...");
			Map<String, String> downloaded = new HashMap<>();
			downloaded.put("fileLocation", "");
			downloaded.put("success", "false");
			try {
				downloaded = downloadFile(url, fileLocation);
			} catch (NullPointerException | MalformedURLException e) {
				System.out.println(name + " could not be downloaded, thus MoreCommands cannot be used.");
			}
			if (!Boolean.parseBoolean(downloaded.get("success")))
				System.out.println(name + " could not be downloaded, thus MoreCommands cannot be used.");
			else
				System.out.println("Successfully downloaded " + name + ".");
		}
	}

	/**
	 *
	 * @param url The url of the file to be downloaded.
	 * @param fileLocation A string of the location where the file should be downloaded to, this must include a file suffix.
	 * @return Map<String, String> Contains keys fileLocation and success, fileLocation will contain the location where the file was downloaded to, success will be a boolean in a string which shows if the download was successful.
	 * @throws NullPointerException
	 * @throws MalformedURLException
	 */
	public static Map<String, String> downloadFile(String url, String fileLocation) throws NullPointerException, MalformedURLException {
		String[] fileLocationParts = fileLocation.split("/");
		String fileLocation2 = "";
		for (int x = 0; x < fileLocationParts.length; x += 1)
			if (x+1 != fileLocationParts.length) {
				fileLocation2 += "/" + fileLocationParts[x];
				new File(fileLocation2.substring(1)).mkdirs();
			}
		if (new File(fileLocation).exists()) fileLocation = fileLocation.split("\\.")[0] + "-1" + (fileLocation.split("\\.").length != 1 ? "." + fileLocation.split("\\.")[fileLocation.split("\\.").length-1] : "");
		while (new File(fileLocation).exists()) fileLocation = addNextDigit(fileLocation);
		java.net.URL website = null;
		try {
			website = new java.net.URL(url); // getting the URL
		} catch (MalformedURLException e) {
			throw e;
		}
		ReadableByteChannel rbc = null;
		try {
			rbc = Channels.newChannel(website.openStream()); // getting the data
		} catch (IOException e1) {
			throw new MalformedURLException("URL does not exist.");
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileLocation); // creating a new FileOutputStream
		} catch (FileNotFoundException e) {}
		try {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); // writing data to a file
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			new File(fileLocation).delete();
			throw new MalformedURLException("URL does not exist.");
		}
		Map<String, String> data = new HashMap<>();
		data.put("fileLocation", fileLocation);
		data.put("success", Boolean.toString(new File(fileLocation).exists()));
		return data;
	}

	private static String addNextDigit(String string) {
		Long digit = Long.parseLong(string.split("-")[string.split("-").length-1].split("\\.")[0]);
		digit += 1;
		return string.split("\\.")[0].split("-")[0] + "-" + digit.toString() + "." + string.split("\\.")[string.split("\\.").length-1];
	}

	public static void addJarToClasspath(String fileLocation) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new File(fileLocation).toURI().toURL());
		System.out.println("Added " + fileLocation.split("/")[fileLocation.split("/").length-1] + " to the classpath.");
	}

}
