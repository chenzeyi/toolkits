package com.chenzeyi.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;
/**
 * 压缩文件(不支持文件夹归档)为xz
 * 测试:PC端 cpu 50%
 * @author zeyi.chen
 *
 */
public class XzUtil {
	private static void xzipFile(File fileToZip, String zippath, XZOutputStream xzos) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileToZip);
			byte[] b = new byte[1024];
			int size = -1;
			while ((size = fis.read(b)) != -1)
				xzos.write(b, 0, size);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void xzip(String[] files, XZOutputStream xzos) throws Exception {
		for(String file:files){
			File fileStart = new File(file);
			if (fileStart.isFile()) {
				xzipFile(fileStart, fileStart.getName(), xzos);
			} else{
				throw new Exception("xz压缩对象必须是文件");
			}
		}
	}
	
	public static boolean xzip(String resource, String target) {
		return xzip(new String[] { resource }, target);
	}

	public static boolean xzip(String[] resource, String target) {
		System.out.println("xz压缩文件");
		long _st = System.currentTimeMillis();
		XZOutputStream xzos = null;
		try {
			File file = new File(target);
			if (!(file = new File(file.getParent())).exists()) {
				file.mkdirs();
			}
//			Getting an output stream to compress with LZMA2 using the default settings and the default integrity check type (CRC64):
			xzos = new XZOutputStream(new FileOutputStream(target),new LZMA2Options());//
			
//			Using the preset level 8 for LZMA2 (the default is 6) and SHA-256 instead of CRC64 for integrity checking:
//			xzos = new XZOutputStream(new FileOutputStream(target), new LZMA2Options(8),XZ.CHECK_SHA256);
			
//			Using the x86 BCJ filter together with LZMA2 to compress x86 executables and printing the memory usage information before creating the XZOutputStream:
//			FilterOptions[] options = { new X86Options(), new LZMA2Options() };
//			xzos = new XZOutputStream(new FileOutputStream(target), options);
			xzip(resource, xzos);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (xzos != null)
				try {
					xzos.flush();
					xzos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println("xz压缩文件耗时"+(System.currentTimeMillis()-_st));
		return true;
	}

	public static void main(String[] args) {
		xzip("D:/BackUp/TaskFile1141.Tar","D:/BackUp/TaskFile1141.Tar.xz");
	}

}
