package com.chenzeyi.util.itextpdf;

public class PaperType {
	private final static float  transferKey = (float) (1.00f / 10000 / 2.54 * 72);
	
	private float paperHeight = -1;
	
	private float paperWidth = -1;
	
	private float marginTop = -1;
	
	private float marginBottom = -1;
	
	private float marginLeft = -1;
	
	private float marginRight = -1;

	public PaperType(float paperWidth, float paperHeight, float marginTop,
			float marginBottom, float marginLeft, float marginRight) {
		super();
		this.paperHeight = paperHeight;
		this.paperWidth = paperWidth;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
	}

	public PaperType() {
		// TODO Auto-generated constructor stub
	}

	public float getPaperHeight() {
		return paperHeight;
	}

	public void setPaperHeight(float paperHeight) {
		this.paperHeight = paperHeight;
	}

	public float getPaperWidth() {
		return paperWidth;
	}

	public void setPaperWidth(float paperWidth) {
		this.paperWidth = paperWidth;
	}

	public float getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(float marginTop) {
		this.marginTop = marginTop;
	}

	public float getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
	}

	public float getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
	}

	public float getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(float marginRight) {
		this.marginRight = marginRight;
	}
	/**
	 * 转换Integer为float类型再转像素
	 * Paper的属性单位是微米
	 * PaperType的属性单位是像素
	 * 数据库配置的是微米，需要按72dpi分辨率转化成像素
	 * 
	 * 210000/10000*0.3937*72 = 595.2
	 * @param paper
	 * @return
	 */
	public static PaperType getPaperTypeFromPaper(Paper paper){
//		return new PaperType(paper.getPaperHeight()*transferKey,paper.getPaperWidth()*transferKey,paper.getMarginTop()*transferKey,paper.getMarginBottom()*transferKey,paper.getMarginLeft()*transferKey,paper.getMarginRight()*transferKey);
		return new PaperType(paper.getPaperWidth()*transferKey,paper.getPaperHeight()*transferKey,paper.getMarginTop()*transferKey,paper.getMarginBottom()*transferKey,paper.getMarginLeft()*transferKey,paper.getMarginRight()*transferKey);
	}
}
