package com.drumge.template;

public class StringUtil {
	
	public static byte[] hexStringConvert(final byte[] strHex)
	{
		byte[] strBin = new byte[strHex.length];
		byte finalByte = 0;
		boolean bHi = true;
		int i = 0;//hex
		int j = 0;//bin
		while (i < strHex.length) {
			int v;
			byte c = strHex[i];
			if (c >= '0' && c <= '9')
				v = c - '0';
			else if (c >= 'a' && c <= 'z')
				v = c - 'a' + 10;
			else if (c >= 'A' && c <= 'Z')
				v = c - 'A' + 10;
			else {
				i++;
				continue;
			}
			
			if (bHi) {
				finalByte = (byte)(v << 4);
				bHi = false;
			} else {
				finalByte |= (v & 0xf);
				bHi = true;
//				*(p++) = (finalByte & 0xff);
				strBin[j] = (byte) (finalByte & 0xff);
				j++;
				finalByte = 0;
			}
			i++;
		}
		byte[] strFinal = new byte[j];
		for (int index = 0; index < j; ++index) {
			strFinal[index] = strBin[index];
		}
		return strFinal;
	}
	

	private static String removeSpace(String IP) {//去掉IP字符串前后所有的空格
		while(IP.startsWith(" ")) {
			IP= IP.substring(1,IP.length()).trim();
		}
		while(IP.endsWith(" ")) {
			IP= IP.substring(0,IP.length()-1).trim();
		}
		return IP;
	}
		
	public static boolean isIp(String IP) {//判断是否是一个IP
		IP = removeSpace(IP);
		if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String s[] = IP.split("\\.");
			if(Integer.parseInt(s[0]) < 255
					&& Integer.parseInt(s[1])<255
					&& Integer.parseInt(s[2])<255
					&& Integer.parseInt(s[3])<255) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String exception2String(Exception e) {
		if (e == null) {
			return "";
		}
		String str = e.toString();
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}
}
