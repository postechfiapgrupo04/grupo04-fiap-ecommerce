package br.com.fiap.item.helpers;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UpdateHelper {

    public static <T> Object updateHelper(T source, T target) {
        Class<?> sourceClass = source.getClass();
        Field[] fields = sourceClass.getDeclaredFields();

        Arrays.stream(fields)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(target);

                        if (value != null)
                            field.set(source, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Erro ao realizar update: ", e);
                    }
                });
        return source;
    }
}