import org.jooq.meta.jaxb.Property

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.3.2"
    id("nu.studer.jooq") version "7.1.1"
    id("org.flywaydb.flyway") version "8.5.9"
}

version = "0.1"
group = "com.example"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.sql:micronaut-jooq")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("org.postgresql:postgresql:42.3.4")
    jooqGenerator("org.postgresql:postgresql:42.3.4")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    implementation("io.micronaut:micronaut-validation")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
}


application {
    mainClass.set("com.example.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}

flyway {
    url = "jdbc:postgresql://localhost:5432/postgres"
    user = "postgres"
    password = ""
}

jooq {
    version.set("3.16.6")
    configurations {
        create("main") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/postgres"
                    user = "postgres"
                }
                generator.apply {
                    generate.apply {
                        isDeprecationOnUnknownTypes = false
                    }
                    database.apply {
                        includes = "metainfo_cache"
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                    }
                    target.apply {
                        directory = "src/generated/java"
                    }
                }
            }
        }
    }
}

tasks.named("generateJooq").get().shouldRunAfter(tasks.named("flywayMigrate").get())