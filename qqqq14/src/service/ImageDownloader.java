package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {
	public static boolean downloadImage(String filePath, String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			try {
				URLConnection uc = url.openConnection();
				InputStream is = uc.getInputStream();
				File file = new File(filePath);
				FileOutputStream fos = new FileOutputStream(file);
				int i = 0;
				while ((i = is.read()) != -1) {
					fos.write(i);
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
