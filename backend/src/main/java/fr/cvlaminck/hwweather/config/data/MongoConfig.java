package fr.cvlaminck.hwweather.config.data;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "fr.cvlaminck.hwweather.data.repositories",
        queryLookupStrategy = QueryLookupStrategy.Key.USE_DECLARED_QUERY)
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.port}")
    private int mongoPort;

    @Value("${mongo.user:}")
    private String mongoUser;

    @Value("${mongo.password:}")
    private String mongoPassword;

    @Value("${mongo.database:hwweather}")
    private String mongoDatabase;

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    public Mongo mongo() throws Exception {
        ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);

        List<MongoCredential> credentials = new ArrayList<>();
        if (mongoUser.length() > 0 && mongoPassword.length() > 0) {
            MongoCredential credential = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
            credentials.add(credential);
        }

        return new MongoClient(serverAddress, credentials);
    }

}
