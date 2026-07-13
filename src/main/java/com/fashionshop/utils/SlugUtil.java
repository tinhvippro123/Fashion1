package com.fashionshop.utils;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import java.text.Normalizer;

import java.util.regex.Pattern;

public class SlugUtil {

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String makeSlug(String input) {
		if (input == null)
			throw new FashionShopException(ErrorCode.BAD_REQUEST, "Chuá»—i truyá»\ufffdn vÃ o Ä‘á»ƒ táº¡o Slug khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");

		// 1. Chuyá»ƒn Ä‘á»•i kÃ½ tá»± tiáº¿ng Viá»‡t Ä‘áº·c biá»‡t (Ä‘, Ä\ufffd) thá»§ cÃ´ng
		// VÃ¬ Normalizer khÃ´ng xá»­ lÃ½ triá»‡t Ä‘á»ƒ chá»¯ Ä‘
		String nowhitespace = input.trim().toLowerCase();
		nowhitespace = nowhitespace.replaceAll("Ä‘", "d");

		// 2. Chuáº©n hÃ³a chuá»—i (TÃ¡ch dáº¥u ra khá»\ufffdi chá»¯ cÃ¡i)
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

		// 3. DÃ¹ng Regex Ä‘á»ƒ loáº¡i bá»\ufffd cÃ¡c dáº¥u Ä‘Ã£ tÃ¡ch
		String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");

		// 4. Thay tháº¿ khoáº£ng tráº¯ng báº±ng gáº¡ch ngang
		slug = WHITESPACE.matcher(slug).replaceAll("-");

		// 5. Loáº¡i bá»\ufffd cÃ¡c kÃ½ tá»± khÃ´ng pháº£i chá»¯, sá»‘ hoáº·c gáº¡ch ngang
		slug = NONLATIN.matcher(slug).replaceAll("");

		// 6. Gá»™p nhiá»\ufffdu gáº¡ch ngang liÃªn tiáº¿p thÃ nh 1 (vÃ­ dá»¥: a--b -> a-b)
		slug = slug.replaceAll("-+", "-");

		// 7. Cáº¯t bá»\ufffd gáº¡ch ngang á»Ÿ Ä‘áº§u vÃ  cuá»‘i náº¿u cÃ³
		slug = slug.replaceAll("^-|-$", "");

		return slug;
	}
}