package com.chenzeyi.util.office;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
  
/** 
 * 使用CVS模式解决XLSX文件，可以有效解决用户模式内存溢出的问题 
 * 该模式是POI官方推荐的读取大数据的模式，在用户模式下，数据量较大、Sheet较多、或者是有很多无用的空行的情况 
 * ，容易出现内存溢出,用户模式读取Excel的典型代码如下： FileInputStream file=new 
 * FileInputStream("c:\\test.xlsx"); Workbook wb=new XSSFWorkbook(file); 
 *  
 *  
 * @author 山人 
 */  
public class XLSXCovertCSVReader {  
  
    /** 
     * The type of the data value is indicated by an attribute on the cell. The 
     * value is usually in a "v" element within the cell. 
     */  
    enum xssfDataType {  
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,  
    }  
  
    /** 
     * 使用xssf_sax_API处理Excel,请参考： http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api 
     * <p/> 
     * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at 
     * http://www.ecma-international.org/publications/standards/Ecma-376.htm 
     * <p/> 
     * A web-friendly version is http://openiso.org/Ecma/376/Part4 
     */  
    class MyXSSFSheetHandler extends DefaultHandler {  
  
        /** 
         * Table with styles 
         */  
        private StylesTable stylesTable;  
  
        /** 
         * Table with unique strings 
         */  
        private ReadOnlySharedStringsTable sharedStringsTable;  
  
        /** 
         * Number of columns to read starting with leftmost 
         */  
        private final int minColumnCount;  
  
        // Set when V start element is seen  
        private boolean vIsOpen;  
  
        // Set when cell start element is seen;  
        // used when cell close element is seen.  
        private xssfDataType nextDataType;  
  
        // Used to format numeric cell values.  
        private short formatIndex;  
        private String formatString;  
  
        private int thisColumn = -1;  
        // The last column printed to the output stream  
        private int lastColumnNumber = -1;  
  
        // Gathers characters as they are seen.  
        private StringBuffer value;  
        private String[] record;
        private List<List<String>> rows = new ArrayList<List<String>>();  
        private boolean isCellNull = false;  
  
        /** 
         * Accepts objects needed while parsing. 
         *  
         * @param styles 
         *            Table of styles 
         * @param strings 
         *            Table of shared strings 
         * @param cols 
         *            Minimum number of columns to show 
         * @param target 
         *            Sink for output 
         */  
        public MyXSSFSheetHandler(StylesTable styles,  
                ReadOnlySharedStringsTable strings, int cols) {  
            this.stylesTable = styles;  
            this.sharedStringsTable = strings;  
            this.minColumnCount = cols;  
            this.value = new StringBuffer();  
            this.nextDataType = xssfDataType.NUMBER; 
            record = new String[this.minColumnCount];  
            rows.clear();// 每次读取都清空行集合  
        }  
  
        /* 
         * (non-Javadoc) 
         *  
         * @see 
         * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, 
         * java.lang.String, java.lang.String, org.xml.sax.Attributes) 
         */  
        public void startElement(String uri, String localName, String name,  
                Attributes attributes) throws SAXException {  
  
            if ("inlineStr".equals(name) || "v".equals(name)) {  
                vIsOpen = true;  
                // Clear contents cache  
                value.setLength(0);  
            }  
            // c => cell  
            else if ("c".equals(name)) {  
                // Get the cell reference  
                String r = attributes.getValue("r");  
                int firstDigit = -1;  
                for (int c = 0; c < r.length(); ++c) {  
                    if (Character.isDigit(r.charAt(c))) {  
                        firstDigit = c;  
                        break;  
                    }  
                }  
                thisColumn = nameToColumn(r.substring(0, firstDigit));  
  
                // Set up defaults.  
                this.nextDataType = xssfDataType.NUMBER;  
                this.formatIndex = -1;  
                this.formatString = null;  
                String cellType = attributes.getValue("t");  
                String cellStyleStr = attributes.getValue("s"); 
                if ("s".equals(cellType))  
                    nextDataType = xssfDataType.SSTINDEX; 
                else if ("b".equals(cellType))  
                    nextDataType = xssfDataType.BOOL;  
                else if ("e".equals(cellType))  
                    nextDataType = xssfDataType.ERROR;  
                else if ("inlineStr".equals(cellType))  
                    nextDataType = xssfDataType.INLINESTR;  
                else if ("str".equals(cellType))  
                    nextDataType = xssfDataType.FORMULA;  
                else if (cellStyleStr != null) {  
                    // It's a number, but almost certainly one  
                    // with a special style or format  
                    int styleIndex = Integer.parseInt(cellStyleStr);  
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);  
                    this.formatIndex = style.getDataFormat();  
                    this.formatString = style.getDataFormatString();  
                    if (this.formatString == null)  
                        this.formatString = BuiltinFormats  
                                .getBuiltinFormat(this.formatIndex);  
                }  
            }  
  
        }  
  
        /* 
         * (non-Javadoc) 
         *  
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, 
         * java.lang.String, java.lang.String) 
         */  
        public void endElement(String uri, String localName, String name)  
                throws SAXException {  
  
            String thisStr = null;  
  
            // v => contents of a cell  
            if ("v".equals(name)) {  
                // Process the value contents as required.  
                // Do now, as characters() may be called more than once  
                switch (nextDataType) {
                case SSTINDEX:  
                    String sstIndex = value.toString();  
                    try {  
                        int idx = Integer.parseInt(sstIndex);  
                        XSSFRichTextString rtss = new XSSFRichTextString(  
                                sharedStringsTable.getEntryAt(idx));  
                        thisStr = rtss.toString();  
                    } catch (NumberFormatException ex) {  
                        ex.printStackTrace();  
                    }  
                    break;
  
                case BOOL:  
                    char first = value.charAt(0);  
                    thisStr = first == '0' ? "FALSE" : "TRUE";  
                    break;  
  
                case ERROR:  
                    thisStr = "\"ERROR:" + value.toString() + '"';  
                    break;  
  
                case FORMULA:  
                    // A formula could result in a string value,  
                    // so always add double-quote characters.  
                    thisStr = '"' + value.toString() + '"';  
                    break;  
  
                case INLINESTR:  
                    // TODO: have seen an example of this, so it's untested.  
                    XSSFRichTextString rtsi = new XSSFRichTextString(  
                            value.toString());  
                    thisStr = rtsi.toString();  
                    break;  
  
                case NUMBER:  
//                    String n = value.toString();  
//                    // 判断是否是日期格式  
//                    if (HSSFDateUtil.isADateFormat(this.formatIndex, n)) {  
//                        Double d = Double.parseDouble(n);  
//                        Date date=HSSFDateUtil.getJavaDate(d);  
//                        thisStr=formateDateToString(date);  
//                    } else if (this.formatString != null)  
//                        thisStr = formatter.formatRawCellContents(  
//                                Double.parseDouble(n), this.formatIndex,  
//                                this.formatString);  
//                    else  
                        thisStr = value.toString();  
                    break;  
  
                default:  
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";  
                    break;  
                }  
  
                // Output after we've seen the string contents  
                // Emit commas for any fields that were missing on this row  
                if (lastColumnNumber == -1) {  
                    lastColumnNumber = 0;  
                }  
                //判断单元格的值是否为空  
                if (thisStr == null || "".equals(isCellNull)) {  
                    isCellNull = true;// 设置单元格是否为空值  
                } 
                if(this.minColumnCount > thisColumn){
                	record[thisColumn] = thisStr;  
                }
                // Update column  
                if (thisColumn > -1)  
                    lastColumnNumber = thisColumn;  
  
            } else if ("row".equals(name)) {  
                if (minColumns > 0) {  
                    // Columns are 0 based  
                    if (lastColumnNumber == -1) {  
                        lastColumnNumber = 0;  
                    }  
                    if (isCellNull == false)// 判断是否空行  
                    {  
                    	//只要有一列有值就添加
                    	boolean haveData = false;
                    	for (int i = 0; i < record.length; i++) {
							if(!"".equals(record[i]) && record[i] != null){
								haveData = true;
								break;
							}
						}
                    	if(haveData){
							rows.add(data2List(record.clone())); 
	                        isCellNull = false;  
	                        for (int i = 0; i < record.length; i++) {  
	                            record[i] = null;  
	                        } 
						}
                    }  
                }  
                lastColumnNumber = -1;  
            }  
  
        }  
        
        private List<String> data2List(String[] s){
        	List<String> list = new ArrayList<String>();
        	for (int i = 0; i < s.length; i++) {
				if(s[i] == null) {
					list.add("");
				}else{
					list.add(s[i]);
				}
			}
        	return list;
        }
  
        public List<List<String>> getRows() {  
            return rows;  
        }  
  
        public void setRows(List<List<String>> rows) {  
            this.rows = rows;  
        }  
  
        /** 
         * Captures characters only if a suitable element is open. Originally 
         * was just "v"; extended for inlineStr also. 
         */  
        public void characters(char[] ch, int start, int length)  
                throws SAXException {  
            if (vIsOpen)  
                value.append(ch, start, length);  
        }  
    }
    
    /** 
     * Converts an Excel column name like "C" to a zero-based index. 
     *  
     * @param name 
     * @return Index corresponding to the specified name 
     */  
    private int nameToColumn(String name) {  
        int column = -1;  
        for (int i = 0; i < name.length(); ++i) {  
            int c = name.charAt(i);  
            column = (column + 1) * 26 + c - 'A';  
        }  
        return column;  
    } 
    
    private int minColumns;  
  
    /** 
     * Creates a new XLSX -> CSV converter 
     *  
     * @param pkg 
     *            The XLSX package to process 
     * @param output 
     *            The PrintStream to output the CSV to 
     * @param minColumns 
     *            The minimum number of columns to output, or -1 for no minimum 
     */  
    public XLSXCovertCSVReader(OPCPackage pkg,int minColumns) {  
        this.minColumns = minColumns;  
    }  
  
    /** 
     * Parses and shows the content of one sheet using the specified styles and 
     * shared-strings tables. 
     *  
     * @param styles 
     * @param strings 
     * @param sheetInputStream 
     */  
    public List<List<String>> processSheet(StylesTable styles,  
            ReadOnlySharedStringsTable strings, InputStream sheetInputStream)  
            throws IOException, ParserConfigurationException, SAXException {  
  
        InputSource sheetSource = new InputSource(sheetInputStream);  
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();  
        SAXParser saxParser = saxFactory.newSAXParser();  
        XMLReader sheetParser = saxParser.getXMLReader();  
        MyXSSFSheetHandler handler = new MyXSSFSheetHandler(styles, strings,  
                this.minColumns);  
        sheetParser.setContentHandler(handler);  
        sheetParser.parse(sheetSource);  
        return handler.getRows();  
    }  
  
    /** 
     * 初始化这个处理程序 将 
     *  
     * @throws IOException 
     * @throws OpenXML4JException 
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */  
    public List<List<List<String>>> process(OPCPackage p) throws IOException, OpenXML4JException,  
            ParserConfigurationException, SAXException {  
    	List<List<List<String>>> result = new ArrayList<List<List<String>>>();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(p);  
        XSSFReader xssfReader = new XSSFReader(p);  
        StylesTable styles = xssfReader.getStylesTable();  
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader  
                .getSheetsData();  
        while (iter.hasNext()) {  
            InputStream stream = iter.next();  
            result.add(processSheet(styles, strings, stream));
            stream.close();
            break;
        }  
        return result;  
    }  
  
    /** 
     * 读取Excel 
     *  
     * @param path 
     *            文件路径 
     * @param sheetName 
     *            sheet名称 
     * @param minColumns 
     *            列总数 
     * @return 
     * @throws Exception 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws OpenXML4JException 
     * @throws IOException 
     */  
    public static List<List<List<String>>> readerExcel(String path,int minColumns) throws Exception{  
    	OPCPackage p = null; 
    	List<List<List<String>>> list = null;
        try{
        	p = OPCPackage.open(path, PackageAccess.READ); 
	        XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p,minColumns);  
	        list = xlsx2csv.process(p);  
        }catch(Exception e){
        	throw e;
        }finally{
        	try {
				p.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }
        return list;  
    }  
    
    public static void main(String[] args) throws Exception {  
    	List<List<List<String>>> list = XLSXCovertCSVReader.readerExcel("D:\\2.xlsx",17);  
    	for (List<List<String>> lists: list) {
    		for (List<String> record : lists) {  
                    System.out.println(record);  
            }  
		}
        
    }  
  
}  