package sample;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.jupiter.api.Test;
import utils.UseDynamoDB;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamoDBEmbeddedTest implements UseDynamoDB {

    @Test
    void createTableTest() {

        AmazonDynamoDB ddb = currentDBInstance();
        String tableName = "Movies";
        String hashKeyName = "film_id";

        CreateTableResult result = createTable(ddb, tableName, hashKeyName);

        TableDescription tableDesc = result.getTableDescription();

        assertEquals(tableName, tableDesc.getTableName());
        assertEquals("[{AttributeName: " + hashKeyName + ",KeyType: HASH}]", tableDesc.getKeySchema().toString());
        assertEquals("[{AttributeName: " + hashKeyName + ",AttributeType: S}]", tableDesc.getAttributeDefinitions().toString());
        assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getReadCapacityUnits());
        assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getWriteCapacityUnits());
        assertEquals("ACTIVE", tableDesc.getTableStatus());
        assertEquals("arn:aws:dynamodb:ddblocal:000000000000:table/Movies", tableDesc.getTableArn());

        ListTablesResult tables = ddb.listTables();
        assertEquals(1, tables.getTableNames().size());
    }

    
    CreateTableResult createTable(AmazonDynamoDB ddb, String tableName, String hashKeyName) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));

        List<KeySchemaElement> ks = new ArrayList<>();
        ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(1000L, 1000L);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(ks)
                .withProvisionedThroughput(provisionedThroughput);

        return ddb.createTable(request);
    }
}
