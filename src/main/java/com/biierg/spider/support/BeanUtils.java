package com.biierg.spider.support;

import java.beans.Transient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供JavaBean的序列化及反序列化方法
 * 
 * @author lei
 */
public class BeanUtils {

	// 用于从Bean中取值时的级联表达式
	private static final Pattern ChainExp = Pattern.compile("((\\w+)\\.)");

	/**
	 * 将字节数组反序列化成Java对象
	 * 
	 * @param objBytes
	 * @param objClass
	 * 
	 * @return Object
	 */
	public static <T> T bytes2Object(byte[] objBytes, Class<T> objClass) {

		try (ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
				ObjectInputStream ois = new ObjectInputStream(bais)) {
			return objClass.cast(ois.readObject());
		} catch (Throwable e) {
		}

		return null;
	}

	/**
	 * 将Java对象序列化成字节数组
	 * 
	 * @param obj
	 * @return bytes[]
	 */
	public static byte[] object2Bytes(Object obj) {

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {

			oos.writeObject(obj);

			return baos.toByteArray();
		} catch (Throwable e) {
		}

		return null;
	}

	public static String getGetterMethodName(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	public static String getSetterMethodName(String fieldName) {
		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	/**
	 * 从给定的Bean获取属性值；
	 * 
	 * @param bean
	 *            给定的Bean，可以是Map
	 * @param fldName
	 *            属性名称，支持级联表达式“.”
	 * @return
	 */
	public static <T> Object getFieldValue(T bean, String fldName) {

		try {
			if (bean != null) {

				// 级联表达式
				Matcher chainMatcher = ChainExp.matcher(fldName);
				if (chainMatcher.find()) {

					String firstName = chainMatcher.group(2);
					fldName = chainMatcher.replaceFirst("");

					return getFieldValue(getFieldValue(bean, firstName), fldName);
				} else {

					if (bean instanceof Map) {
						return Map.class.cast(bean).get(fldName);
					}

					Class<?> beanCls = bean.getClass();
					String getterMethodName = getGetterMethodName(fldName);

					try {
						Method getterMethod = beanCls.getMethod(getterMethodName);
						if (getterMethod != null) {
							return beanCls.getMethod(getterMethodName).invoke(bean);
						}
					} catch (Throwable e) {
					}

					Field field = beanCls.getDeclaredField(fldName);
					field.setAccessible(true);
					return field.get(bean);
				}
			}
		} catch (Throwable t) {
		}

		return null;
	}

	/**
	 * 设置单个属性
	 * 
	 * @param bean
	 * @param fieldName
	 * @param fieldType
	 * @param fieldValue
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public static <T> void setFieldValue(T bean, String fieldName, Class<?> fieldType, Object fieldValue)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException {

		try {
			String fieldSetterName = BeanUtils.getSetterMethodName(fieldName);
			Method fieldSetterMethod = bean.getClass().getMethod(fieldSetterName, fieldType);

			if (fieldSetterMethod != null) {
				fieldSetterMethod.invoke(bean, fieldValue);
			}
		} catch (Throwable e) {
		}

		Field field = bean.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(bean, fieldValue);
	}

	/**
	 * 判断给定的字段是否是常量（包含静态）
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isConstField(Field field) {
		return Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
	}

	/**
	 * 判断给定的字段是否是临时的（无须持久化的）
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isTransient(Field field) {

		return Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())
				|| field.getAnnotation(Transient.class) != null;
	}

	/**
	 * 拷贝全部属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 */
	public static <T> T copyProperties(T fromBean, T toBean) {
		return copyProperties(fromBean, toBean, null, null);
	}

	/**
	 * 拷贝部分属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 * @param ignoreFields
	 */
	public static <T> T copyProperties(T fromBean, T toBean, String[] ignoreFields) {
		return copyProperties(fromBean, toBean, ignoreFields, null);
	}

	/**
	 * 拷贝属性
	 * 
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 * @param ignoreFields
	 * @param limitFields
	 */
	public static <T> T copyProperties(T fromBean, T toBean, String[] ignoreFields, String[] limitFields) {

		if (fromBean == null || toBean == null) {
			throw new NullPointerException();
		}

		Field[] fields = fromBean.getClass().getDeclaredFields();

		if (fields == null || fields.length == 0) {
			return toBean;
		}

		// 为bean的各字段赋值
		fieldLoop: for (int i = 0, fieldCount = fields.length; i < fieldCount; i++) {
			String fieldName = fields[i].getName();

			// 跳过静态、常量字段
			if (isConstField(fields[i])) {
				continue fieldLoop;
			}

			// 忽略的字段
			if (ignoreFields != null) {

				for (String fld : ignoreFields) {

					if (fieldName.equals(fld)) {
						continue fieldLoop;
					}
				}
			}

			// 限制的字段
			if (limitFields != null) {

				boolean finded = false;
				for (String fld : limitFields) {

					if (fieldName.equals(fld)) {
						finded = true;
						break;
					}
				}

				if (!finded) {
					continue fieldLoop;
				}
			}

			// getter
			Method getterMethod = null;
			try {
				getterMethod = fromBean.getClass().getMethod(getGetterMethodName(fieldName));
			} catch (Throwable e) {
			}

			// setter
			Method setterMethod = null;
			try {
				setterMethod = toBean.getClass().getMethod(getSetterMethodName(fieldName), fields[i].getType());
			} catch (Throwable e) {
			}

			if (getterMethod != null && setterMethod != null) {

				try {
					setterMethod.invoke(toBean, getterMethod.invoke(fromBean));
				} catch (Throwable e) {
				}
			} else {
				Object fromFldValue = null;

				try {
					if (getterMethod == null) {
						fields[i].setAccessible(true);
						fromFldValue = fields[i].get(fromBean);
					}
				} catch (Throwable e) {
				}

				try {
					if (setterMethod == null) {
						Field toField = toBean.getClass().getDeclaredField(fieldName);
						toField.setAccessible(true);
						toField.set(toBean, fromFldValue);
					}
				} catch (Throwable e) {
				}
			}
		}

		return toBean;
	}
}
