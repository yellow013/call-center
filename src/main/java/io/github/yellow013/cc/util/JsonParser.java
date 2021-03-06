package io.github.yellow013.cc.util;

import static com.google.gson.JsonParser.parseString;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.JsonElement;

/**
 * 
 * @author yellow013
 * 
 * @see <a>https://github.com/yellow013/mercury/blob/master/serialization/serialization-json/src/main/java/io/mercury/serialization/json/JsonParser.java</a>
 *
 */
public final class JsonParser {

	private static final ObjectMapper Mapper = new ObjectMapper()
	// TODO 添加反序列化属性
	;

	private static final TypeFactory TypeFactory = Mapper.getTypeFactory()
	// TODO 添加配置信息
	;

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static JsonElement parseJson(String json) {
		return parseString(json);
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonArray(String json) {
		return parseString(json).isJsonArray();
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonObject(String json) {
		return parseString(json).isJsonObject();
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @return
	 * @throws JsonParseException
	 */

	public static final <T> T toObject(String json) throws JsonParseException {
		try {
			if (json == null)
				return null;
			return Mapper.readValue(json, new TypeReference<T>() {
			});
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 */

	public static final <T> T toObject(String json, Class<T> clazz) throws JsonParseException {
		try {
			if (json == null || clazz == null)
				return null;
			return Mapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws JsonParseException
	 */
	public static final <T> List<T> toList(String json) throws JsonParseException {
		try {
			return Mapper.readValue(json, new TypeReference<List<T>>() {
			});
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 */
	public static final <T> List<T> toList(String json, Class<T> clazz) throws JsonParseException {
		try {
			return Mapper.readValue(json, TypeFactory.constructCollectionLikeType(List.class, clazz));
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws JsonParseException
	 */
	public static final <K, V> Map<K, V> toMap(String json) throws JsonParseException {
		try {
			return Mapper.readValue(json, new TypeReference<Map<K, V>>() {
			});
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @param json
	 * @param keyClass
	 * @param valueClass
	 * @return
	 * @throws JsonParseException
	 */
	public static final <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass)
			throws JsonParseException {
		try {
			return Mapper.readValue(json, TypeFactory.constructMapLikeType(Map.class, keyClass, valueClass));
		} catch (Exception e) {
			throw new JsonParseException(json, e);
		}
	}

	/**
	 * 
	 * @author yellow013
	 *
	 */
	public static final class JsonParseException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9000408863460789219L;

		public JsonParseException(String json, Throwable throwable) {
			super("Parse JSON -> [" + json + "], Throw exception -> [" + throwable.getClass().getSimpleName() + "]",
					throwable);
		}

		public JsonParseException(Throwable throwable) {
			super("Parse JSON throw exception -> [" + throwable.getClass().getSimpleName() + "]", throwable);
		}

		public JsonParseException(String message) {
			super(message);
		}

	}

}
