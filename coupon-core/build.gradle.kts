val bootJar: org.springframework.boot.gradle.tasks.bundling.BootJar by tasks

bootJar.enabled = false

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.redisson:redisson-spring-boot-starter:3.16.4")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.redisson:redisson-spring-boot-starter:3.16.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 로컬 캐시 적용 : 카페인
    implementation("com.github.ben-manes.caffeine:caffeine")
}

tasks.withType<Test> {
    useJUnitPlatform()
}