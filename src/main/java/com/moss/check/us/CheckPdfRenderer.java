/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of us-check-printing-trunk.
 *
 * us-check-printing-trunk is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * us-check-printing-trunk is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with us-check-printing-trunk; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.check.us;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * TODO:
 * - correct MICR line placement, etc.
 * - make sure everything fits nicely on the check stock
 */
public class CheckPdfRenderer {
	
	/*
	 * All measurements are expressed in centimeters and converted to points
	 * (1/64ths of an inch) when appropriate.
	 */
	
	static final float POINTS_IN_A_CM = 28.3464567f;
	
	static final float PAGE_HEIGHT = 28f * POINTS_IN_A_CM;
	static final float PAGE_WIDTH = 21.6f * POINTS_IN_A_CM;
	
	static final float BOTTOM_EDGE_HEIGHT = 0.5f * POINTS_IN_A_CM;
	static final float OPTICAL_CLEAR_BAND_HEIGHT = 0.76f * POINTS_IN_A_CM;
	static final float MICR_CLEAR_BAND_HEIGHT = 1.59f * POINTS_IN_A_CM;
	
	public void render(CheckModel model, OutputStream out) throws Exception {
		
		Document document = new Document();
		document.setPageSize(new Rectangle(PAGE_WIDTH, PAGE_HEIGHT));
		
		PdfWriter writer = PdfWriter.getInstance(document, out);
		document.open();
		
		PdfContentByte cb = writer.getDirectContent();
		
		Check check = new Check();
		check.defaultFont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		check.defaultFontSize = 8;
		check.defaultFontLeading = 10;
		check.largeFontSize = 9;
		check.largeFontLeading = 12;
		check.fixedWidthFont = createFixedFont();
		check.fixedWidthFontSize = 8;
		check.voidFont = BaseFont.createFont("Helvetica-Bold", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		check.voidFontSize = 14;
		check.micrFont = createMicrFont();
		check.micrFontSize = 12;
		check.model = model;
		check.x = 0;
		check.y = 0;
		check.renderMode = CheckRenderMode.CHECK;
		
		check.render(cb);
		
		if (StubPrintMode.CHECK_DUPLICATE == model.stubPrintMode) {
			check.renderMode = CheckRenderMode.STUB;
			check.y = document.top() - (8.2f * POINTS_IN_A_CM); 
			check.render(cb);
		}
		else if (StubPrintMode.CUSTOM == model.stubPrintMode) {
			PdfReader reader = new PdfReader(model.customStubPdf);
			PdfImportedPage customPage = writer.getImportedPage(reader, 1);
			cb.addTemplate(customPage, 0f, 0f);
		}
		else {
			throw new RuntimeException("Unknown stub print mode: " + model.stubPrintMode);
		}
		
		document.close();
	}
	
	private static enum CheckRenderMode {
		CHECK, STUB
	}
	
	private static class Check {
		
		CheckModel model;
		float x, y;
		
		BaseFont defaultFont;
		int defaultFontSize;
		float defaultFontLeading;
		int largeFontSize;
		float largeFontLeading;
		
		BaseFont fixedWidthFont;
		int fixedWidthFontSize;
		
		BaseFont micrFont;
		int micrFontSize;
		
		BaseFont voidFont;
		int voidFontSize;
		
		CheckRenderMode renderMode;

		void render(PdfContentByte cb) {
			
			/*
			 * useful for debugging--the line below which nothing but MICR should be printed
			 */
			
//			cb.setColorStroke(Color.BLACK);
//			cb.moveTo(0, MICR_CLEAR_BAND_HEIGHT);
//			cb.lineTo(PAGE_WIDTH, MICR_CLEAR_BAND_HEIGHT);
//			cb.stroke();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.setLeading(defaultFontLeading);
			cb.moveText(x + (1.7f * POINTS_IN_A_CM), y + (8.2f * POINTS_IN_A_CM));
			
			for (int i=0; i<model.drawerNameAddressLines.length; i++) {
				cb.showText(model.drawerNameAddressLines[i]);
				if (i + 1 < model.drawerNameAddressLines.length) {
					cb.newlineText();
				}
			}
			
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, largeFontSize);
			cb.moveText(x + (1.7f * POINTS_IN_A_CM), y + (6f * POINTS_IN_A_CM));
			cb.showText(model.amountVerbose);
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.moveText(x + (3.7f * POINTS_IN_A_CM), y + (4.8f * POINTS_IN_A_CM));
			cb.showText("TO THE ORDER OF");
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, largeFontSize);
			cb.setLeading(largeFontLeading);
			cb.moveText(x + (2.5f * POINTS_IN_A_CM), y + (4.05f * POINTS_IN_A_CM));
			
			for (int i=0; i<model.payeeNameAddressLines.length; i++) {
				cb.showText(model.payeeNameAddressLines[i]);
				if (i + 1 < model.payeeNameAddressLines.length) {
					cb.newlineText();
				}
			}
			
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.setLeading(defaultFontLeading);
			cb.moveText(x + (11.65f * POINTS_IN_A_CM), y + (8.2f * POINTS_IN_A_CM));
			cb.showText(model.draweeName);
			cb.newlineText();
			cb.showText(model.draweeCityStateZip);
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.moveText(x + (18.8f * POINTS_IN_A_CM), y + (7.9f * POINTS_IN_A_CM));
			cb.showText(model.routingFraction);
			cb.endText();
			
			LabelBox box = new LabelBox();
			box.x = x + (11.65f * POINTS_IN_A_CM);
			box.y = y + (4.7f * POINTS_IN_A_CM);
			box.width = (2.5f * POINTS_IN_A_CM);
			box.height = (1f * POINTS_IN_A_CM);
			box.label = "DATE";
			box.value = model.date;
			box.labelFont = defaultFont;
			box.labelFontSize = 9;
			box.valueFont = fixedWidthFont;
			box.valueFontSize = fixedWidthFontSize;
			box.valueAlignment = Alignment.CENTER;
			box.render(cb);
			
			box.x = x + (14.45f * POINTS_IN_A_CM);
			box.label = "CHECK NO";
			box.value = model.checkNumber;
			box.valueAlignment = Alignment.RIGHT;
			box.render(cb);
			
			box.x = x + (17.35f * POINTS_IN_A_CM);
			box.width = x + (3.5f * POINTS_IN_A_CM);
			box.label = "CHECK AMOUNT";
			box.value = model.amount;
			box.valueAlignment = Alignment.CENTER;
			box.render(cb);
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, largeFontSize);
			cb.setLeading(defaultFontLeading);
			cb.moveText(x + (14.45f * POINTS_IN_A_CM), y + (3.2f * POINTS_IN_A_CM));
			cb.showText(model.limitsText1);
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.newlineText();
			cb.showText(model.limitsText2);
			cb.endText();
			
			cb.beginText();
			cb.setFontAndSize(defaultFont, defaultFontSize);
			cb.moveText(x + (12.2f * POINTS_IN_A_CM), y + (1.7f * POINTS_IN_A_CM));
			cb.showText("BY");
			cb.endText();

			cb.setLineWidth(.5f);
			cb.moveTo(x + (12.55f * POINTS_IN_A_CM), y + (1.7f * POINTS_IN_A_CM));
			cb.lineTo(x + (18.65f * POINTS_IN_A_CM), y + (1.7f * POINTS_IN_A_CM));
			cb.stroke();
			
			if (CheckRenderMode.CHECK == renderMode) {
				
				if (model.signature != null && model.signature.length!=0) {

					try {
						Image image = Image.getInstance(model.signature);

						float imageX = x + (12.6f * POINTS_IN_A_CM);
						float imageY = y + (1.35f * POINTS_IN_A_CM);

						image.setAbsolutePosition(imageX, imageY);
						image.scaleAbsolute(8f * POINTS_IN_A_CM, 1.2f * POINTS_IN_A_CM);
						cb.addImage(image);

						/*
						 * draws the border of the scaled image--useful for debugging
						 */

						//	float imageWidth = image.getScaledWidth();
						//	float imageHeight = image.getScaledHeight();
						//	cb.rectangle(imageX, imageY, imageWidth, imageHeight);
						//	cb.stroke();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				float micrPrintY = y + (.575f * POINTS_IN_A_CM);
				float transitOnUsPrintX = x + (7.3f * POINTS_IN_A_CM); 
				
				cb.beginText();
				cb.setFontAndSize(micrFont, micrFontSize);
				cb.moveText(transitOnUsPrintX, micrPrintY);
				cb.showText("A" + model.transit + "A " + model.onUs + "C");
				cb.endText();
				
				cb.beginText();
				float spaceWidth = cb.getEffectiveStringWidth(" ", true);
				cb.setFontAndSize(micrFont, micrFontSize);
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "C" + model.auxOnUs + "C", transitOnUsPrintX - spaceWidth, micrPrintY, 0);
				cb.endText();
			}
			else if (CheckRenderMode.STUB == renderMode) {
				
				cb.beginText();
				cb.setFontAndSize(voidFont, voidFontSize);
				cb.moveText(x + (13.3f * POINTS_IN_A_CM), y + (1.8f * POINTS_IN_A_CM));
				cb.showText("VOID ** VOID ** VOID");
				cb.endText();				
			}
			else {
				throw new RuntimeException("Unknown render mode: " + renderMode);
			}
		}
	}
	
	private static enum Alignment {
		LEFT,
		RIGHT,
		CENTER
	}
	
	private static class LabelBox {
		
		float x;
		float y;
		float width;
		float height;
		String label;
		String value;
		BaseFont labelFont;
		int labelFontSize;
		BaseFont valueFont;
		int valueFontSize;
		Alignment valueAlignment;
		
		void render(PdfContentByte cb) {
			
			cb.saveState();
			
			/*
			 * this is supposed to put a greyed-out shadow behind the label text,
			 * but it doesn't work very well on acrobat reader
			 */

//			PdfPatternPainter dot = cb.createPattern(5f, 5f, 3f, 3f, null);
//			dot.setLineWidth(1);
//
//			dot.moveTo(0, 5);
//			dot.lineTo(0, 4.5f);
//			dot.stroke();
//
//			dot.moveTo(5, 0);
//			dot.lineTo(4.5f, 0);
//			dot.stroke();
//
//			PatternColor dots = new PatternColor(dot); 
//
//			cb.setColorFill(dots);
//			cb.rectangle(x, y + (height / 2), width, height / 2);
//			cb.fill();

			cb.rectangle(x, y, width, height);
			cb.setLineWidth(.5f);
			cb.stroke();

			cb.beginText();
			cb.moveText(x, y);
			cb.setFontAndSize(labelFont, labelFontSize);
			cb.showTextAligned(PdfContentByte.ALIGN_CENTER, label, x + (width / 2), y + (height - 11), 0);
			
			cb.setFontAndSize(valueFont, valueFontSize);
			
			if (valueAlignment == Alignment.LEFT) {
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, value, x + 3, y + (valueFontSize / 2), 0);
			}
			else if (valueAlignment == Alignment.CENTER) {
				cb.showTextAligned(PdfContentByte.ALIGN_CENTER, value, x + (width / 2), y + (valueFontSize / 2), 0);
			}
			else if (valueAlignment == Alignment.RIGHT) {
				cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, value, x + width - 3, y + (valueFontSize / 2), 0);
			}
			
			cb.endText();
			
			cb.restoreState();
		}
	}
	
	private BaseFont createMicrFont() throws Exception {
		
		boolean cached = true;
		byte[] ttf;
		{
			InputStream in = CheckPdfRenderer.class.getResourceAsStream("/com/moss/check/us/GnuMICR.ttf");
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024 * 10]; //10k buffer
			for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
				out.write(buffer, 0, numRead);
			}

			ttf = out.toByteArray();
		}
		byte[] pfb = null; 
		boolean noThrow = false;
		
		BaseFont baseFont = BaseFont.createFont("GnuMICR.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, cached, ttf, pfb, noThrow);
		
		return baseFont;
	}
	
	private BaseFont createFixedFont() throws Exception {
		
		boolean cached = true;
		byte[] ttf;
		{
			InputStream in = CheckPdfRenderer.class.getResourceAsStream("/com/moss/check/us/VeraMono.ttf");
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024 * 10]; //10k buffer
			for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
				out.write(buffer, 0, numRead);
			}

			ttf = out.toByteArray();
		}
		byte[] pfb = null; 
		boolean noThrow = false;
		
		BaseFont baseFont = BaseFont.createFont("VeraMono.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, cached, ttf, pfb, noThrow);
		
		return baseFont;
	}
}
