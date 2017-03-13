package com.chenzeyi.util.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImageUtil {

	
	/**
	 * 图片生成base64
	 * @param imgPath
	 * @return
	 */
	public static String imageToBase64(String imgPath) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgPath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);
	}
	
	/**
	 * base64生成图片
	 * @param imgStr
	 * @param imgPath
	 * @return
	 */
	public static boolean base64ToImage(String imgStr,String imgPath) {
		if (imgStr == null)
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(imgPath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @param imgStr
	 * @param imgPath
	 * @return
	 */
	public static boolean base64ToImage0(String imgStr,String imgPath) {
		if (imgStr == null)
			return false;
		Decoder base64 = Base64.getDecoder();
		try {
			byte[] decoded = base64.decode(imgStr.getBytes("UTF-8"));
			OutputStream out = new FileOutputStream(imgPath);
			out.write(decoded);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String strImg = imageToBase64(new File("img/pic.png").getAbsolutePath());
		System.out.println(strImg);
		base64ToImage(strImg,new File("img/pic.jpeg").getAbsolutePath());
	}
}
