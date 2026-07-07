plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.openapi.generator") version "7.6.0"
}

group = "io.guardrail"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    
    // ArchUnit Architecture Guard
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/contracts/order-api.yaml")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").map { it.asFile.absolutePath })
    apiPackage.set("com.example.demo.api")
    modelPackage.set("com.example.demo.dto")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "useSpringBoot3" to "true",
        "useJakartaValidation" to "true",
        "openApiNullable" to "false"
    ))
}

sourceSets {
    main {
        java.srcDirs(
            "src/main/java",
            layout.buildDirectory.dir("generated/openapi/src/main/java")
        )
    }
	test {
        java.srcDirs(
            "src/test/java",
            "architecture",
            "generated-tests"
        )
    }
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}

tasks.test {
    useJUnitPlatform()
}