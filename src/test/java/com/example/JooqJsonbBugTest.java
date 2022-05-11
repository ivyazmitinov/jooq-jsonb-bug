package com.example;

import io.micronaut.context.env.PropertySource;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.runtime.Micronaut;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jooq.generated.public_.Tables.METAINFO_CACHE;

@Testcontainers
class JooqJsonbBugTest {

    @Test
    void testJsonbBug() {

        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.2")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")) {
            postgres.start();
            final var mappedPort = postgres.getMappedPort(5432);
            PropertySource micronautPropertySource = PropertySource
                .of(CollectionUtils.mapOf("database.server.port", mappedPort));
            try (var applicationContext = Micronaut.build((String[]) null)
                .propertySources(micronautPropertySource)
                .start()) {

                final var dslContext = applicationContext.getBean(DSLContext.class);
                final var value = JSONB.valueOf("""
                    {
                      "type": "OBJECT",
                      "fields": {
                        "gametime": {
                          "type": "VALUES",
                          "reachedThreshold": false,
                          "values": [
                            {
                              "type": "NUMBER",
                              "value": 18446681938112752000,
                              "count": 30
                            }
                          ]
                        }
                      }
                    }
                    """);
                dslContext.insertInto(METAINFO_CACHE)
                    .set(METAINFO_CACHE.CACHE_PART_NAME, "test")
                    .set(METAINFO_CACHE.VALUE, value)
                    .set(METAINFO_CACHE.TENANT_ID, "t")
                    .onConflict(METAINFO_CACHE.CACHE_PART_NAME, METAINFO_CACHE.TENANT_ID)
                    .doUpdate()
                    .set(METAINFO_CACHE.VALUE, value)
                    .where(METAINFO_CACHE.CACHE_PART_NAME.eq("test")
                        .and(METAINFO_CACHE.TENANT_ID.eq("t")))
                    .execute();

            }

        }
    }

}
