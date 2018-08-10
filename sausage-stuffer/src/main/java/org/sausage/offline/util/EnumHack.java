package org.sausage.offline.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.wm.lang.ns.NSWSDescriptor;

import sun.misc.Unsafe;

/**
 * Superb hack found on the web
 * <p>
 * 
 * <pre>
 * https://blog.gotofinal.com/java/diorite/2017/06/24/dynamic-enum.html
 * https://gist.githubusercontent.com/GotoFinal/74393bbc88d2b89646c93a9617e04795/raw/58c104dd58d4fd724a312bd800fa543c99e9e6bf/EnumHack.java
 * </pre>
 * 
 * Slightly adapted to be more generic.
 */
@SuppressWarnings({ "rawtypes", "restriction" })
public class EnumHack {
	
	public static void main(String[] args) throws Throwable {

		Class enumClass = NSWSDescriptor.Version.class;

		System.out.println(Arrays.toString(enumClass.getEnumConstants()));
		unsafelyAddEnumValues(enumClass, "LION", "_9_7");
		System.out.println(Arrays.toString(enumClass.getEnumConstants()));

		System.out.println(NSWSDescriptor.Version.valueOf("LION"));
	}

	public static void unsafelyAddEnumValues(Class enumClass, String... values) throws Throwable {
		
		Constructor<?> constructor = Unsafe.class.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		Unsafe unsafe = (Unsafe) constructor.newInstance();

		Field ordinalField = Enum.class.getDeclaredField("ordinal");
		makeAccessible(ordinalField);
		
		Field nameField = Enum.class.getDeclaredField("name");
		makeAccessible(nameField);
		
		for (String enumName : values) {

			// check that the value is not already here
			try {
				@SuppressWarnings({ "unchecked", "unused" })
				Enum existingValue = Enum.valueOf(enumClass, enumName);
				continue;
			} catch (IllegalArgumentException e) {
				// yay not found ! Let's add it
			}
			
			Object enumValue = unsafe.allocateInstance(enumClass);

			int ordinal = enumClass.getEnumConstants().length;
			ordinalField.setInt(enumValue, ordinal);

			nameField.set(enumValue, enumName);
			registerValue(enumClass, enumValue);

		}

	}

	private static void registerValue(Class enumClass, Object enumValue) throws Throwable {
		// and now we need to replace values reference from final field.
		Field $VALUESField = enumClass.getDeclaredField("$VALUES");
		makeAccessible($VALUESField);
		// just copy old values to new array and add our new field.
		Object[] oldValues = (Object[]) $VALUESField.get(null);
		Object newValues = java.lang.reflect.Array.newInstance(enumClass, oldValues.length + 1);
		System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
		java.lang.reflect.Array.set(newValues, oldValues.length, enumValue);
		$VALUESField.set(null, newValues);

		Field enumConstantsField = Class.class.getDeclaredField("enumConstants");
		makeAccessible(enumConstantsField);
		enumConstantsField.set(enumClass, null);

		Field enumConstantDirectoryField = Class.class.getDeclaredField("enumConstantDirectory");
		makeAccessible(enumConstantDirectoryField);
		enumConstantDirectoryField.set(enumClass, null);
	}

	static void makeAccessible(Field field) throws Exception {
		field.setAccessible(true);
		// note that every field is just copy of real field, so changed modifiers affects only this Field instance, if you will
		// get this same field again, it
		// will be final again.
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}

}