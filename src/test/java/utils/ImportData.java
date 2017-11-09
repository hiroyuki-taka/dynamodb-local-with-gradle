package utils;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ImportDataHolder.class)
public @interface ImportData {
    String tableName();
    String fileName();
}
