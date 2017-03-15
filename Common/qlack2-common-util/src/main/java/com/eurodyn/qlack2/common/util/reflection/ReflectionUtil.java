package com.eurodyn.qlack2.common.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utilities for Java Reflection.
 *
 */
public class ReflectionUtil {

	/**
	 * Sets a private field to the given value.
	 * @param target
	 * @param fieldName
	 * @param value
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setPrivateField(Object target, String fieldName, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = target.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(target, value);
	}
	
	/**
	 * Finds a method in a given class by name. This may be handy in situations
	 * where you do not know the type of the argument(s) of the method however
	 * it has a significant caveat you should be aware of: Since method
	 * arguments are not checked, overloaded methods can not be safely found
	 * using this method (in fact, the first method found is returned).
	 * 
	 * @return The first method that matched the given name, or null if a method
	 *         with the given name was not found.
	 */
	public static Method getMethodByName(Class clazz, String methodName) {
		Method method = null;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		
		return method;
	}
}
