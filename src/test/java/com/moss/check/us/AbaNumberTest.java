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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.moss.usbanknumbers.AbaNumber;
import com.moss.usbanknumbers.AbaNumberException;
import com.moss.usbanknumbers.RoutingNumber;
import com.moss.usbanknumbers.RoutingNumberException;


public class AbaNumberTest extends TestCase{
	public void testRun(){
		
		try {
			testSuffix(AbaNumber.Prefix.Ohio, new RoutingNumber("044202505"), "250", "56-250");
			testSuffix(AbaNumber.Prefix.Ohio, new RoutingNumber("244172095"), "7209", "56-7209");
			testSuffix(AbaNumber.Prefix.Cleveland_OH, new RoutingNumber("041000124"), "12", "6-12");
			testSuffix(AbaNumber.Prefix.West_Virginia, new RoutingNumber("043400036"), "3", "69-3");
		} catch (RoutingNumberException e) {
			throw new RuntimeException(e);
		}
		
		assertInvalidSuffix("12345");
		assertInvalidSuffix("123a");
		assertInvalidSuffix("a123");
		assertInvalidSuffix("12 3");
		assertInvalidSuffix("abcd");
		assertInvalidSuffix(null);
		assertInvalidSuffix("");
		assertInvalidSuffix(" ");
		assertInvalidSuffix("    ");
		
		assertValidSuffix("1"); // this is questionable, but I have no solid info indicating that this is impossible
		assertValidSuffix("12");
		assertValidSuffix("123");
		assertValidSuffix("1234");
		
		assertInvalid("fdsafdsafdsa");
		assertInvalid("69-3/494");
		assertInvalid("1234-1234");
		assertInvalid("a-2323");
		assertInvalid("33-232a");
	}
	
	private void assertInvalid(String abaText){
		try {
			new AbaNumber(abaText);
			throw new AssertionFailedError("Should have chocked on \"" + abaText + "\"");
		} catch (AbaNumberException e) {
			// expected
		}
	}
	private void testSuffix(AbaNumber.Prefix prefix, RoutingNumber number, String expectedSuffix, String expectedToString){
		AbaNumber aba = new AbaNumber(prefix, number);
		assertEquals(expectedSuffix, aba.suffix());
		assertEquals(expectedToString, aba.toString());
		try {
			aba = new AbaNumber(expectedToString);
			
		} catch (AbaNumberException e) {
			throw new AssertionFailedError("Unable to parse ABA:" + e.getMessage());
		}
		assertEquals(prefix, aba.prefix());
		assertEquals(expectedSuffix, aba.suffix());
		assertEquals(expectedToString, aba.toString());
	}
	
	private void assertValidSuffix(String suffix){
		try {
			new AbaNumber(AbaNumber.Prefix.Alabama, suffix);
		} catch (AbaNumberException e) {
			e.printStackTrace();
			throw new AssertionFailedError("Chocked on suffix \"" + suffix + "\".  Message:" + e.getMessage());
		}
	}
	private void assertInvalidSuffix(String suffix){
		try {
			new AbaNumber(AbaNumber.Prefix.Alabama, suffix);
			throw new AssertionFailedError("Should have chocked on suffix \"" + suffix + "\"");
		} catch (AbaNumberException e) {
			// expected
		}
	}
}
