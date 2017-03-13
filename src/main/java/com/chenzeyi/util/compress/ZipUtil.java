package com.chenzeyi.util.compress;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	private static void zipFile(File fileToZip, String zippath, ZipOutputStream zos) {
		FileInputStream fis = null;
		try {
			// zos.setLevel(9);//0~9压缩级别【默认6】
			zos.putNextEntry(new org.apache.tools.zip.ZipEntry(zippath));
			fis = new FileInputStream(fileToZip);
			byte[] b = new byte[1024];
			int size = -1;
			while ((size = fis.read(b)) != -1)
				zos.write(b, 0, size);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (fis != null||zos!=null){
					fis.close();
					zos.flush();
					zos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void zipFolder(File folder, String zippath, ZipOutputStream zos) {
		String[] subFn = folder.list();
		for(String sub:subFn){
			File subFile = new File(folder.getPath() + "/" + sub);
			if (subFile.isFile()) {
				zipFile(subFile, zippath + "/" + subFile.getName(), zos);
			} else
				zipFolder(subFile, zippath + "/" + subFile.getName(), zos);
		}
	}

	private static void zip(String[] files, ZipOutputStream zos) throws Exception {
		for(String file:files){
			File fileStart = new File(file);
			if (fileStart.isFile()) {
				zipFile(fileStart, fileStart.getName(), zos);
			} else
				zipFolder(fileStart, fileStart.getName(), zos);
		}
	}

	public static boolean zip(String resource, String target) {
		return zip(new String[] { resource }, target);
	}

	public static boolean zip(String[] resource, String target) {
		ZipOutputStream zos = null;
		try {
			File file = new File(target);
			if (!(file = new File(file.getParent())).exists()) {
				file.mkdirs();
			}
			zos = new ZipOutputStream(new FileOutputStream(target));
			zip(resource, zos);

			zos.setEncoding("GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (zos != null)
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return true;
	}

	public static boolean unzip(String resource, String target) {
		ZipInputStream in = null;
		try {
			in = new ZipInputStream(new FileInputStream(resource));
			java.util.zip.ZipEntry z = null;
			while ((z = in.getNextEntry()) != null)
				if (z.isDirectory()) {
					File f = new File(target + "/" + z.getName() + "/");

					f.mkdirs();
				} else {
					File f = new File(target + "/" + z.getName());
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

	/**
	 * 不解压读取文件内容
	 * 
	 * @param file
	 * @param fileName
	 *            readSubZipFile("E://PrintTemplate.zip",
	 *            "PrintTemplate/FTS/FORMAT_LIB_ENT_CAN.xml");
	 * @throws Exception
	 */
	public static StringBuffer readSubContentFromZip(String zipfilePath, String innerFilePath)
			throws Exception {
		Long _start = System.currentTimeMillis();
		ZipFile zf = new ZipFile(zipfilePath);
		ZipEntry ze = zf.getEntry(innerFilePath);
		InputStream in = zf.getInputStream(ze);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		// System.out.println(result);
		in.close();
		br.close();
		System.out.println("提取文件耗时 = " + (System.currentTimeMillis() - _start));
		return sb;
	}

	/**
	 * 不解压读取文件并保存
	 * 
	 * @param zipfilePath
	 *            E://TASKFILE_BAK_20150602.zip
	 * @param saveFolder
	 *            E://TaskFile/DSB/20150123/
	 * @param innerFileEntry
	 *            TaskFile/DSB/20151229/T_CN0010001_PTMS.ENTRY.ADD_20140705_001.
	 *            dat_00001-00002
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File getSubFileFromZip(String zipfileFolder, String saveFolder,
			String innerFileEntry) throws FileNotFoundException, IOException {
		int len = 0;
		Long _start = System.currentTimeMillis();
		if(System.getProperty("os.name").contains("Windows")) innerFileEntry = innerFileEntry.replaceAll("\\\\", "/");//只支持/

		String fileName = !innerFileEntry.contains("/") ? innerFileEntry : innerFileEntry
				.substring(innerFileEntry.lastIndexOf("/"));
		File subFile = new File(saveFolder + fileName);
		if (!subFile.exists()) {// 不存在该文件再生成
			File[] zipFiles = new File(zipfileFolder).listFiles();
			ZipFile zf = null;
			ZipEntry ze = null;
			for (File zipFile : zipFiles) {
				System.out.println("在备份文件 " + zipFile.getAbsolutePath() + " 中检测文件" + innerFileEntry);
				if (zipFile.isDirectory()) {
					System.out.println("跳过文件夹");
					continue;// 如果是文件夹则跳过该次循环
				}
				zf = new ZipFile(zipFile.getAbsolutePath());
				ze = zf.getEntry(innerFileEntry);
				if (ze != null) {
					System.out.println("已找到所需文件");
					break;// 找到文件则跳出所有循环
				}
			}
			if (zf != null && ze != null) {
				InputStream read = zf.getInputStream(ze);// 读取内容
				File subFileFolder = new File(subFile.getParent());
				if (!subFileFolder.exists())
					subFileFolder.mkdirs();// 创建目录
				subFile.createNewFile();// 新建文件
				BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(subFile));
				while ((len = read.read()) != -1) {
					write.write(len);// 写内容
				}
				write.flush();
				write.close();
				read.close();
			} else {
				throw new FileNotFoundException("没有在任何压缩文件内找到需要的文件");
			}
		}
		System.out.println("提取保存文件耗时 = " + (System.currentTimeMillis() - _start));
		return subFile;
	}
	
	public static void main(String[] strings) {
		try {
			// readSubContentFromZip("E://TASKFILE_BAK_20150602.zip","TaskFile/DSB/20151229/T_CN0010001_PTMS.ENTRY.ADD_20140705_001.dat_00001-00002");
			// getSubFileFromZip("E://BackUp/","E://TaskFile/DSB/20150121/","TaskFile/DSB/20151229/T_CN0010001_PTMS.ENTRY.ADD_20140705_001.dat_00001-00002");
			zip("D:\\PrintFile\\", "D:/BackUp/TaskFile1141.zip");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}