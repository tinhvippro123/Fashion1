package com.fashionshop.utils;

import java.text.Normalizer;

import java.util.regex.Pattern;

public class SlugUtil {

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String makeSlug(String input) {
		if (input == null)
			throw new IllegalArgumentException();

		// 1. Chuyển đổi ký tự tiếng Việt đặc biệt (đ, Đ) thủ công
		// Vì Normalizer không xử lý triệt để chữ đ
		String nowhitespace = input.trim().toLowerCase();
		nowhitespace = nowhitespace.replaceAll("đ", "d");

		// 2. Chuẩn hóa chuỗi (Tách dấu ra khỏi chữ cái)
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

		// 3. Dùng Regex để loại bỏ các dấu đã tách
		String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");

		// 4. Thay thế khoảng trắng bằng gạch ngang
		slug = WHITESPACE.matcher(slug).replaceAll("-");

		// 5. Loại bỏ các ký tự không phải chữ, số hoặc gạch ngang
		slug = NONLATIN.matcher(slug).replaceAll("");

		// 6. Gộp nhiều gạch ngang liên tiếp thành 1 (ví dụ: a--b -> a-b)
		slug = slug.replaceAll("-+", "-");

		// 7. Cắt bỏ gạch ngang ở đầu và cuối nếu có
		slug = slug.replaceAll("^-|-$", "");

		return slug;
	}
}