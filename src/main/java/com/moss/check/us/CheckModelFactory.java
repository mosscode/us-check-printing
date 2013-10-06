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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.moss.usbanknumbers.AccountNumber;
import com.moss.usbanknumbers.AccountNumberException;
import com.moss.usbanknumbers.CheckNumber;
import com.moss.usbanknumbers.CheckNumberException;
import com.moss.usbanknumbers.FractionalRoutingNumber;
import com.moss.usbanknumbers.RoutingNumber;
import com.moss.usbanknumbers.RoutingNumberException;

public class CheckModelFactory {
	
	String drawerName;
	String drawerAddressLine1;
	String drawerAddressLine2;
	String drawerCityStateZip;
	byte[] drawerSignature;
	
	String draweeName;
	String draweeAddressLine1;
	String draweeAddressLine2;
	String draweeCityStateZip;
	
	FractionalRoutingNumber routingFraction;
	RoutingNumber draweeRoutingNumber;
	AccountNumber draweeAccountNumber;
	
	String payeeName;
	String payeeAddressLine1;
	String payeeAddressLine2;
	String payeeCityStateZip;
	
	SimpleDate date;
	CheckNumber checkNumber;
	BigDecimal amount;
	String limitsText1;
	String limitsText2;
	
	StubPrintMode stubPrintMode;
	byte[] customStubPdf;
	
	String transit;
	String onUs;
	String auxOnUs;
	
	public void setDrawerName(String drawerName) {
		this.drawerName = drawerName;
	}

	public void setDrawerAddressLine1(String drawerAddressLine1) {
		this.drawerAddressLine1 = drawerAddressLine1;
	}

	public void setDrawerAddressLine2(String drawerAddressLine2) {
		this.drawerAddressLine2 = drawerAddressLine2;
	}

	public void setDrawerCityStateZip(String drawerCityStateZip) {
		this.drawerCityStateZip = drawerCityStateZip;
	}

	public void setDrawerSignature(InputStream in) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024 * 10]; //10k buffer
		for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
			out.write(buffer, 0, numRead);
		}

		this.drawerSignature = out.toByteArray();
	}
	
	public void setDraweeName(String draweeName) {
		this.draweeName = draweeName;
	}
	public void setDraweeAddressLine1(String draweeAddressLine1) {
		this.draweeAddressLine1 = draweeAddressLine1;
	}
	public void setDraweeAddressLine2(String draweeAddressLine2) {
		this.draweeAddressLine2 = draweeAddressLine2;
	}
	public void setDraweeCityStateZip(String draweeCityStateZip) {
		this.draweeCityStateZip = draweeCityStateZip;
	}
	
	public void setRoutingFraction(FractionalRoutingNumber routingFraction) {
		this.routingFraction = routingFraction;
	}
	
	public void setDraweeRoutingNumber(String draweeRoutingNumber) throws RoutingNumberException {
		this.draweeRoutingNumber = new RoutingNumber(draweeRoutingNumber);
	}

	public void setDraweeAccountNumber(String draweeAccountNumber) throws AccountNumberException {
		this.draweeAccountNumber = new AccountNumber(draweeAccountNumber);
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public void setPayeeAddressLine1(String payeeAddressLine1) {
		this.payeeAddressLine1 = payeeAddressLine1;
	}

	public void setPayeeAddressLine2(String payeeAddressLine2) {
		this.payeeAddressLine2 = payeeAddressLine2;
	}

	public void setPayeeCityStateZip(String payeeCityStateZip) {
		this.payeeCityStateZip = payeeCityStateZip;
	}

	public void setDate(SimpleDate date) {
		this.date = date;
	}

	public void setCheckNumber(String checkNumber) throws CheckNumberException {
		this.checkNumber = new CheckNumber(checkNumber);
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setLimitsText1(String limitsText1) {
		this.limitsText1 = limitsText1;
	}

	public void setLimitsText2(String limitsText2) {
		this.limitsText2 = limitsText2;
	}
	
	public void setStubPrintMode(StubPrintMode stubPrintMode) {
		this.stubPrintMode = stubPrintMode;
	}
	
	public void setCustomStubPdf(InputStream in) throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024 * 10]; //10k buffer
		for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
			out.write(buffer, 0, numRead);
		}

		this.customStubPdf = out.toByteArray();
	}
	
	public CheckModel buildModel() {
		
		CheckModel model = new CheckModel();
		
		{
			List<String> lines = new ArrayList<String>();
			
			if (drawerName != null) {
				lines.add(drawerName.trim());
			}
			
			if (drawerAddressLine1 != null) {
				lines.add(drawerAddressLine1.trim());
			}
			
			if (drawerAddressLine2 != null) {
				lines.add(drawerAddressLine2.trim());
			}
			
			if (drawerCityStateZip != null) {
				lines.add(drawerCityStateZip.trim());
			}
			
			model.drawerNameAddressLines = lines.toArray(new String[0]);
		}
		
		if (draweeName != null) {
			model.draweeName = draweeName.trim();
		}
		else {
			model.draweeName = "";
		}
		
		if (draweeCityStateZip != null) {
			model.draweeCityStateZip = draweeCityStateZip.trim();
		}
		else {
			model.draweeCityStateZip = "";
		}
		
		model.routingFraction = routingFraction==null?"":routingFraction.toString();
		
		if (draweeRoutingNumber != null) {
			model.transit = draweeRoutingNumber.toString();
		} else {
			model.transit = "";
		}
		
		if (date != null) {
			
			String month = leftPad(Integer.toString(date.month), '0', 2);
			String day = leftPad(Integer.toString(date.day), '0', 2);
			String year = Integer.toString(date.year);
			
			model.date = month + "/" + day + "/" + year; 
		}
		else {
			model.date = "";
		}
		
		if (checkNumber != null) {
			model.checkNumber = checkNumber.toString();
			model.auxOnUs = checkNumber.toString();
		}
		else {
			model.checkNumber = "";
			model.auxOnUs = "";
		}
		
		if (amount != null) {
			model.amount = "$" + leftPad(amount.setScale(2).toString(), '*', 18);
			model.amountVerbose = "PAY: " + new CheckAmountFormatter().format(amount);
		}
		else {
			model.amount = "";
			model.amountVerbose = "";
		}
		
		{
			List<String> lines = new ArrayList<String>();
			
			if (payeeName != null) {
				lines.add(payeeName.trim());
			}
			
			if (payeeAddressLine1 != null) {
				lines.add(payeeAddressLine1.trim());
			}
			
			if (payeeAddressLine2 != null) {
				lines.add(payeeAddressLine2.trim());
			}
			
			if (payeeCityStateZip != null) {
				lines.add(payeeCityStateZip.trim());
			}
			
			model.payeeNameAddressLines = lines.toArray(new String[0]);
		}
		
		if (limitsText1 != null) {
			model.limitsText1 = limitsText1.trim();
		}
		else {
			model.limitsText1 = "";
		}
		
		if (limitsText2 != null) {
			model.limitsText2 = limitsText2.trim();
		}
		else {
			model.limitsText2 = "";
		}
		
		model.signature = drawerSignature;

		if (stubPrintMode != null) {
			
			if (StubPrintMode.CUSTOM == stubPrintMode && customStubPdf == null) {
				throw new RuntimeException("Cannot print a custom stub if customStubPdf is not supplied.");
			}
			
			model.customStubPdf = customStubPdf;
			model.stubPrintMode = stubPrintMode;
		}
		else {
			model.stubPrintMode = StubPrintMode.CHECK_DUPLICATE;
		}
		
		if (draweeAccountNumber != null) {
			model.onUs = draweeAccountNumber.toString();
		}
		else {
			model.onUs = "";
		}
		
		return model;
	}
	
	private String leftPad(String s, char c, int length) {
		
		int charsToAdd = length - s.length();
		
		if (charsToAdd < 1) {
			return s;
		}
		
		for (int i=0; i<charsToAdd; i++) {
			s = c + s;
		}
		
		return s;
	}
}
