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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.moss.usbanknumbers.AbaNumber;
import com.moss.usbanknumbers.FractionalRoutingNumber;
import com.moss.usbanknumbers.RoutingNumber;

public class TestCheckModelFactory {
	
	private CheckModelFactory factory;
	
	@Before
	public void before() throws Exception {
		
		File target = new File("target");
		
		if (!target.exists()) {
			if (!target.mkdir()) {
				throw new RuntimeException ("Cannot create target dir");
			}
		}
		
		factory = new CheckModelFactory();
		
		factory.setDrawerName("Checks-R-Us");
		factory.setDrawerAddressLine1("2048 Walrus St.");
		factory.setDrawerAddressLine2("Suite 3");
		factory.setDrawerCityStateZip("Chattanooga TN 37409");
		factory.setDrawerSignature(new FileInputStream("src/test/resources/com/moss/check/us/signature-small.jpg"));
		
		factory.setDraweeName("Wachovia Bank");
		factory.setDraweeCityStateZip("Las Vegas NV 57212");
		factory.setDraweeRoutingNumber("076401251");
		factory.setDraweeAccountNumber("002009412");
		
		factory.setPayeeName("John Q. Doe");
		factory.setPayeeAddressLine1("2047 Walrus St.");
		factory.setPayeeAddressLine2("Apt 2");
		factory.setPayeeCityStateZip("Nashville TN 37408");
		
		factory.setAmount(new BigDecimal("244.32"));
		factory.setDate(new SimpleDate(2007, 3, 21));
		factory.setCheckNumber("007402");
		factory.setLimitsText1("VOID AFTER 120 DAYS");
		factory.setLimitsText2("NOT GOOD FOR OVER $500.00");
		
		factory.setRoutingFraction(new FractionalRoutingNumber(AbaNumber.Prefix.New_York_NY, new RoutingNumber("076401251")));
	}
	
	@Test
	public void basicCheckStub() throws Exception {
		
		factory.setStubPrintMode(StubPrintMode.CHECK_DUPLICATE);
		
		run(factory);
	}
	
	@Test
	public void customCheckStub() throws Exception {
		
		factory.setStubPrintMode(StubPrintMode.CUSTOM);
		factory.setCustomStubPdf(new FileInputStream("src/test/resources/com/moss/check/us/custom-stub.pdf"));
		
		run(factory);
	}

	@Test
	public void nullSafety() throws Exception {
		
		CheckModelFactory factory = new CheckModelFactory();
		run(factory);
	}
	
	private void run(CheckModelFactory factory) throws Exception {
		CheckPdfRenderer renderer = new CheckPdfRenderer();
		File tempFile = File.createTempFile(getClass().getName(), ".pdf", new File("target"));
		renderer.render(factory.buildModel(), new FileOutputStream(tempFile));
		
//		Runtime.getRuntime().exec("gnome-open " + tempFile.getAbsolutePath());
	}
}
