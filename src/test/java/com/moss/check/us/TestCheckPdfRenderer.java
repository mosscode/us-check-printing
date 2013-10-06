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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class TestCheckPdfRenderer {
	
	@Before
	public void before() {
		
		File target = new File("target");
		
		if (!target.exists()) {
			if (!target.mkdir()) {
				throw new RuntimeException ("Cannot create target dir");
			}
		}
	}

	@Test
	public void basic() throws Exception {
		
		CheckModel model = new CheckModel();
		
		model.drawerNameAddressLines = new String[] {
			"Checks-R-Us",
			"2048 Walrus St.",
			"Suite 3",
			"Chattanooga TN 37409"
		};
		
		model.draweeName = "Wachovia Bank";
		model.draweeCityStateZip = "Las Vegas NV 57212";
		model.routingFraction = "56-250/442";
		
		model.amountVerbose = "PAY: ONE HUNDRED DOLLARS & NO CENT";
		
		model.date = "03/21/2007";
		model.checkNumber = "0791";
		model.amount = "$************100.00";
		
		model.payeeNameAddressLines = new String[] {
			"John Q. Doe",
			"2047 Walrus St.",
			"Apt 2",
			"Nashville TN 37408"
		};
		
		model.limitsText1 = "VOID AFTER 120 DAYS";
		model.limitsText2 = "NOT GOOD FOR OVER $500.00";
		
		byte[] signature;
		{
			InputStream in = new FileInputStream("src/test/resources/com/moss/check/us/other.png");
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024 * 10]; //10k buffer
			for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
				out.write(buffer, 0, numRead);
			}

			signature = out.toByteArray();
		}
		model.signature = signature;
		
		model.transit = "076401251";
		model.onUs = "002009111";
		model.auxOnUs = "00021";
		
		model.stubPrintMode = StubPrintMode.CHECK_DUPLICATE;
		
		CheckPdfRenderer renderer = new CheckPdfRenderer();
		File tempFile = File.createTempFile(getClass().getName(), ".pdf", new File("target"));
		renderer.render(model, new FileOutputStream(tempFile));
		
//		Runtime.getRuntime().exec("gnome-open " + tempFile.getAbsolutePath());
	}
}
