package com.chenzeyi.util.rw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.chenzeyi.util.unicode.UnicodeReader;

public class FileUtil {
	private static final String enterKey = "\n\r";//换行
	private static final String tabKey = "\t";//tab键
	private static final String blankKey = "\b";//空格键
	private static final String pageKey = "\f";//换行符
	/**
	 * 新建目录
	 */
	public static boolean newFolder(String folderPath) throws Exception {
		File myFilePath = null;
		try {
			myFilePath = new File(folderPath);
			myFilePath.mkdirs();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 新建文件
	 */
	public static boolean newFile(String filepath) throws Exception {
		File file = new File(filepath);
		if (!file.exists()) 
		{
			String parent = file.getParent();
			parent = parent == null ? "/" : parent;
			newFolder(parent);
			file.createNewFile();
			return true;
		}
		return false;
	}

	/**
	 * 删除文件
	 */
	public static void delFile(String filename) throws Exception {
		File file = new File(filename);
		if (file.exists())
			file.delete();
	}

	/**
	 * 删除文件夹
	 */
	public static void delFolder(String folderPath) throws Exception {
		delAllFiles(folderPath); // 删除完里面所有内容
		File myFilePath = new File(folderPath);
		myFilePath.delete(); // 删除空文件夹
	}

	/**
	 * 删除文件夹里面的所有文件
	 */
	public static void delAllFiles(String path) throws Exception {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			file.delete();
			return;
		}
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isFile()) {
					f.delete();
				} else {
					delFolder(f.getAbsolutePath());
				}
			}
		}
	}
	/**
	 * 删除文件夹里面的所有文件
	 */
	public static void delAllFiles(String[] paths) throws Exception {
		for(String path:paths){
			File file = new File(path);
			if (!file.exists()) {
				return;
			}
			if (!file.isDirectory()) {
				file.delete();
				return;
			}
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isFile()) {
						f.delete();
					} else {
						delFolder(f.getAbsolutePath());
					}
				}
			}
		}
	}

	/**
	 * 复制单个文件
	 */
	public static void copyFile(String oldPath, String newPath)
			throws Exception {
		File source = new File(oldPath);
		if (!source.exists())
			throw new FileNotFoundException("未找到源文件!!!");
		newFile(newPath);
		write(oldPath, newPath);
	}

	/**
	 * 复制整个文件夹内容
	 */
	public static void copyFolder(String oldPath, String newPath)
			throws Exception {
		File source = new File(oldPath);
		if (!source.exists())
			throw new FileNotFoundException("未找到源文件夹!!!");
		newFolder(newPath);

		File[] files = source.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				copyFile(f.getAbsolutePath(), newPath + "/" + f.getName());
			}
			if (f.isDirectory()) {
				copyFolder(f.getAbsolutePath(), newPath + "/" + f.getName());
			}
		}
	}

	/**
	 * 移动文件到指定目录
	 */
	public static void move(String oldPath, String newPath) throws Exception {
		File source = new File(oldPath);
		if (!source.exists())
			throw new FileNotFoundException("未找到源文件/文件夹!!!");
		if (source.isFile()) {
			copyFile(oldPath, newPath);
			delFile(oldPath);
		} else {
			copyFolder(oldPath, newPath);
			delFolder(oldPath);
		}
	}

	public static void write(String source, String target) throws IOException {
		write(new File(source), new File(target));
	}

	public static void write(File source, String target) throws IOException {
		write(source, new File(target));
	}

	public static void write(String source, File target) throws IOException {
		write(new File(source), target);
	}

	public static void write(File source, File target) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(source);
			write(in, target);
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
		}

	}

	public static void write(String source, OutputStream out)
			throws IOException {
		write(new File(source), out);
	}

	public static void write(InputStream io, String target) throws IOException {
		write(io, new File(target));
	}

	public static void write(InputStream io, File target) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(target);
			write(io, out);
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null)
				out.close();
		}
	}

	public static void write(File source, OutputStream out) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(source);
			write(in, out);
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
		}
	}

	public static void write(InputStream io, OutputStream out)
			throws IOException {
		byte[] b = new byte[1024];
		for (int size = -1; (size = io.read(b)) > 0;) {
			out.write(b, 0, size);
		}
	}

	/**
	 * 以指定的字符集写文件
	 * 
	 * @param path
	 *            路径
	 * @param fileName
	 *            文件名称
	 * @param content
	 *            要写的内容
	 * @param charset
	 *            字符集名称
	 * @throws Exception
	 */
	public static void writeFileWithCharset(String path, String fileName,
			String content, String charset) throws Exception {
		File file = new File(path);
		if (!file.exists()) {
			FileUtil.newFolder(path);
		}
		String fullPath = (path + "/" + fileName).replaceAll("\\\\", "/")
				.replaceAll("//", "/");
		file = new File(fullPath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream,
					charset);
			bufferedWriter = new BufferedWriter(outputStreamWriter);
			bufferedWriter.write(content);
		} finally {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}

	/**
	 * 获取文件最后一行
	 * 
	 * @param file
	 * @param charset
	 *            字符集
	 * @return
	 * @throws IOException
	 */
	public static String getFileLastLineWithCharset(File file, String charset)
			throws IOException {
		String lastLine = "";
		FileInputStream in = null;
		BufferedReader br = null;
		try
		{
			 in = new FileInputStream(file);  
	         br = new BufferedReader(new UnicodeReader(in, charset));  
	         
			String line;
			while ((line = br.readLine()) != null) {
				lastLine = line;
			}
		} finally {
			
			if (in != null) {
				in.close();
			}
			if (br != null) {
				br.close();
			}
		}
		return lastLine;
	}

	/**
	 * 去除指定文件的最后一行
	 * 
	 * @param path
	 * @param charset
	 * @throws Exception
	 */
	public static void deleteFileLastLine(String filePath, String charset)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！");
		}
		List<String> list = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		String content = "";
		int size = list.size() - 1;
		for (int i = 0; i < size; i++) {
			content = content + list.get(i) + "\r\n";
		}
		writeFileWithCharset(
				file.getAbsolutePath().replace(file.getName(), ""),
				file.getName(), content, charset);
	}
	
	/**
	 * 去除指定文件的最后n行
	 * 
	 * @param path
	 * @param charset
	 * @param num(num > 0)
	 * @throws Exception
	 */
	public static void deleteFileTailLine(String filePath, String charset,int num)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！");
		}
		List<String> list = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		String content = "";
		int size = list.size() - num;
		for (int i = 0; i < size; i++) {
			content = content + list.get(i) + "\r\n";
		}
		writeFileWithCharset(
				file.getAbsolutePath().replace(file.getName(), ""),
				file.getName(), content, charset);
	}
	/**
	 * 增加指定文件的最后n行
	 * 
	 * @param path
	 * @param charset
	 * @param num(num > 0)
	 * @throws Exception
	 */
	public static void addFileTailLine(String filePath, String charset,String linecontent)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！");
		}
		List<String> list = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		String content = "";
		for (int i = 0; i < list.size(); i++) {
			content = content + list.get(i) + "\r\n";
		}
		content = content+linecontent;//
		writeFileWithCharset(
				file.getAbsolutePath().replace(file.getName(), ""),
				file.getName(), content, charset);
	}
	/**
	 * @param filePath
	 * @param charset
	 * @throws Exception
	 */
	public static List<String> getPagedFileLines(String filePath, String charset) throws Exception {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！");
		}
		List<String> pageList = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			StringBuilder tempSb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				if("".equals(line)){//分页符分页
					pageList.add(tempSb.toString());
					tempSb.delete(0, tempSb.length());//清空
				}else{
					tempSb.append(line);
					tempSb.append("\r\n");
				}
			}
			pageList.add(tempSb.toString());
			tempSb.delete(0, tempSb.length());//清空
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return pageList;
	}
	/**
	 * 打印文件分割
	 * 
	 * @param fileName
	 *            要分隔的文件名称，绝对路径
	 * @param taskFilePath
	 *            要分发至的目录
	 * @param charset
	 *            字符集 UTF-8,GBK等
	 * @return
	 * @throws Exception
	 */
	public static List<String> splitPrintFile(String fileName, String taskFilePath,
			String charset) throws Exception {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！" +file.getAbsolutePath());
		}
		String nameString = file.getName();
		List<String> list = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try
		{
			int index = 1;
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
			taskFilePath = taskFilePath.replaceAll("\\\\", "/");
			while ((line = bufferedReader.readLine()) != null)
			{
				String newNameString = nameString  + index;
				String newPath = (taskFilePath + "/" + dateString).replaceAll("//", "/");
				writeFileWithCharset(newPath, newNameString, line, charset);
				String subFileName = (newPath+"/"+newNameString).replaceAll("//", "/");
				list.add(subFileName);
				index++;
			}
		} 
		finally 
		{
			if (bufferedReader != null) 
			{
				bufferedReader.close();
			}
			if (inputStreamReader != null)
			{
				inputStreamReader.close();
			}
			if (fileInputStream != null) 
			{
				fileInputStream.close();
			}
		}
		return list;
	}
	
	/**
	 * 打印文件分割
	 * 
	 * @param fileName
	 *            要分隔的文件名称，绝对路径
	 * @param taskFilePath
	 *            要分发至的目录
	 * @param charset
	 *            字符集 UTF-8,GBK等
	 * @return
	 * @throws Exception
	 */
	public static List<String> splitFile(String fileName, String taskFilePath,
			String charset) throws Exception {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new Exception("指定的文件不存在！" +file.getAbsolutePath());
		}
		String nameString = file.getName();
		List<String> list = new ArrayList<String>();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try
		{
			int index = 1;
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charset);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
			taskFilePath = taskFilePath.replaceAll("\\\\", "/");
			while ((line = bufferedReader.readLine()) != null)
			{
				String newNameString = nameString  + index;
				String newPath = (taskFilePath + "/" + dateString).replaceAll("//", "/");
				writeFileWithCharset(newPath, newNameString, line, charset);
				String subFileName = (newPath+"/"+newNameString).replaceAll("//", "/");
				list.add(subFileName);
				index++;
			}
		} 
		finally 
		{
			if (bufferedReader != null) 
			{
				bufferedReader.close();
			}
			if (inputStreamReader != null)
			{
				inputStreamReader.close();
			}
			if (fileInputStream != null) 
			{
				fileInputStream.close();
			}
		}
		return list;
	}
	
	/**
	 * 获取文件行内容列表
	 * @param file
	 * @param charset
	 * @return
	 * @throws IOException 
	 */
	public static List<String> getFileAllLineWithCharset(File file, String charset) throws IOException
	{		
		List<String> list = new ArrayList<String>();
		
		FileInputStream in = null;
		BufferedReader br = null;
		
		try
		{
			 in = new FileInputStream(file);  
	         br = new BufferedReader(new UnicodeReader(in, charset));  
	         
			String line;
			while ((line = br.readLine()) != null)
			{
				list.add(line);
			}
		} 
		finally 
		{
			if (in != null) 
			{
				in.close();
			}
			if (br != null) 
			{
				br.close();
			}
		}
		return list;
	}
	/**
	 * 去除拆解的页中的要素行
	 * 拼接成带分页符的
	 * @param targetFolder
	 * @param fileName
	 * @param pages
	 * @throws Exception 
	 */
	public static void combineStrWithPaging(String targetFolder,String fileName,List<String> pages,String charset) throws Exception{
		StringBuilder sb = new StringBuilder();
		for(String page:pages){
			InputStream is = new ByteArrayInputStream(page.getBytes(charset));
			BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
			StringBuilder b = new StringBuilder();
			String line;
			while((line=br.readLine())!=null) {
				if(!line.contains("|")) {
					b.append(line);
					b.append("\r\n");
				}else{
					System.out.println("遇到尾行不记入  "+line);
				}
			}
			sb.append(b);
			sb.append("\f");
		}
		writeFileWithCharset(targetFolder, fileName, sb.toString(), charset);
	}
	public static void main(String[] s){
		try {
			addFileTailLine("E:/STMT.xml","UTF-8","sssssssssssssssssssss");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
