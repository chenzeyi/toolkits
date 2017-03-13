package com.chenzeyi.util.QrCoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZxingUtil {
	/**
	 * 生成条形码
	 * @param contents
	 * @param width
	 * @param height
	 * @param imgPath
	 */
	public static void BarcodeEncoder(String contents, int width, int height, String imgPath) {    
        int codeWidth = 3 + // start guard    
                (7 * 6) + // left bars    
                5 + // middle guard    
                (7 * 6) + // right bars    
                3; // end guard    
        codeWidth = Math.max(codeWidth, width);    
        try {    
        	BitMatrix  byteMatrix = new MultiFormatWriter().encode(contents,BarcodeFormat.EAN_13, codeWidth, height, null); 
            File imgFile = new File(imgPath);
            MatrixToImageWriter.writeToFile(byteMatrix, "png", imgFile);
    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
    }    
    /**
     * 解析条形码
     * @param imgPath
     * @return
     */
    public static String BarcodeDecoder(String imgPath) {    
        BufferedImage image = null;    
        Result result = null;    
        try {    
            image = ImageIO.read(new File(imgPath));    
            if (image == null) {    
                System.out.println("the decode image may be not exit.");    
            }    
            LuminanceSource source = new BufferedImageLuminanceSource(image);    
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));    
            result = new MultiFormatReader().decode(bitmap, null);    
            return result.getText();    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return null;    
    }  
      
    
    /**  
     * 编码  
     * @param contents  
     * @param width  
     * @param height  
     * @param imgPath  
     */  
    public static void encode(String contents, int width, int height, String imgPath) {    
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();    
        // 指定纠错等级    
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);    
        // 指定编码格式    
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");    
        try {    
        	BitMatrix  byteMatrix = new MultiFormatWriter().encode(contents,BarcodeFormat.QR_CODE, width, height, hints);    
            File imgpath = new File(imgPath);  
            MatrixToImageWriter.writeToFile(byteMatrix, "png",imgpath);    
        } catch (Exception e) {    
            e.printStackTrace();    
        }  
    }  
    
    public static String decode(String imgPath) {    
        BufferedImage image = null;    
        Result result = null;    
        try {    
            image = ImageIO.read(new File(imgPath));    
            if (image == null) {    
                System.out.println("the decode image may be not exit.");    
            }    
            LuminanceSource source = new BufferedImageLuminanceSource(image);    
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));    
    
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();   
            hints.put(DecodeHintType.OTHER, "UTF-8");    
    
            result = new MultiFormatReader().decode(bitmap, hints);    
            return result.getText();    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return null;    
    }  
    /**  
     * @param args  
     */    
    public static void main(String[] args) {  
          
//        String imgPath = "d:/123.jpg";    
//        String contents = "6943620593115";    
//        int width = 105, height = 50;    
//        BarcodeEncoder(contents, width, height, imgPath);  
//          
//        String imgPath2 = "d:/123.jpg";    
//        String decodeContent = BarcodeDecoder(imgPath2);    
//        System.out.println("解码内容如下：");    
//        System.out.println(decodeContent);    
        
        String imgdPath = "d:/michael_zxing.png";    
        String condtents = "http://www.paihang.com";    
        int widtdh = 300;
        int height1 = 300;    
        encode(condtents, widtdh, height1, imgdPath);  
          
        String imgPdath2 = "d:/michael_zxing.png";    
        String decodeCdontent = decode(imgPdath2);    
        System.out.println("解码内容如下：");    
        System.out.println(decodeCdontent);    
    }  
}
