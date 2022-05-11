To reproduce run `com.example.JooqJsonbBugTest.testJsonbBug`

To generate jooq classes:

1. `docker-compose up -d` 
2. `./gradlew flywayMigrate generateJooq`