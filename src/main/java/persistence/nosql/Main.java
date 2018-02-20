package persistence.nosql;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Main {
    public static void main(String[] args) {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .withClusterName("Test Cluster")
                    .build();

            Session session = cluster.connect();

            ResultSet rs = session.execute("SELECT * FROM demodb.People");
            Row row = rs.one();

            System.out.println(row.getString("name"));
            System.out.println("LLLL");

//
//
//            String schema = "type Query{hello: String} schema{query: Query}";
//
//            SchemaParser schemaParser = new SchemaParser();
//            TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
//
//            RuntimeWiring runtimeWiring = newRuntimeWiring()
//                    .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("mohamed")))
//                    .build();
//
//            SchemaGenerator schemaGenerator = new SchemaGenerator();
//            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
//
//            GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
//            ExecutionResult executionResult = build.execute("{hello}");
//
//            System.out.println(executionResult.getData().toString());
        } finally {
            if (cluster != null) cluster.close();
        }
    }
}