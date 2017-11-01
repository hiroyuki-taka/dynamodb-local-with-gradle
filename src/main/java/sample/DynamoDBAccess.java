package sample;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.List;

public class DynamoDBAccess {

    AmazonDynamoDB getClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();
    }

    Table createTable(AmazonDynamoDB client) {
        DynamoDB dynamoDB = new DynamoDB(client);

        List<AttributeDefinition> attributeDefinitions = List.of(
                new AttributeDefinition().withAttributeName("Id").withAttributeType("N")
        );

        List<KeySchemaElement> keySchema = List.of(
                new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH)
        );

        CreateTableRequest request = new CreateTableRequest()
                .withTableName("sample1")
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(5L)
                        .withWriteCapacityUnits(5L));

        Table table = dynamoDB.createTable(request);

        try {
            table.waitForActive();
        } catch (InterruptedException ex) {
            // empty
        }

        return table;
    }
}
