package com.eurodyn.qlack2.be.rules.impl.util;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.SerialVersionUID;

/**
 * Gererate a javassist class from a data model metadata.
 *
 * Based on http://blog.javaforge.net/post/31913732423/howto-create-java-pojo-at-runtime-with-javassist
 */
public class PojoGenerator {
	private final ClassPool pool;

	public PojoGenerator(ClassPool pool) {
		this.pool = pool;
	}

	public CtClass generate(StringDataModelVersion stringVersion)
			throws NotFoundException, CannotCompileException {

		String className = stringVersion.getClassName();
		String superClassName = stringVersion.getSuperClassName();

		String id = stringVersion.getId();
		String name = stringVersion.getName();

		CtClass cc = pool.get(className);

		if (superClassName != null) {
			CtClass superCC = pool.get(superClassName);
			cc.setSuperclass(superCC);
		}

		cc.addInterface(pool.get(Serializable.class.getName()));
		SerialVersionUID.setSerialVersionUID(cc);

		CtField idField = new CtField(pool.get(String.class.getName()), "ID", cc);
		idField.setModifiers(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
		cc.addField(idField, CtField.Initializer.constant(id));

		CtField nameField = new CtField(pool.get(String.class.getName()), "VERSION", cc);
		nameField.setModifiers(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
		cc.addField(nameField, CtField.Initializer.constant(name));

		for (StringDataModelField stringField : stringVersion.getFields()) {
			String fieldName = stringField.getName();
			String fieldClassName = stringField.getClassName();

			CtClass fieldCC = pool.get(fieldClassName);
			CtField field = new CtField(fieldCC, fieldName, cc);
			cc.addField(field);

			CtMethod getter = generateGetter(cc, fieldName, fieldClassName);
			cc.addMethod(getter);

			CtMethod setter = generateSetter(cc, fieldName, fieldClassName);
			cc.addMethod(setter);
		}

		return cc;
	}

	private static CtMethod generateGetter(CtClass declaringClass, String fieldName, String fieldClassName)
			throws CannotCompileException {

		String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		StringBuffer sb = new StringBuffer();
		sb.append("public ").append(fieldClassName).append(" ").append(getterName).append("()").append("{")
				.append("return this.").append(fieldName).append(";")
				.append("}");

		return CtMethod.make(sb.toString(), declaringClass);
	}

	private static CtMethod generateSetter(CtClass declaringClass, String fieldName, String fieldClassName)
			throws CannotCompileException {

		String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

		StringBuffer sb = new StringBuffer();
		sb.append("public void ").append(setterName).append("(").append(fieldClassName).append(" ").append(fieldName).append(")").append("{")
				.append("this.").append(fieldName).append("=").append(fieldName).append(";")
				.append("}");

		return CtMethod.make(sb.toString(), declaringClass);
	}

}