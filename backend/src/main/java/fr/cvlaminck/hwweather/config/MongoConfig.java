package fr.cvlaminck.hwweather.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;

@Configuration
@EnableMongoRepositories(basePackages = "fr.cvlaminck.hwweather.data.repositories",
        queryLookupStrategy = QueryLookupStrategy.Key.USE_DECLARED_QUERY)
public class MongoConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "hwweather";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient("localhost");
    }

}
