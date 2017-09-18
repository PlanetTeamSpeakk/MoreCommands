package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class Downloader {

	public static void downloadDependency(String url, String name) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
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
	 * @throws IOException
	 */
	public static Map<String, String> downloadFile(String url, String fileLocation) throws IOException {
		String[] fileLocationParts = fileLocation.split("/");
		String fileLocation2 = "";
		for (int x = 0; x < fileLocationParts.length; x += 1)
			if (x+1 != fileLocationParts.length) {
				fileLocation2 += "/" + fileLocationParts[x];
				new File(fileLocation2.substring(1)).mkdirs();
			}
		if (new File(fileLocation).exists()) fileLocation = fileLocation.split("\\.")[0] + "-1" + (fileLocation.split("\\.").length != 1 ? "." + fileLocation.split("\\.")[fileLocation.split("\\.").length-1] : "");
		while (new File(fileLocation).exists()) fileLocation = addNextDigit(fileLocation);
		ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
		FileOutputStream fos = new FileOutputStream(fileLocation);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
		Map<String, String> data = new HashMap<>();
		data.put("fileLocation", fileLocation);
		data.put("success", Boolean.toString(new File(fileLocation).exists()));
		data.put("bytes", "" + new File(new File(fileLocation).getAbsolutePath()).length());
		return data;
	}

	private static String addNextDigit(String string) {
		Long digit = Long.parseLong(string.split("-")[string.split("-").length-1].split("\\.")[0]);
		digit += 1;
		return string.split("\\.")[0].split("-")[0] + "-" + digit.toString() + "." + string.split("\\.")[string.split("\\.").length-1];
	}

	public static Map<String, String> downloadYoutubeVideo(String url, String fileLocation) throws IOException {
		return downloadFile(getVideoLinkFromYoutubeVid(url), fileLocation);
	}

	public static String getVideoLinkFromYoutubeVid(String url) throws IOException { // this code looks like a real mess, but it works perfectly!
		if (url.contains("youtu")) {
			Gson gson = new Gson();
			String downloadUrl = "";
			String data = Reference.getHTML("http://api.w3hills.com/youtube/search?keyword=" + url + "&api_key=" + Reference.apiKeys.get("w3hills"));
			if (!data.equals("[]")) {
				Map dataMap = gson.fromJson(data, Map.class);
				Map video = (Map) ((List) dataMap.get("videos")).get(0);
				String token = (String) video.get("token");
				String data1 = Reference.getHTML("http://api.w3hills.com/youtube/get_video_info?video_id=" + url.split("v=")[1] + "&api_key=" + Reference.apiKeys.get("w3hills") + "&token=" + token);
				if (!data1.equals("{\"status\":false,\"message\":\"Unauthorised Request\"}")) {
					Map data1Map = gson.fromJson(data1, Map.class);
					Map links = (Map) data1Map.get("links");
					List<Map> videos = (List<Map>) links.get("videos");
					if (videos.isEmpty()) {
						List<Map> audios = (List<Map>) links.get("audios"); // you cannot download music videos with this api.
						for (Map audio : audios)
							if (((String) audio.get("extension")).toLowerCase().equals("mp3")) {
								downloadUrl = (String) audio.get("url");
								break;
							}
					} else
						for (Map video1 : videos)
							if (((String) video1.get("extension")).toLowerCase().equals("mp4")) {
								downloadUrl = (String) video1.get("url");
								break;
							}
					downloadUrl = downloadUrl.replaceAll(" ", "+");
				}
			}
			return downloadUrl;
		} else throw new IOException("The given URL was not a YouTube URL.");
	}

	public static Map<String, String> downloadVimeoVideo(String url, String fileLocation) throws IOException {
		return downloadFile(getVideoLinkFromVimeoVid(url), fileLocation);
	}

	public static String getVideoLinkFromVimeoVid(String url) throws IOException {
		if (url.contains("vimeo.com/")) {
			Gson gson = new Gson();
			String data = Reference.getHTML("https://player.vimeo.com/video/" + url.split("vimeo.com/")[1] + "/config");
			List<Map> videos = (List<Map>) ((Map) ((Map) gson.fromJson(data, Map.class).get("request")).get("files")).get("progressive");
			Map video = new HashMap();
			String quality = "1080p";
			boolean found = false;
			while (!found) {
				for (Map video1 : videos)
					if (video1.get("quality").equals(quality)) {
						found = true;
						video = video1;
						break;
					}
				quality = lowerQuality(quality);
			}
			if (!video.isEmpty()) return (String) video.get("url");
			else return "";
		} else throw new IOException("The given URL was not a Vimeo URL.");
	}

	public static int getWebFileSize(String url) throws IOException {
		try {
			return new URL(url).openConnection().getContentLength();
		} catch (Throwable e) {
			if (!(e instanceof IOException)) throw new IOException(e);
			else throw e;
		}
	}

	public static String formatFileSize(float bytes) {
		String output = "0 bytes";
		if (bytes/1024F/1024F >= 1F) output = bytes/1024F/1024F + " megabytes";
		else output = (long) (bytes/1024) + " kilobytes";
		return output;
	}

	public static String lowerQuality(String quality) {
		String output = "240p";
		switch (quality) {
		case "1080p": {output = "720p"; break;}
		case "720p": {output = "540p"; break;}
		case "540p": {output = "480p"; break;}
		case "480p": {output = "360p"; break;}
		default: break;
		}
		return output;
	}

	public static Map<String, String> downloadFileOrVideo(String url, String fileLocation) throws IOException {
		if (url.contains("youtu")) return downloadYoutubeVideo(url, fileLocation);
		else if (url.contains("vimeo.com/")) return downloadVimeoVideo(url, fileLocation);
		else return downloadFile(url, fileLocation);
	}

	public static String convertUrl(String url) throws IOException {
		if (url.contains("youtu")) return getVideoLinkFromYoutubeVid(url);
		else if (url.contains("vimeo")) return getVideoLinkFromVimeoVid(url);
		else return url;
	}

}
