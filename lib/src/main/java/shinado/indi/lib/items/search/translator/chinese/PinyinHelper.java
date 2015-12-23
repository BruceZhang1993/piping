package shinado.indi.lib.items.search.translator.chinese;

import android.content.Context;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 汉字转拼音类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 * @version 1.0
 */
public class PinyinHelper {
	private Properties PINYIN_TABLE;
	private Properties MUTIL_PINYIN_TABLE;
	private Properties CHINESE_TABLE;
	
	private final String PINYIN_SEPARATOR = ","; // 拼音分隔符
	private final String ALL_UNMARKED_VOWEL = "aeiouv";
	private final String ALL_MARKED_VOWEL = "āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ"; // 所有带声调的拼音字母

	public PinyinHelper(Context context){
		PINYIN_TABLE = PinyinResource.getPinyinTable(context);
		MUTIL_PINYIN_TABLE = PinyinResource.getMutilPintinTable(context);
		CHINESE_TABLE = PinyinResource.getChineseTable(context);
	}

	/**
	 * 将带声调格式的拼音转换为数字代表声调格式的拼音
	 * 
	 * @param pinyinArrayString
	 *            带声调格式的拼音
	 * @return 数字代表声调格式的拼音
	 */
	private String[] convertWithToneNumber(String pinyinArrayString) {
		String[] pinyinArray = pinyinArrayString.split(PINYIN_SEPARATOR);
		for (int i = pinyinArray.length - 1; i >= 0; i--) {
			boolean hasMarkedChar = false;
			String originalPinyin = pinyinArray[i].replaceAll("ü", "v"); // 将拼音中的ü替换为v

			for (int j = originalPinyin.length() - 1; j >= 0; j--) {
				char originalChar = originalPinyin.charAt(j);

				// 搜索带声调的拼音字母，如果存在则替换为对应不带声调的英文字母
				if (originalChar < 'a' || originalChar > 'z') {
					int indexInAllMarked = ALL_MARKED_VOWEL.indexOf(originalChar);
					int toneNumber = indexInAllMarked % 4 + 1; // 声调数
					char replaceChar = ALL_UNMARKED_VOWEL.charAt(((indexInAllMarked - indexInAllMarked % 4)) / 4);
					pinyinArray[i] = originalPinyin.replaceAll(String.valueOf(originalChar), String.valueOf(replaceChar)) + toneNumber;
					hasMarkedChar = true;
					break;
				}
			}
			if (!hasMarkedChar) {
				// 找不到带声调的拼音字母说明是轻声，用数字5表示
				pinyinArray[i] = originalPinyin + "5";
			}
		}

		return pinyinArray;
	}

	/**
	 * 将带声调格式的拼音转换为不带声调格式的拼音
	 * 
	 * @param pinyinArrayString
	 *            带声调格式的拼音
	 * @return 不带声调的拼音
	 */
	private String[] convertWithoutTone(String pinyinArrayString) {
		String[] pinyinArray;
		for (int i = ALL_MARKED_VOWEL.length() - 1; i >= 0; i--) {
			char originalChar = ALL_MARKED_VOWEL.charAt(i);
			char replaceChar = ALL_UNMARKED_VOWEL.charAt(((i - i % 4)) / 4);
			pinyinArrayString = pinyinArrayString.replaceAll(String.valueOf(originalChar), String.valueOf(replaceChar));
		}
		// 将拼音中的ü替换为v
		pinyinArray = pinyinArrayString.replaceAll("ü", "v").split(PINYIN_SEPARATOR);

		// 去掉声调后的拼音可能存在重复，做去重处理
		Set<String> pinyinSet = new LinkedHashSet<String>();
		for (String pinyin : pinyinArray) {
			pinyinSet.add(pinyin);
		}

		return pinyinSet.toArray(new String[pinyinSet.size()]);
	}

	/**
	 * 将带声调的拼音格式化为相应格式的拼音
	 * 
	 * @param pinyinString
	 *            带声调的拼音
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，
	 *            WITH_TONE_MARK--带声调
	 * @return 格式转换后的拼音
	 */
	private String[] formatPinyin(String pinyinString, PinyinFormat pinyinFormat) {
		if (pinyinFormat == PinyinFormat.WITH_TONE_MARK) {
			return pinyinString.split(PINYIN_SEPARATOR);
		} else if (pinyinFormat == PinyinFormat.WITH_TONE_NUMBER) {
			return convertWithToneNumber(pinyinString);
		} else if (pinyinFormat == PinyinFormat.WITHOUT_TONE) {
			return convertWithoutTone(pinyinString);
		}
		return null;
	}

	/**
	 * 将单个汉字转换为相应格式的拼音
	 * 
	 * @param c
	 *            需要转换成拼音的汉字
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，
	 *            WITH_TONE_MARK--带声调
	 * @return 汉字的拼音
	 */
	public String[] convertToPinyinArray(char c, PinyinFormat pinyinFormat) {
		String pinyin = PINYIN_TABLE.getProperty(String.valueOf(c));
		if ((pinyin != null) && (!pinyin.equals("null"))) {
			return formatPinyin(pinyin, pinyinFormat);
		}
		return null;
	}

	/**
	 * 将单个汉字转换成带声调格式的拼音
	 * 
	 * @param c
	 *            需要转换成拼音的汉字
	 * @return 字符串的拼音
	 */
	public String[] convertToPinyinArray(char c) {
		return convertToPinyinArray(c, PinyinFormat.WITH_TONE_MARK);
	}

	/**
	 * 将字符串转换成拼音
	 *
	 * @param str
	 *            需要转换成拼音的字符串
	 * @return 字符串的拼音
	 */
	public String[] convertToPinyinArray(String str){
		if (!containsChinese(str)){
			return null;
		}

		String[] pinying = new String[str.length()];
		for (int i=0; i<pinying.length; i++){
			char c = str.charAt(i);
			if(isChinese(c)){
				pinying[i] = convertToPinyinString(""+c, "", PinyinFormat.WITHOUT_TONE);
			}else{
				pinying[i] = (c+"").toLowerCase();
			}
		}
		return pinying;
	}

	public boolean containsChinese(String str){
		for (int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			if (isChinese(c))
				return true;
		}
		return false;
	}

	/**
	 * 将字符串转换成相应格式的拼音
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param separator
	 *            拼音分隔符
	 * @param pinyinFormat
	 *            拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，
	 *            WITH_TONE_MARK--带声调
	 * @return 字符串的拼音
	 */
	public String convertToPinyinString(String str, String separator, PinyinFormat pinyinFormat) {
		str = convertToSimplifiedChinese(str);
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			if (isChinese(c) || c == '〇') // 判断是否为汉字或者〇
			{
				// 多音字识别处理
				boolean isFoundFlag = false;
				int rightMove = 3;

				// 将当前汉字依次与后面的3个、2个、1个汉字组合，判断下是否存在多音字词组
				for (int rightIndex = (i + rightMove) < len ? (i + rightMove) : (len - 1); rightIndex > i; rightIndex--) {
					String cizu = str.substring(i, rightIndex + 1);
					if (MUTIL_PINYIN_TABLE.containsKey(cizu)) {
						String[] pinyinArray = formatPinyin(MUTIL_PINYIN_TABLE.getProperty(cizu), pinyinFormat);
						for (int j = 0, l = pinyinArray.length; j < l; j++) {
							sb.append(pinyinArray[j]);
							if (j < l - 1) {
								sb.append(separator);
							}
						}
						i = rightIndex;
						isFoundFlag = true;
						break;
					}
				}
				if (!isFoundFlag) {
					String[] pinyinArray = convertToPinyinArray(str.charAt(i), pinyinFormat);
					if (pinyinArray != null) {
						sb.append(pinyinArray[0]);
					} else {
						sb.append(str.charAt(i));
					}
				}
				if (i < len - 1) {
					sb.append(separator);
				}
			} else {
				sb.append(c);
				if ((i + 1) < len && isChinese(str.charAt(i + 1))) {
					sb.append(separator);
				}
			}

		}
		return sb.toString();
	}

	/**
	 * 将字符串转换成带声调格式的拼音
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param separator
	 *            拼音分隔符
	 * @return 转换后带声调的拼音
	 */
	public String convertToPinyinString(String str, String separator) {
		return convertToPinyinString(str, separator, PinyinFormat.WITH_TONE_MARK);
	}

	/**
	 * 判断一个汉字是否为多音字
	 * 
	 * @param c
	 *            汉字
	 * @return 判断结果，是汉字返回true，否则返回false
	 */
	public boolean hasMultiPinyin(char c) {
		String[] pinyinArray = convertToPinyinArray(c);
		if (pinyinArray != null && pinyinArray.length > 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取字符串对应拼音的首字母
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 对应拼音的首字母
	 */
	public String getShortPinyin(String str) {
		String separator = "#"; // 使用#作为拼音分隔符
		StringBuilder sb = new StringBuilder();

		char[] charArray = new char[str.length()];
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);

			// 首先判断是否为汉字或者〇，不是的话直接将该字符返回
			if (!isChinese(c) && c != '〇') {
				charArray[i] = c;
			} else {
				int j = i + 1;
				sb.append(c);

				// 搜索连续的汉字字符串
				while (j < len && (isChinese(str.charAt(j)) || str.charAt(j) == '〇')) {
					sb.append(str.charAt(j));
					j++;
				}
				String hanziPinyin = convertToPinyinString(sb.toString(), separator, PinyinFormat.WITHOUT_TONE);
				String[] pinyinArray = hanziPinyin.split(separator);
				for (String string : pinyinArray) {
					charArray[i] = string.charAt(0);
					i++;
				}
				i--;
				sb.delete(0, sb.toString().length());
				sb.trimToSize();
			}
		}
		return String.valueOf(charArray);
	}

	/**
	 * 将单个繁体字转换为简体字
	 *
	 * @param c
	 *            需要转换的繁体字
	 * @return 转换后的简体字
	 */
	public char convertToSimplifiedChinese(char c) {
		if (isTraditionalChinese(c)) {
			return CHINESE_TABLE.getProperty(String.valueOf(c)).charAt(0);
		}
		return c;
	}

	/**
	 * 将单个简体字转换为繁体字
	 *
	 * @param c
	 *            需要转换的简体字
	 * @return 转换后的繁字体
	 */
	public char convertToTraditionalChinese(char c) {
		String hanzi = String.valueOf(c);
		if (CHINESE_TABLE.containsValue(hanzi)) {
			Iterator<Map.Entry<Object, Object>> itr = CHINESE_TABLE.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<Object, Object> e = itr.next();
				if (e.getValue().toString().equals(hanzi)) {
					return e.getKey().toString().charAt(0);
				}
			}
		}
		return c;
	}

	/**
	 * 将繁体字转换为简体字
	 *
	 * @param str
	 *            需要转换的繁体字
	 * @return 转换后的简体体
	 */
	public String convertToSimplifiedChinese(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			sb.append(convertToSimplifiedChinese(c));
		}
		return sb.toString();
	}

	/**
	 * 将简体字转换为繁体字
	 *
	 * @param str
	 *            需要转换的简体字
	 * @return 转换后的繁字体
	 */
	public String convertToTraditionalChinese(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			sb.append(convertToTraditionalChinese(c));
		}
		return sb.toString();
	}

	/**
	 * 判断某个字符是否为繁体字
	 *
	 * @param c
	 *            需要判断的字符
	 * @return 是繁体字返回true，否则返回false
	 */
	public boolean isTraditionalChinese(char c) {
		return CHINESE_TABLE.containsKey(String.valueOf(c));
	}

	/**
	 * 判断某个字符是否为汉字
	 *
	 * @param c
	 *            需要判断的字符
	 * @return 是汉字返回true，否则返回false
	 */
	public boolean isChinese(char c) {
		String regex = "[\\u4e00-\\u9fa5]";
		return String.valueOf(c).matches(regex);
	}

	public void destroy(){
		CHINESE_TABLE = null;
		MUTIL_PINYIN_TABLE = null;
		PINYIN_TABLE = null;
	}
}
