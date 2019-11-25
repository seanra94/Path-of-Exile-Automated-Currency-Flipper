package backend;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;



public class Utility {

	public static String sigDigRounder(double value, int nSigDig) {

		BigDecimal bd = new BigDecimal(value);
		bd = bd.round(new MathContext(nSigDig));
		return String.valueOf(bd.doubleValue());

	}
	
	public static String regex (String regex, String searchString) {
		Pattern p;
		try {
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} catch (PatternSyntaxException e) { 
			return ""; 
		}

		Matcher m = p.matcher(searchString);
		if (m.find()) {
			try {
				return m.group(1);
			} catch (IndexOutOfBoundsException e) { 
				return ""; 
			}
			
		} else { 
			return ""; 
		}
	}
	
	public static String[] regex (String regex, String searchString, String[] info) {
		Pattern p;
		try {
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} catch (PatternSyntaxException e) { 
			return null; 
		}

		Matcher m = p.matcher(searchString);
		if (m.find()) {
			for (int i = 0; i < info.length; i++) {
				try {
					info[i] =  m.group(i + 1);
				} catch (IndexOutOfBoundsException e) { 
					return null; 
				}
				
			}

		} else { 
			return null; 
		}
		return info;
	}
	
	public static String folderPath(String folderName) {
		try {
			return regex("(.*)(?:/.*.jar|/CurrencyExchange.*)", Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + "/Currency Exchange Resources/" + folderName;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static String icon() {
		return folderPath("POE.png");
	}
	
	public static String stashInfo() {
		return folderPath("CurrencyTabInfo.txt");
	}
	
}
