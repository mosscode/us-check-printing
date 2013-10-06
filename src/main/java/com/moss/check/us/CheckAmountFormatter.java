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

import java.math.BigDecimal;

public 	class CheckAmountFormatter {
	public String format(BigDecimal amount) {
		return getStringAmount(amount);
	}
	
	private String getStringAmount(BigDecimal amount){
		String sValue = "";
		String s = getHundredValue(4,amount);
		if(!s.equals(""))
			sValue = s + " " + "BILLION";
		
		s = getHundredValue(3,amount);
		if(!s.equals("")){
			if(!sValue.equals(""))
				sValue = sValue + " " + s + " " + "MILLION";
			else
				sValue = s + " " + "MILLION";
		}
		
		s = getHundredValue(2,amount);
		if(!s.equals("")){
			if(!sValue.equals(""))
				sValue = sValue + " " + s + " " + "THOUSAND";
			else
				sValue = s + " " + "THOUSAND";
		}
		
		s = getHundredValue(1,amount);
		if(!s.equals("")){
			if(!sValue.equals(""))
				sValue = sValue + " " + s;
			else
				sValue = s;
		}
		
		if(!sValue.equals("")){
			if(sValue.equals("ONE"))
				sValue = sValue + " " + "DOLLAR";
			else
				sValue = sValue + " " + "DOLLARS";
		}
		
		String centValue = getCentValue(amount);
		if(centValue.equals("NO") || centValue.equals("ONE"))
			centValue = centValue + " " + "CENT";
		else
			centValue = centValue + " " + "CENTS";
		
		if(!sValue.equals(""))
			sValue = sValue + " " + "&" + " " + centValue;
		else
			sValue = centValue;
		
		return sValue;
	}
	
	private String getHundredValue(int index,BigDecimal value){
		String s = "";
		int i = 0;
		
		double dValue = value.doubleValue();
		if(index == 1){
			dValue = dValue % 1000;
		} else if(index == 2){
			dValue = dValue % 1000000;
			dValue = dValue / 1000;
		} else if(index == 3){
			dValue = dValue % 1000000000;
			dValue = dValue / 1000000;
		} else if(index == 4){
			dValue = dValue % 1000000000000.00;
			dValue = dValue / 1000000000;
		}
		
		if(dValue < 1)
			return "";
		
		double tempValue = dValue / 100;
		i = (int)tempValue;
		if(i == 0)
			s = "";
		else if(i == 1)
			s = "ONE";
		else if(i == 2)
			s = "TWO";
		else if(i == 3)
			s = "THREE";
		else if(i == 4)
			s = "FOUR";
		else if(i == 5)
			s = "FIVE";
		else if(i == 6)
			s = "SIX";
		else if(i == 7)
			s = "SEVEN";
		else if(i == 8)
			s = "EIGHT";
		else if(i == 9)
			s = "NINE";
		
		if(!s.equals(""))
			s = s + " " + "HUNDRED";
		
		tempValue = dValue % 100;
		
		String s3 = getTenValue(tempValue);
		
		if(!s3.equals("")){
			if(!s.equals(""))
				s = s + " " + s3;
			else
				s = s3;
		}
		
		return s;
		
	}
	
	private String getTenValue(double dValue){
		double tempValue = dValue / 10;
		int i = (int)tempValue;
		
		String s2 = "";
		if(i == 0)
			s2 = "";
		else if(i == 1)
			s2 = "TEN";
		else if(i == 2)
			s2 = "TWENTY";
		else if(i == 3)
			s2 = "THIRTY";
		else if(i == 4)
			s2 = "FOURTY";
		else if(i == 5)
			s2 = "FIFTY";
		else if(i == 6)
			s2 = "SIXTY";
		else if(i == 7)
			s2 = "SEVENTY";
		else if(i == 8)
			s2 = "EIGHTY";
		else if(i == 9)
			s2 = "NINTY";
		
		tempValue = dValue % 10;
		i = (int)tempValue;
		
		String s3 = "";
		if(s2.equals("TEN")){
			if(i == 0)
				s3 = "TEN";
			else if(i == 1)
				s3 = "ELEVEN";
			else if(i == 2)
				s3 = "TWELVE";
			else if(i == 3)
				s3 = "THIRTEEN";
			else if(i == 4)
				s3 = "FOURTEEN";
			else if(i == 5)
				s3 = "FIFTEEN";
			else if(i == 6)
				s3 = "SIXTEEN";
			else if(i == 7)
				s3 = "SEVENTEEN";
			else if(i == 8)
				s3 = "EIGHTEEN";
			else if(i == 9)
				s3 = "NINTEEN";
		} else{
			if(i == 0)
				s3 = "";
			else if(i == 1)
				s3 = "ONE";
			else if(i == 2)
				s3 = "TWO";
			else if(i == 3)
				s3 = "THREE";
			else if(i == 4)
				s3 = "FOUR";
			else if(i == 5)
				s3 = "FIVE";
			else if(i == 6)
				s3 = "SIX";
			else if(i == 7)
				s3 = "SEVEN";
			else if(i == 8)
				s3 = "EIGHT";
			else if(i == 9)
				s3 = "NINE";
			
			if(!s2.equals("")){
				if(!s3.equals(""))
					s3 = s2 + " " + s3;
				else
					s3 = s2;
			}
		}
		
		return s3;
	}
	
	private String getCentValue(BigDecimal value){
		String sValue = value.toString();
		int index = sValue.indexOf(".");
		sValue = sValue.substring(index + 1,index + 3);
		value = new BigDecimal(sValue);
		
		double dValue = value.doubleValue();
		
		if(dValue <= 0)
			return "NO";
		else{
			String s = getTenValue(dValue);
			if(s.equals(""))
				s = "NO";
			return s;
		}
	}
}
