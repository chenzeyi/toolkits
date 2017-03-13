package com.chenzeyi.util.QrCoder;

public class QrTester {
	 public static void main(String[] args) {
	        try {
	        	Bc4JUtil.generateBarCode("11111","E://BR.png");
	        	Bc4JUtil.generateQrCode("12121","E://QR.png");
	        	} catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
