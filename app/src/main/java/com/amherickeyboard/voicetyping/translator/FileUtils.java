package com.amherickeyboard.voicetyping.translator;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	public static final String FOLDER_NAME = "amharickeyboard";
	public static File getExternalPath(Context context) {
		return context.getExternalFilesDir("");
	}
	public static File createFolders(Context context) {
		File baseDir;
		baseDir = getExternalPath(context);
		if (baseDir == null)
			return context.getExternalFilesDir("");
		File aviaryFolder = new File(baseDir, FOLDER_NAME);
		if (aviaryFolder.exists())
			return aviaryFolder;
		if (aviaryFolder.isFile())
			aviaryFolder.delete();
		if (aviaryFolder.mkdirs())
			return aviaryFolder;
		return context.getExternalFilesDir("");
	}

	public static File genEditFile(Context context){
		return com.amherickeyboard.voicetyping.translator.FileUtils.getEmptyFile("photo"
				+ System.currentTimeMillis() + ".jpg",context);
	}

	public static File getEmptyFile(String name,Context context) {
		File folder = com.amherickeyboard.voicetyping.translator.FileUtils.createFolders(context);
		if (folder != null) {
			if (folder.exists()) {
				File file = new File(folder, name);
				return file;
			}
		}
		return null;
	}

	public static boolean isConnect(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

}
