package utils;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CreateTableHolder.class)
public @interface CreateTable {
    String tableName();
    String[] attributeDefinitions();
    String[] keySchemaElements();
}
