package com.chenzeyi.util.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

/**
 * Tar 归档工具
 * @author zeyi.chen
 *
 */
public class TarUtil {
	private static void TarFile(File fileToTar, String Tarpath, TarOutputStream tos) {
		FileInputStream fis = null;
		TarEntry tarEntry = null;
		try {
			fis = new FileInputStream(fileToTar);
			tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);//设置长文件名格式|使用长文件名的设置缺点是在其他非gnu操作系统上可能无法解压
			tarEntry = new TarEntry(Tarpath);
			tarEntry.setSize(fileToTar.length());//设置文件字节长度
			tos.putNextEntry(tarEntry);
			byte[] b = new byte[1024];
			int size = -1;
			while ((size = fis.read(b)) != -1)
				tos.write(b, 0, size);
			tos.closeEntry();//
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (fis != null||tos!=null){
					fis.close();
					tos.flush();
					tos.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void TarFolder(File folder, String Tarpath, TarOutputStream tos) {
		String[] subFn = folder.list();
		for (int i = 0; i < subFn.length; i++) {
			File subFile = new File(folder.getPath() + "/" + subFn[i]);
			if (subFile.isFile()) {
				TarFile(subFile, Tarpath + "/" + subFile.getName(), tos);
			} else
				TarFolder(subFile, Tarpath + "/" + subFile.getName(), tos);
		}
	}

	private static void Tar(String[] files, TarOutputStream tos) throws Exception {
		for(String file:files){
			File fileStart = new File(file);
			if (fileStart.isFile()) {
				TarFile(fileStart, fileStart.getName(), tos);
			} else{
				TarFolder(fileStart, fileStart.getName(), tos);
			}
		}
	}

	public static boolean Tar(String resource, String target) {
		
		return Tar(new String[] { resource }, target);
	}

	public static boolean Tar(String[] resource, String target) {
		long _st = System.currentTimeMillis();
		TarOutputStream tos = null;
		try {
			File file = new File(target);
			if (!(file = new File(file.getParent())).exists()) {
				file.mkdirs();
			}
			tos = new TarOutputStream(new FileOutputStream(target));
			Tar(resource, tos);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (tos != null)
				try {
					tos.flush();
					tos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println(System.currentTimeMillis()-_st);
		return true;
	}

	public static boolean unTar(String resource, String target) {
		TarInputStream in = null;
		try {
			in = new TarInputStream(new FileInputStream(resource));
			TarEntry t = null;
			while ((t = in.getNextEntry()) != null)
				if (t.isDirectory()) {
					File f = new File(target + "/" + t.getName() + "/");
					f.mkdirs();
				} else {
					File f = new File(target + "/" + t.getName());
					File folder = null;
					if (!(folder = new File(f.getParent())).exists())
						folder.mkdirs();
					OutputStream out = null;
					try {
						out = new FileOutputStream(f);
						byte[] b = new byte[1024];
						int size = -1;
						while ((size = in.read(b)) != -1)
							out.write(b, 0, size);
					} finally {
						if (out != null)
							out.close();
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return true;
	}

 	public static void main(String[] strings) {
		try {
			Tar("D:\\PrintFile\\", "D:/BackUp/TaskFile1141.Tar");
//			unTar("D:/BackUp/TaskFile1141.Tar","D:/");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}