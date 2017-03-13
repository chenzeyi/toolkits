package com.chenzeyi.util.itextpdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfUtil {
	private static File fonts = new File("fonts/SIMSUN.TTC");
	public static List<String> TXTToPDF(String sourcePath, PaperType pt,String keyword)throws Exception {
		List<String> unsignedpath = new ArrayList<String>();
		BufferedReader read = null;
		String pdfFile = sourcePath + ".pdf";
		Document document = null;
		FileOutputStream fos = null;
		PdfWriter writer = null;
		try {
			fos = new FileOutputStream(pdfFile);
			document = new Document(new Rectangle(pt.getPaperWidth()/100*97,pt.getPaperHeight()));
			document.setMargins(pt.getMarginLeft(),pt.getMarginRight(),pt.getMarginTop()/200,pt.getMarginBottom());
			writer = PdfWriter.getInstance(document, fos);
			document.addAuthor("厦门国际银行");
			document.addCreationDate();
			document.addCreator("打印管理系统");
			document.addTitle("空标题");
			document.addSubject("SUBJECT");
			document.addKeywords(keyword);
			System.out.println(document.getHtmlStyleClass());
			System.out.println(document.getJavaScript_onLoad());
			System.out.println(document.getJavaScript_onUnLoad());
			System.out.println(document.getPageNumber());
			System.out.println(document.getPageSize());
			
			document.open();
			
			BaseFont bf = BaseFont.createFont(fonts.getAbsolutePath()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bf, (float)8.8, Font.NORMAL);
			FontChinese = new Font(bf, (float)8.8, Font.NORMAL);
			read = new BufferedReader(new FileReader(sourcePath));
			String line = null;
			Paragraph t = null;
			int tailBlank = 0;
			StringBuilder sb = new StringBuilder();
			while ((line = read.readLine()) != null) {
					sb.append(line);
	                sb.append("\n");
	                if(!"".equals(line)){//当遇到非空行时,重置;记录内容尾部空行行数
	                	tailBlank = 0;
	                }else{
	                	tailBlank++;
	                }
			}
			t = new Paragraph(sb.substring(0, sb.length()-tailBlank), FontChinese);
			document.add(t);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				read.close();
				document.close();
				writer.flush();
				writer.close();
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		File pdf = new File(pdfFile);
		if (pdf.exists()) unsignedpath.add(pdf.getAbsolutePath());
		return unsignedpath;
	}
	
	/**
	 * 往pdf中加入js脚本
	 * @param inpdfpath
	 * @param scripts
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static String  appendScript(String inpdfpath,String scripts) throws IOException, DocumentException{
		String solvedPdf = null;
		String outpdfpath = inpdfpath.replaceAll(".pdf", "..pdf");
		File inpdf = new File(inpdfpath);
		File outpdf = new File(outpdfpath);
		InputStream is = null;
		FileOutputStream fos= null;
		PdfReader reader = null;
		PdfStamper stamp =null;
		try{
			is = new FileInputStream(inpdf);
			fos = new FileOutputStream(outpdf);
	        reader = new PdfReader(is);  
	        stamp = new PdfStamper(reader, fos);
	        StringBuffer script = new StringBuffer();  

	        script.append("this.zoom=100;");//设置打开时候的显示百分比,不是打印的
	        //打印参数链接 http://help.adobe.com/en_US/acrobat/acrobat_dc_sdk/2015/HTMLHelp/index.html#t=Acro12_MasterBook%2FJS_API_AcroJS%2FPrintParams.htm
//	        script.append("this.print({bUI: false,bSilent: true,bShrinkToFit: false});");  //给pdf加上脚本实现自动掉打印机，然后自动关闭  
//	        script.append("\r\n this.closeDoc();");   //自动关闭的脚本 
//	        script.append("var PrintParams = this.getPrintParams();PrintParams.interactive = PrintParams.constants.interactionLevel.silent;this.print(PrintParams);");//打印2
//	        script.append("var PrintParams = this.getPrintParams();PrintParams.interactive = PrintParams.constants.interactionLevel.automatic;this.print(PrintParams);");//打印3
//	        script.append("var PrintParams = this.getPrintParams();PrintParams.interactive = PrintParams.constants.interactionLevel.full;this.print(PrintParams);");//打印4
//	        script.append("var PrintParams = this.getPrintParams();PrintParams.printAsImage=true;PrintParams.interactive = PrintParams.constants.interactionLevel.full;this.print(PrintParams);");//作为图片打印打印4
	        script.append("var PrintParams = this.getPrintParams();PrintParams.printAsImage=true;PrintParams.bitmapDPI=9600;PrintParams.gradientDPI=9600;PrintParams.interactive = PrintParams.constants.interactionLevel.full;this.print(PrintParams);");//作为图片打印打印5,设置dpi
	        stamp.addJavaScript(script.toString()); 
	        stamp.setViewerPreferences(PdfWriter.HideMenubar|PdfWriter.PrintScalingNone|PdfWriter.HideWindowUI|PdfWriter.HideToolbar|PdfWriter.FitWindow);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			stamp.close();
			reader.close();
	        fos.flush();  
	        fos.close();
	        is.close();
	        if(outpdf.exists()) {
//	        	inpdf.delete();
	        	solvedPdf =  outpdf.getAbsolutePath();
	        }else{
	        	solvedPdf =  inpdf.getAbsolutePath();
	        }
		}
		
		return solvedPdf;
	}
	public static void fillTemplate(String inpdfpath,String outpdfpath,String scripts) throws IOException, DocumentException{
		FileOutputStream fos = new FileOutputStream(new File(outpdfpath));
		InputStream is = new FileInputStream(new File(inpdfpath));
		// 目标输出流  
		PdfReader reader = new PdfReader(is);  
		PdfStamper stamp = new PdfStamper(reader, fos);
		StringBuffer script = new StringBuffer();  
		//给pdf加上脚本实现自动掉打印机，然后自动关闭  
		script.append("this.print({bUI: false,bSilent: true,bShrinkToFit: false});");  
		script.append("this.zoom=100;");//设置打开时候的显示百分比,不是打印的
		//自动关闭的脚本  
		script.append("\r\n this.closeDoc();");  
		stamp.addJavaScript(script.toString());  
		Image img = Image.getInstance(new File("img/pic.png").getAbsolutePath());  
		img.setAbsolutePosition(50, 680);  
		PdfContentByte over = stamp.getOverContent(1);  
		over.addImage(img);               
        AcroFields form = stamp.getAcroFields();  
       //通过属性名来赋值  
        form.setField("orderCode", "DD0045784-897");  
        form.setField("orderPrice", "78.99");  
        form.setField("receiveName", "艾伦");  
        form.setField("cellphone", "021-14579896");  
        form.setField("mobile", "16898654789");  
        form.setField("ispos", "是");  
        form.setField("address", "银河系太阳系地球亚洲中国内蒙古自治区亚历山大市亚历山大县亚历山大镇亚里士多德乡哥伦布村长寿街3号");  
          
        for(int i=0;i<2;i++){  
            form.setField("stockNo"+i, "789558554");  
            form.setField("number"+i, "2");  
            form.setField("price"+i, "40");  
        }  
		
		
		stamp.setFormFlattening(true);  
		stamp.close();  
		//输出文件，关闭流  
		fos.flush();  
		fos.close();  
		
	}
	
	/**
	 * 可设置行间距
	 * @param sourcePath
	 * @param pt
	 * @param reprintInfo
	 * @return
	 * @throws Exception
	 */
	public static List<String> TXT2PDF(String sourcePath, PaperType pt,String reprintInfo) throws Exception {
		List<String> unsignedpath = new ArrayList<String>();
		BufferedReader read = null;
		String destpath = sourcePath + ".pdf";
		com.itextpdf.text.Document document = null;
		try {
			document = new com.itextpdf.text.Document(new Rectangle(pt.getPaperWidth(),pt.getPaperHeight()));
			document.setMargins(pt.getMarginLeft()+10,pt.getMarginRight(),pt.getMarginTop(),pt.getMarginBottom());
			
			PdfWriter pdfwriiter = PdfWriter.getInstance(document, new FileOutputStream(destpath));
			pdfwriiter.setInitialLeading(-25);//设置初始顶部间距
			pdfwriiter.setViewerPreferences(PdfWriter.FitWindow);//展示时适应纸张
			pdfwriiter.setViewerPreferences(PdfWriter.CenterWindow);//打开时窗口置于中间
			pdfwriiter.setViewerPreferences(PdfWriter.HideToolbar);//隐藏工具栏
			pdfwriiter.setViewerPreferences(PdfWriter.HideMenubar);//隐藏菜单栏
			document.open();
			
			document.addAuthor("厦门国际银行股份有限公司");
			document.addCreationDate();
			document.addCreator("打印管理系统");
			document.addTitle("交易凭证电子文件");
			document.addSubject("http://www.xib.com.cn/");
			document.addKeywords(reprintInfo);
			
			BaseFont bf = BaseFont.createFont(fonts.getAbsolutePath()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bf, (float)10, Font.NORMAL);
			read = new BufferedReader(new FileReader(sourcePath));
			String line = null;
			int lineIndex = 0;
			int tailBlank = 0;//尾部空行条数
			List<String> lineLists = new ArrayList<String>();
			Paragraph paragraph = null;
			Phrase phase = new Phrase();
			phase.setFont(FontChinese);
			phase.setLeading(12);//设置行间距
			while ((line = read.readLine()) != null) {
				if(lineIndex==0 && "".equals(line) && reprintInfo!=null&&!"".equals(reprintInfo)) line = leftpadBlank(85,reprintInfo);//A00默认最长91个字符
				lineLists.add(line);
				 if(!"".equals(line)){//当遇到非空行时,重置;记录内容尾部空行行数
	                tailBlank = 0;
				 }else{
                	tailBlank ++;
				 }
				lineIndex ++;
			}
			lineLists = lineLists.subList(0, lineIndex-tailBlank);//截取空行
			for(String content:lineLists){
				Chunk newLine = new Chunk(content);
				phase.add(newLine);
				phase.add(Chunk.NEWLINE);//换行
			}
			lineLists.clear();
			paragraph = new Paragraph(phase);
			paragraph.setFont(FontChinese);
			paragraph.setAlignment(3);//1居中，2右对齐，3左对齐，默认为3
			document.add(paragraph);
		} catch (Exception e) {
			throw e;
		} finally {
			read.close();
			document.close();
		}
		File pdf = new File(destpath);
		if (pdf.exists()) {
			unsignedpath.add(setPdfPreference(pdf));
		}
		return unsignedpath;
	}
	/**
	 * 可设置行间距
	 * @param sourcePath
	 * @param pt
	 * @param reprintInfo
	 * @param pageMark  换行标志
	 * @return
	 * @throws Exception
	 */
	public static List<String> TXT2PDF(String sourcePath, PaperType pt,String reprintInfo,String pageMark) throws Exception {
		List<String> unsignedpath = new ArrayList<String>();
		BufferedReader read = null;
		String destpath = sourcePath + ".pdf";
		com.itextpdf.text.Document document = null;
		try {
			document = new com.itextpdf.text.Document(new Rectangle(pt.getPaperWidth(),pt.getPaperHeight()));
			document.setMargins(pt.getMarginLeft()+10,pt.getMarginRight(),pt.getMarginTop(),pt.getMarginBottom());
			
			PdfWriter.getInstance(document, new FileOutputStream(destpath)).setInitialLeading(-25);//设置初始顶部间距
			document.open();
			
			document.addAuthor("厦门国际银行股份有限公司");
			document.addCreationDate();
			document.addCreator("打印管理系统");
			document.addTitle("交易凭证电子文件");
			document.addSubject("http://www.xib.com.cn/");
			document.addKeywords(reprintInfo);
			
			BaseFont bf = BaseFont.createFont(fonts.getAbsolutePath()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font FontChinese = new Font(bf, (float)10, Font.NORMAL);
			read = new BufferedReader(new FileReader(sourcePath));
			String line = null;
			int lineIndex = 0;
			int tailBlank = 0;//尾部空行条数
			List<String> lineLists = new ArrayList<String>();
			Paragraph paragraph = null;
			Phrase phase = new Phrase();
			phase.setFont(FontChinese);
			phase.setLeading(12);//设置行间距
			while ((line = read.readLine()) != null) {
				if(lineIndex==0 && "".equals(line) && reprintInfo!=null&&!"".equals(reprintInfo)) line = leftpadBlank(85,reprintInfo);//A00默认最长91个字符
				lineLists.add(line);
				if(!"".equals(line)){//当遇到非空行时,重置;记录内容尾部空行行数
					tailBlank = 0;
				}else{
					tailBlank ++;
				}
				lineIndex ++;
			}
			lineLists = lineLists.subList(0, lineIndex-tailBlank);//截取空行
			for(String content:lineLists){
				Chunk newLine = new Chunk(content);
				phase.add(newLine);
				phase.add(Chunk.NEWLINE);//换行
			}
			lineLists.clear();
			paragraph = new Paragraph(phase);
			paragraph.setFont(FontChinese);
			paragraph.setAlignment(3);//1居中，2右对齐，3左对齐，默认为3
			document.add(paragraph);
		} catch (Exception e) {
			throw e;
		} finally {
			read.close();
			document.close();
		}
		File pdf = new File(destpath);
		if (pdf.exists()) {
			unsignedpath.add(setPdfPreference(pdf));
		}
		return unsignedpath;
	}
	/**
	 * 左填充空格
	 * @param maxLineLength
	 * @return
	 */
	private static String leftpadBlank(int maxLineLength,String reprintinfo){
		StringBuilder sb = new StringBuilder();
		int infoLenght = reprintinfo.length();
		for(int i=0;i<(maxLineLength-infoLenght);i++){
			sb.append(" ");
		}
		sb.append(reprintinfo);
		return sb.toString();
	}
	/**
	 * 设置pdf属性
	 * win7上测试耗时平均80毫秒
	 * @param pdffile
	 * @throws Exception 
	 */
	private static String setPdfPreference(File inpdf) throws Exception{
		String outpdfpath = inpdf.getAbsolutePath().replaceAll(".pdf", "..pdf");
		File outpdf = new File(outpdfpath);
		InputStream is = null;
		FileOutputStream fos= null;
		PdfReader reader = null;
		PdfStamper stamp =null;
		try{
			is = new FileInputStream(inpdf);
			fos = new FileOutputStream(outpdf);
	        reader = new PdfReader(is);  
	        stamp = new PdfStamper(reader, fos);
	        StringBuffer script = new StringBuffer();  
	        script.append("this.zoom=100;");//设置打开时候的显示百分比,不是打印的
	        stamp.addJavaScript(script.toString()); 
	        stamp.setViewerPreferences(PdfWriter.HideMenubar|PdfWriter.PrintScalingNone|PdfWriter.HideWindowUI|PdfWriter.HideToolbar|PdfWriter.FitWindow);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			stamp.close();
			reader.close();
	        fos.flush();  
	        fos.close();
	        is.close();
		}
		 if(outpdf.exists()) {
	        	inpdf.delete();//先删除原文件
	        	outpdf.renameTo(inpdf);//再重命名为原文件名
	      }
		return  inpdf.getAbsolutePath();
	}
	
	public static void WriteDocument() throws DocumentException, IOException{
		com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("E://dede.pdf"));
		doc.open();
        PdfContentByte direct = writer.getDirectContent();
        direct.saveState();
        String foobar = "ShowTextAligned演示";

        direct.beginText();
      
        BaseFont bf = BaseFont.createFont(fonts.getAbsolutePath()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font FontChinese = new Font(bf, (float)10, Font.NORMAL);
		FontChinese = new Font(bf, (float)10, Font.NORMAL);
        direct.setFontAndSize(bf, 12);
        direct.showTextAligned(PdfContentByte.ALIGN_LEFT, foobar, 400, 788, 0);
        direct.showTextAlignedKerned(PdfContentByte.ALIGN_LEFT, foobar, 408, 788, 0);
        direct.showTextAligned(PdfContentByte.ALIGN_RIGHT, foobar, 400, 752, 0);
        direct.showTextAlignedKerned(PdfContentByte.ALIGN_RIGHT, foobar, 408, 752, 0);
        direct.showTextAligned(PdfContentByte.ALIGN_CENTER, foobar, 400, 716, 0);
        direct.showTextAlignedKerned(PdfContentByte.ALIGN_CENTER, foobar, 408, 716, 0);
        direct.showTextAligned(PdfContentByte.ALIGN_CENTER, foobar, 400, 680, 30);
        direct.showTextAlignedKerned(PdfContentByte.ALIGN_CENTER, foobar, 408, 680, 30);
        direct.showTextAligned(PdfContentByte.ALIGN_LEFT, foobar, 400, 644, 0);
        direct.showTextAlignedKerned(PdfContentByte.ALIGN_LEFT, foobar, 408, 644, 0);
        
      
        direct.endText();
        direct.restoreState();//恢复输出状态
        doc.close();
    }
	
	public static void insertImgMark() throws FileNotFoundException, DocumentException, IOException{
		String imgpath= "E://img/A4_black.png";
		String sourpdfpath = "E://img/11.pdf";
		String destpdfpath = "E://img/112.pdf";
        PdfReader reader = new PdfReader(sourpdfpath);  
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destpdfpath)); 
        Image image = Image.getInstance(imgpath);  
        image.setAbsolutePosition(0, 0);  
//        image.scaleToFit(400, 125);  
        PdfContentByte content;
        content = stamper.getOverContent(1);  
        content.addImage(image);  
        stamper.close();  
        
	}
	public static void main(String[] args) {
		try {
			TXTToPDF("E://MO0011188_20160331_TL.RC.ALL.ACCOUNT.COB_17631802874403800_COBUSER_COB.OUT",PaperType.getPaperTypeFromPaper(new Paper(216000,139900,10000,10000,1000,1000)),"第1次打印,以此份为准");
//			//FTS_CN0018002_20160317_XIB.PER.OTP.ABO.VOU_ITS20160317100004183_3000002384.OUT.pdf
//			appendScript( "E://FTS_CN0018002_20160317_XIB.PER.OTP.ABO.VOU_ITS20160317100004183_3000002384.OUT.pdf", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
