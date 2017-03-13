package com.chenzeyi.util.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * excel文件操作类，支持2003、2007+版本, 目前只有读取的方法，如有需要可以再次添加
 * 
 * 
 */
public class ExcelUtil {

	/**
	 * 读取excel文件类容 ；取值时需自行判断每层是否不为null或empty。
	 * 
	 * @param filePath
	 *            文件的路径，含文件名
	 * @param cellNum 指定读取的列数
	 * @return 最外层的list为sheet，第二层为row，第三层为cell，例如获取第一个sheet的第一行第一列的值的方法为：String
	 *         cellValue=list.get(0).get(0).get(0);取值时需自行判断每层是否不为null或empty。
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static List<List<List<String>>> getFileContent(String filePath, int cellNum)throws Exception{
		
		if(filePath.toUpperCase().endsWith(".XLSX")){
			return XLSXCovertCSVReader.readerExcel(filePath,cellNum); 
		}else if(filePath.toUpperCase().endsWith(".XLS")){
			return readXLSFile(filePath, cellNum);
		}else{
			throw new Exception("文件格式非法，非EXCEL文件，请检查！");
		}
	}
	
	
	public static List<List<List<String>>> readXLSFile(String filePath, int cellNum)
			throws Exception {
		File file = new File(filePath);
		FileInputStream fiStream = null;
		try {
			fiStream = new FileInputStream(file);
			List<List<List<String>>> valueList = new ArrayList<List<List<String>>>();
			Workbook workbook = WorkbookFactory.create(file);
			int sheetCount = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				List<List<String>> rowList = new ArrayList<List<String>>();
				if (sheet != null) {
					int rowCount = sheet.getLastRowNum() + 1;
					for (int j = 0; j < rowCount; j++) {
						Row row = sheet.getRow(j);
						List<String> cellList = new ArrayList<String>();
						if (row != null) {
//							int cellCount = row.getLastCellNum();
//							for (int k = 0; k < cellCount; k++) {
							for (int k = 0; k < cellNum; k++) {//中原出现文件后面含有空列被读入，因此修改为指定多少列，读多少列，
								Cell cell = row.getCell(k);
								if (cell != null) {
									String valueString = getCellVaule(cell);
									cellList.add(valueString);
								} else {
									cellList.add("");
								}
							}
						}
						rowList.add(cellList);
					}
				}
				valueList.add(rowList);
			}
			return valueList;
		} finally {
			if (fiStream != null) {
				fiStream.close();
			}
		}
	}

	private static String getCellVaule(Cell cell) {
		String value = "";
		DecimalFormat df = new DecimalFormat("0");
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_NUMERIC: // 数值型
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 如果是date类型则 ，获取该cell的date值
				value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue())
						.toString();
			} else {// 纯数字
				value = String.valueOf(df.format(cell.getNumericCellValue()));
			}
			break;
		case HSSFCell.CELL_TYPE_STRING: // 字符串型
			value = cell.getRichStringCellValue().toString();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:// 公式型
			// 读公式计算值
			value = String.valueOf(df.format(cell.getNumericCellValue()));
			if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
				value = cell.getRichStringCellValue().toString();
			}
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:// 布尔
			value = " " + cell.getBooleanCellValue();
			break;
		case HSSFCell.CELL_TYPE_BLANK: // 空值
			value = "";
			break;
		case HSSFCell.CELL_TYPE_ERROR: // 故障
			value = "";
			break;
		default:
			value = cell.getRichStringCellValue().toString();
		}
		return value;
	}

	/**
	 * 写数据到excel文件
	 * 
	 * @param filePath
	 *            文件的路径，含文件名称
	 * @param dataList
	 *            要写的数据，第一层list为sheet，第二层list为row，第三层list为列数据,列数据的第一行为表头
	 * @throws IOException
	 */
	public static void writeFile(String filePath,
			List<List<List<String>>> dataList) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(new File(filePath));
			write(outputStream, dataList);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void write(OutputStream outputStream,
			List<List<List<String>>> dataList) throws IOException {
		// 初始一个workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 循环创建多个sheet
		for (int sheetIndex = 0; sheetIndex < dataList.size(); sheetIndex++) {
			HSSFSheet sheet = workbook.createSheet("sheet" + sheetIndex);
			// 创建多行
			for (int rowIndex = 0; rowIndex < dataList.get(sheetIndex).size(); rowIndex++) {
				HSSFRow row = sheet.createRow(rowIndex);
				int count = dataList.get(sheetIndex).get(rowIndex).size();
				// 创建多列
				for (int cellnum = 0; cellnum < count; cellnum++) {
					HSSFCell cell = row.createCell(cellnum);
					cell.setCellValue(dataList.get(sheetIndex).get(rowIndex)
							.get(cellnum));
				}
			}
		}
		workbook.write(outputStream);
	}

	// --------------------------------------读取excel模版填值----------------------------
	/**
	 * 写数据到excel文件
	 * 
	 * @param mastplPath
	 *            文件的路径，含文件名称
	 * @param dataList
	 *            要写的数据，第一层list为sheet，第二层list为row，第三层list为列数据,列数据的第一行为表头
	 * @throws Exception
	 */
	public static Boolean writeFileByMastpl(String mastplPath,
			List<String> dataConstval, List<List<List<String>>> dataList,
			String serSavPath/* ,Trade trade */) throws Exception {
		HSSFWorkbook workbook = null;
		FileOutputStream fOut = null;
		FileInputStream fileInputStream = null;
		try {
			if (mastplPath == null || mastplPath.trim().equals("")) {
				throw new Exception("模版路径为空！");
			}

			File file = new File(mastplPath);
			if (!file.exists()) {
				throw new Exception("模版不存在！");
			}
			// 创建对Excel工作簿文件的引用
			fileInputStream = new FileInputStream(mastplPath);
			workbook = new HSSFWorkbook(fileInputStream);
			// 设置单元格的字体、居中、边框
			HSSFFont font = workbook.createFont();
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 11);// 设置字体大小
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
			// 本例是按名引用（让我们假定那张表有着缺省名"Sheet1"）
			HSSFSheet sheet = workbook.getSheet("Sheet1");
			if (dataConstval != null && dataConstval.size() > 0) {
				fillConstva(sheet, dataConstval);
			}
			if (dataList != null && dataList.size() > 0) {
				fillList(sheet, dataList, cellStyle);
			}
			fOut = new FileOutputStream(serSavPath);
			// 把相应的Excel 工作簿存盘
			workbook.write(fOut);
			fOut.flush();
			// 操作结束，关闭文件
			//fOut.close();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 填充excel文件中的固定项
	 * 
	 * @param HSSFSheet
	 *            sheet excel中sheet对象
	 * @param List
	 *            <String> dataConstval 要写的固定项数据
	 * @throws Exception
	 * @throws IOException
	 */
	private static void fillConstva(HSSFSheet sheet, List<String> dataConstval)
			throws Exception {
		// 用indexOf寻找包含"param"的单元格：找不到为-1
		int Ifined = -1;
		// excel模版中用于存放固定项cell内容，用于匹配值
		String sParam = "param";
		// 得到总行数
		int tRow = sheet.getLastRowNum() + 1;
		for (int irow = 0; irow < tRow; irow++) {
			Row row = sheet.getRow(irow);
			// 总列数
			int tCol = row.getLastCellNum();
			for (int icol = 0; icol < tCol; icol++) {
				Cell cell = row.getCell(icol);
				String sValue = getCellVaule(cell);
				Ifined = sValue.indexOf(sParam);
				if (Ifined > -1) {
					try {
						Ifined = Integer.valueOf(sValue.substring(Ifined + 5));
						cell.setCellValue(dataConstval.get(Ifined));
					} catch (Exception e) {
						cell.setCellValue("");
						throw e;
					}
				}
				Ifined = -1;
			}
		}
	}

	/**
	 * 根据dataList的数据表的个数，填充excel模版中数据表： 模版中表的区域是用“lst1”[第一张表]，“lst2[第一张表]”标识，
	 * 该函数根据dataList中表的个数寻找excel中对应表的起始位置并以插入行的方式填充表数据。
	 * 
	 * @param HSSFSheet
	 *            sheet excel中sheet对象
	 * @param List
	 *            <List<List<String>>> dataList
	 *            要写的数据，第一层list为sheet，第二层list为row，第三层list为列数据
	 *            ，注意第三层列的个数要跟excel对应表中列的个数一至。
	 * @throws IOException
	 */
	private static void fillList(HSSFSheet sheet,
			List<List<List<String>>> dataList, HSSFCellStyle cellStyle) {
		// 用indexOf寻找包含"param"的单元格：找不到为-1
		int Ifined = -1;
		// excel模版中用于存放固定项cell内容，用于匹配值
		String sParam = "lst";
		// 获取表的个数
		int iTableTotalNum = dataList.size();
		for (int iTablNum = 0; iTablNum < iTableTotalNum; iTablNum++) {
			List<List<String>> dataTablel = (List<List<String>>) dataList
					.get(iTablNum);
			int iFindRow = -1;
			int iFindCol = -1;
			// 得到总行数
			int tRow = sheet.getLastRowNum() + 1;
			for (int irow = 0; irow < tRow; irow++) {
				Row row = sheet.getRow(irow);
				// 总列数
				int tCol = row.getLastCellNum();
				for (int icol = 0; icol < tCol; icol++) {
					Cell cell = row.getCell(icol);
					String sValue = getCellVaule(cell);
					Ifined = sValue.indexOf(sParam);
					if (Ifined > -1) {
						iFindRow = irow;
						iFindCol = icol;
						break;
					}
				}
				if (iFindRow > -1 && iFindCol > -1) {
					if (dataTablel != null && dataTablel.size() > 0) {
						fillTalbe(sheet, dataTablel, cellStyle, iFindRow,
								iFindCol);
					}

					break;
				}
			}

		}
	}

	/**
	 * 填充excel模版中一张表的数据
	 * 
	 * @param HSSFSheet
	 *            sheet excel中sheet对象
	 * @param List
	 *            <List<String>> dataTable
	 *            第一层list为row，第二层list为列数据，注意第三层列的个数要跟excel对应表中列的个数一至。
	 * @param int iExcRow excel中需要填充表的起始行。
	 * @param int iExcCol excel中需要填充表的起始列。
	 * @throws IOException
	 */
	private static void fillTalbe(HSSFSheet sheet,
			List<List<String>> dataTable, HSSFCellStyle cellStyle, int iExcRow,
			int iExcCol) {
		int iExcColStart = iExcCol;
		int iTCol = 0;
		for (int irow = 0; irow < dataTable.size(); irow++) {
			List<String> dataRow = (List<String>) dataTable.get(irow);
			// 第1行覆盖值，其他行要插入行再填值
			if (irow == 0) {
				Row row = sheet.getRow(iExcRow);
				iTCol = row.getLastCellNum();
				for (int icol = 0; icol < dataRow.size(); icol++) {
					Cell cell = row.getCell(iExcCol);
					try {
						cell.setCellValue(dataRow.get(icol));
					} catch (Exception e) {
						cell.setCellValue("");
					}
					iExcCol++;
				}
			} else {
				sheet.shiftRows(iExcRow, sheet.getLastRowNum() + 1, 1, true,
						false);
				Row row = sheet.createRow(iExcRow);
				for (int icol = 0; icol < dataRow.size(); icol++) {
					// 在索引0的位置创建单元格（左上端）
					Cell cell = row.createCell(iExcCol);
					cell.setCellStyle(cellStyle);
					// 定义单元格为字符串类型
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					try {
						// 在单元格中输入一些内容
						cell.setCellValue(dataRow.get(icol));
					} catch (Exception e) {
						cell.setCellValue("");
					}
					iExcCol++;
				}
				// 如果excel模版中的其他表格的列数比当前列多，就补空字符串
				if (iTCol > dataRow.size()) {
					for (int icol = 0; icol < (iTCol - dataRow.size()); icol++) {
						// 在索引0的位置创建单元格（左上端）
						Cell cell = row.createCell(iExcCol);
						cell.setCellStyle(cellStyle);
						// 定义单元格为字符串类型
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						// 在单元格中输入一些内容
						cell.setCellValue("");
						iExcCol++;
					}
				}
			}
			iExcCol = iExcColStart;
			iExcRow++;
		}
	}

}
