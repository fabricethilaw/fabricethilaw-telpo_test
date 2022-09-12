package com.telpo.tps550.api.demo.nfc;

import android.nfc.tech.IsoDep;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class NFC {
	// 声明ISO-DEP协议的Tag操作实例
	private final IsoDep tag;

	public NFC(IsoDep tag) throws IOException {
		// 初始化ISO-DEP协议的Tag操作类实例
		this.tag = tag;
		tag.setTimeout(5000);
		tag.connect();
	}
	
	public byte[] transmit(byte[] cmd) throws IOException{
		byte[] result = tag.transceive(cmd);
		return result;
	}
	
	/**
	* 将16进制字符串转换成汉字
	* @param str
	* @return
	*/
	public static String deUnicode(String str) {
		byte[] bytes = new byte[str.length() / 2];
		byte tempByte = 0;
		byte tempHigh = 0;
		byte tempLow = 0;
		for (int i = 0, j = 0; i < str.length(); i += 2, j++) {
			tempByte = (byte) (((int) str.charAt(i)) & 0xff);
			if (tempByte >= 48 && tempByte <= 57) {
				tempHigh = (byte) ((tempByte - 48) << 4);
			} else if (tempByte >= 97 && tempByte <= 101) {
				tempHigh = (byte) ((tempByte - 97 + 10) << 4);
			}
			tempByte = (byte) (((int) str.charAt(i + 1)) & 0xff);
			if (tempByte >= 48 && tempByte <= 57) {
				tempLow = (byte) (tempByte - 48);
			} else if (tempByte >= 97 && tempByte <= 101) {
				tempLow = (byte) (tempByte - 97 + 10);
			}
			bytes[j] = (byte) (tempHigh | tempLow);
		}
		String result = null;
		try {
			result = new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
