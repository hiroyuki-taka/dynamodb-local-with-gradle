package utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface DynamoDBLoadData extends UseDynamoDB {

    Logger logger = LoggerFactory.getLogger(DynamoDBLoadData.class);

    @SuppressWarnings("unchecked")
    private <LIST extends Annotation, ITEM extends Annotation> List<ITEM> getAnnotations(Method m, Class<ITEM> i) {
        try {
            Repeatable repeatable = i.getAnnotation(Repeatable.class);
            if (repeatable != null) {
                Class<LIST> listClass = (Class<LIST>) repeatable.value();
                LIST a = m.getAnnotation(listClass);
                if (a != null) {
                    Method value = listClass.getMethod("value");
                    return Arrays.asList((ITEM[])value.invoke(a));
                }
            }

            ITEM itemAnnotation = m.getAnnotation(i);
            if (itemAnnotation != null) {
                return Collections.singletonList(itemAnnotation);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("", e);
        }
        return Lists.newArrayList();
    }

    @BeforeEach
    default void DynamoDBLoadData__before(TestInfo testInfo) {
        testInfo.getTestMethod().ifPresent(m -> {

            AmazonDynamoDB client = currentDBInstance();

            for (CreateTable t : getAnnotations(m, CreateTable.class)) {
                String tableName = t.tableName();
                List<AttributeDefinition> attributeDefinitions = Lists.partition(Arrays.asList(t.attributeDefinitions()), 2).stream()
                        .map(params -> new AttributeDefinition(params.get(0), params.get(1)))
                        .collect(Collectors.toList());

                List<KeySchemaElement> keySchemaElements = Lists.partition(Arrays.asList(t.keySchemaElements()), 2).stream()
                        .map(params -> new KeySchemaElement(params.get(0), params.get(1)))
                        .collect(Collectors.toList());

                CreateTableRequest request = new CreateTableRequest()
                        .withTableName(tableName)
                        .withKeySchema(keySchemaElements)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

                client.createTable(request);
                logger.debug("create dynamoDB table: {}", tableName);
            }

            for (ImportData d : getAnnotations(m, ImportData.class)) {
                logger.debug("load data to {}, {}", d.tableName(), d.fileName());

                // TODO:
            }
        });
    }
}
