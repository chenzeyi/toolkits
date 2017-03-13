package com.chenzeyi.util.QrCoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode.BarcodeGenerator;
import org.krysalis.barcode.BarcodeUtil;
import org.krysalis.barcode.impl.Code39;
import org.krysalis.barcode.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode.tools.MimeTypes;
import org.krysalis.barcode.tools.UnitConv;
/**
 * http://blog.csdn.net/szwangdf/article/details/46506427
 * @author zeyi.chen
 *
 */
public class Bc4JUtil {
	private static final int dpi = 150;//分辨率
    private static final String FORMAT = MimeTypes.MIME_JPEG;  
    private static final int ORIENTATION = 0;  
    private static final int RESOLUTION = 300;  
    private static final String BARCODE_TYPE = "datamatrix";  
    
	public static void generateQrCode(String qrMsg, String imgPath){
		
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		BitmapCanvasProvider bitmap = null;
		FileOutputStream fos = null;
		try {
			DefaultConfiguration cfg = new DefaultConfiguration("barcode");
			DefaultConfiguration child = new DefaultConfiguration(BARCODE_TYPE);
			cfg.addChild(child);
			DefaultConfiguration attr;
			attr = new DefaultConfiguration("height");
			child.addChild(attr);
			attr = new DefaultConfiguration("module-width");
			attr.setValue("0.6");
			child.addChild(attr);
			BarcodeUtil util = BarcodeUtil.getInstance();
			BarcodeGenerator gen = util.createBarcodeGenerator(cfg,null);
			baos = new ByteArrayOutputStream(4096);
			bitmap = new BitmapCanvasProvider(baos, FORMAT, RESOLUTION, BufferedImage.TYPE_BYTE_GRAY, true);
			gen.generateBarcode(bitmap, qrMsg);
			bitmap.finish();
			is = new ByteArrayInputStream(baos.toByteArray());
			fos = new FileOutputStream(imgPath);
			byte[] b = new byte[1024];
			while((is.read(b)) != -1){
				fos.write(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				fos.close();
				if (baos != null) {
					baos.close();
				}
				bitmap = null;
			} catch (Exception e) {
			}
		}
	}
	/**
	 * 
	 * @param bcMsg
	 * @param imgPath
	 * @throws IOException
	 */
	public static void generateBarCode(String bcMsg,String imgPath) throws IOException{
        Code39 bean = new Code39();
        bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));//竖线的宽度
        bean.doQuietZone(false);
        File outputFile = new File(imgPath);
        OutputStream out = new FileOutputStream(outputFile);
        try {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false);
            bean.generateBarcode(canvas, bcMsg);
            canvas.finish();
        } finally {
            out.close();
        }
	}
	
	
}
