package sample;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UseDynamoDB;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DynamoDBAccessTest implements UseDynamoDB {

    DynamoDBAccess dynamoDBAccess;

    @BeforeEach
    void setup() {
        dynamoDBAccess = new DynamoDBAccess();
    }

    @Test
    void testGetClient() {
        AmazonDynamoDB client = dynamoDBAccess.getClient();
        assertNotNull(client);
    }

    @Test
    void testCreateTable() {
        Table table = dynamoDBAccess.createTable(currentDBInstance());
        assertNotNull(table);
    }
}
