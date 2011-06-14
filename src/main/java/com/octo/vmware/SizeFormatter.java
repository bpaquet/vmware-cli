package com.octo.vmware;

public class SizeFormatter {

	public static String formatSize(long size) {
		if (size < 1024) {
			return size + " B";
		}
		float s = size;
		s /= 1024;
		if (s < 1024) {
			return String.format("%.3f", s) + " kB";
		}
		s /= 1024;
		if (s < 1024) {
			return String.format("%.3f", s) + " MB";
		}
		s /= 1024;
		return String.format("%.3f", s) + " GB";
	}
	
	public static String formatSizeKb(long size) {
		return formatSize(size * 1024);
	}
	
}
