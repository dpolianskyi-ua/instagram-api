package com.example.mockmvcintframework.testsupport;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(StringResourceParameterResolver.class)
public @interface StringResource {
    String value();
}
