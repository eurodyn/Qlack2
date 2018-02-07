package com.eurodyn.qlack2.util.jwt.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.ws.rs.NameBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface JWTNeeded {
}