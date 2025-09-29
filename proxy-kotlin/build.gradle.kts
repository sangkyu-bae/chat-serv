import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.gradle.jvm.tasks.Jar
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	id("com.bmuschko.docker-spring-boot-application") version "9.4.0"
}

group = "com.chat"
version = "0.0.1"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0" // ※ 호환 매트릭스 확인 권장

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.kafka:spring-kafka-test")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("com.mysql:mysql-connector-j")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("io.swagger.core.v3:swagger-annotations:2.2.21")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// 이미 있으시겠지만 보강
tasks.named<org.gradle.jvm.tasks.Jar>("jar") { enabled = false }
val bootJarTask = tasks.named<BootJar>("bootJar")

// 1) Docker 컨텍스트 준비 (깨끗한 디렉터리)
val prepareDockerContext = tasks.register<Sync>("prepareDockerContext") {
	dependsOn(bootJarTask)

	into(layout.buildDirectory.dir("docker"))   // => build/docker

	// Dockerfile 원본을 컨텍스트로 복사
	from("Dockerfile")

	// 부트 JAR을 컨텍스트로 복사, 이름은 app.jar로 통일
	from(bootJarTask.flatMap { it.archiveFile }) {
		into(".")
		rename { "app.jar" }
	}

	// 애초에 불필요/문제 파일은 포함 안 함
	exclude(".gradle/**", "**/.gradle/**", ".git/**", "**/.git/**",
		".idea/**", "**/*.iml", "build/**", "**/build/**")
}

// 2) Docker 이미지 빌드 (컨텍스트는 build/docker)
tasks.register<DockerBuildImage>("buildDockerImageCustoms") {
	group = "docker"
	description = "Build image using project Dockerfile (temurin 21)"

	dependsOn(prepareDockerContext)

	// 깨끗한 컨텍스트 사용
	val ctxDir = layout.buildDirectory.dir("docker")
	inputDir.set(ctxDir.get().asFile)
	dockerFile.set(ctxDir.map { it.file("Dockerfile") })

	images.add("${project.name}:${project.version}")

	// Dockerfile에서 ARG JAR_FILE 사용 시
	buildArgs.put("JAR_FILE", "app.jar")

	doFirst {
		println("Docker context = ${inputDir.get().asFile.absolutePath}")
		println("Dockerfile     = ${dockerFile.get().asFile.absolutePath}")
		println("Image tag      = ${images}")
	}
}