package utils;

import com.almworks.sqlite4java.SQLite;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public interface UseDynamoDB {

    Logger logger = LoggerFactory.getLogger(UseDynamoDB.class);
    Map<String, Object> __objectMap = new HashMap<>();

    @BeforeEach
    default void __before() {
        System.setProperty(SQLite.LIBRARY_PATH_PROPERTY, "build/libs");

        AmazonDynamoDB dynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
        __objectMap.put("dynamoDB", dynamoDB);
    }

    @AfterEach
    default void __after() {
        AmazonDynamoDB db = currentDBInstance();
        if (db != null) {
            db.shutdown();
            __objectMap.remove("dynamoDB");
        }
    }

    default AmazonDynamoDB currentDBInstance() {
        return (AmazonDynamoDB) __objectMap.get("dynamoDB");
    }
}
