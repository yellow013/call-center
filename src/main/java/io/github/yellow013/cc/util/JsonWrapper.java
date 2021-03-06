package io.github.yellow013.cc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author yellow013
 * 
 * @see <a>https://github.com/yellow013/mercury/blob/master/serialization/serialization-json/src/main/java/io/mercury/serialization/json/JsonWrapper.java</a>
 *
 */
public final class JsonWrapper {

	// 普通JSON序列化
	private static final Gson Gson = new GsonBuilder().create();

	// JSON序列化, 包含Null值
	private static final Gson GsonHasNulls = new GsonBuilder().serializeNulls().create();

	// 以较高可视化的格式返回JSON
	private final static Gson GsonPrettyPrinting = new GsonBuilder().setPrettyPrinting().create();

	// 以漂亮的格式返回JSON, 包含Null值
	private static final Gson GsonPrettyPrintingHasNulls = new GsonBuilder().serializeNulls().setPrettyPrinting()
			.create();

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static final String toJson(Object obj) {
		return obj == null ? "null" : Gson.toJson(obj);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static final String toJsonHasNulls(Object obj) {
		return obj == null ? "null" : GsonHasNulls.toJson(obj);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static final String toPrettyJson(Object obj) {
		return obj == null ? "null" : GsonPrettyPrinting.toJson(obj);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static final String toPrettyJsonHasNulls(Object obj) {
		return obj == null ? "null" : GsonPrettyPrintingHasNulls.toJson(obj);
	}

}
