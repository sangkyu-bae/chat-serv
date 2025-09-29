import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.gradle.jvm.tasks.Jar
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.bmuschko.docker-spring-boot-application") version "9.4.0"
}

group = "com.chat"
version = "0.0.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")  // WebFlux 코루틴 지원

	implementation("io.projectreactor.kafka:reactor-kafka:1.3.20")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.21")  // 최신 버전 기준

	implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation ("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.11.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit-vintage-engine")
	}


}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "21"
	}
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