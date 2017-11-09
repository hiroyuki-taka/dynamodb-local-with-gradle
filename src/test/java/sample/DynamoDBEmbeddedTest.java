package sample;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import org.junit.jupiter.api.Test;
import utils.CreateTable;
import utils.DynamoDBLoadData;
import utils.ImportData;
import utils.UseDynamoDB;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamoDBEmbeddedTest implements UseDynamoDB, DynamoDBLoadData {

    @Test
    @CreateTable(tableName = "Movies", keySchemaElements = {"film_id", "HASH"}, attributeDefinitions = {"film_id", "S"})
    @CreateTable(tableName = "Tests1", keySchemaElements = {"test_id", "HASH"}, attributeDefinitions = {"test_id", "S"})
    @ImportData(tableName = "Movies", fileName = "hoge.csv")
    void createTableTest() {

        AmazonDynamoDB ddb = currentDBInstance();
        String tableName = "Movies";
        String hashKeyName = "film_id";

        DescribeTableResult describeTableResult = ddb.describeTable(tableName);
        TableDescription tableDesc = describeTableResult.getTable();

        assertEquals(tableName, tableDesc.getTableName());
        assertEquals("[{AttributeName: " + hashKeyName + ",KeyType: HASH}]", tableDesc.getKeySchema().toString());
        assertEquals("[{AttributeName: " + hashKeyName + ",AttributeType: S}]", tableDesc.getAttributeDefinitions().toString());
        assertEquals(Long.valueOf(1L), tableDesc.getProvisionedThroughput().getReadCapacityUnits());
        assertEquals(Long.valueOf(1L), tableDesc.getProvisionedThroughput().getWriteCapacityUnits());
        assertEquals("ACTIVE", tableDesc.getTableStatus());
        assertEquals("arn:aws:dynamodb:ddblocal:000000000000:table/Movies", tableDesc.getTableArn());

        ListTablesResult tables = ddb.listTables();
        assertEquals(2, tables.getTableNames().size());
    }
}
