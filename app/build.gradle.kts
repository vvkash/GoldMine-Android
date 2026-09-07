import java.util.Properties
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// The Google Services plugin is only applied when a google-services.json is present,
// so the project still builds for anyone who has not yet registered the Android app
// in the Firebase console.
if (project.file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

val secretProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun secret(key: String, fallback: String = ""): String =
    (secretProps.getProperty(key) ?: System.getenv(key) ?: fallback)

android {
    namespace = "com.goldmine.uncc"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.goldmine.uncc"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "OPENWEATHER_API_KEY", "\"${secret("OPENWEATHER_API_KEY")}\"")
        manifestPlaceholders["MAPS_API_KEY"] = secret("MAPS_API_KEY")
    }

    signingConfigs {
        create("release") {
            val storeFilePath = secret("RELEASE_STORE_FILE")
            if (storeFilePath.isNotBlank() && rootProject.file(storeFilePath).exists()) {
                storeFile = rootProject.file(storeFilePath)
                storePassword = secret("RELEASE_STORE_PASSWORD")
                keyAlias = secret("RELEASE_KEY_ALIAS")
                keyPassword = secret("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val rc = signingConfigs.getByName("release")
            if (rc.storeFile != null) signingConfig = rc
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}

val verifyPlayRelease = tasks.register("verifyPlayRelease") {
    group = "verification"
    description = "Checks production configuration without printing credentials."
    doLast {
        val problems = mutableListOf<String>()
        listOf("MAPS_API_KEY", "OPENWEATHER_API_KEY", "RELEASE_STORE_FILE",
            "RELEASE_STORE_PASSWORD", "RELEASE_KEY_ALIAS", "RELEASE_KEY_PASSWORD").forEach { key ->
            if (secret(key).isBlank() || secret(key).contains("REPLACE_WITH")) problems += "$key is missing or a placeholder"
        }
        val store = secret("RELEASE_STORE_FILE")
        if (store.isNotBlank() && !rootProject.file(store).isFile) problems += "Upload keystore file does not exist"
        val firebase = project.file("src/release/google-services.json").takeIf { it.isFile }
            ?: project.file("google-services.json")
        if (!firebase.isFile) {
            problems += "Firebase config is missing (expected app/src/release/google-services.json or app/google-services.json)"
        } else {
            runCatching {
                val config = JsonSlurper().parse(firebase) as Map<*, *>
                val clients = config["client"] as List<*>
                val production = clients.filterIsInstance<Map<*, *>>().firstOrNull { client ->
                    val info = client["client_info"] as? Map<*, *>
                    val androidInfo = info?.get("android_client_info") as? Map<*, *>
                    androidInfo?.get("package_name") == "com.goldmine.uncc"
                }
                check(production != null)
                val info = production["client_info"] as Map<*, *>
                val appId = info["mobilesdk_app_id"] as? String ?: ""
                val keys = production["api_key"] as? List<*> ?: emptyList<Any>()
                check(appId.isNotBlank() && !appId.contains("REPLACE_WITH"))
                check(keys.filterIsInstance<Map<*, *>>().any {
                    val key = it["current_key"] as? String ?: ""
                    key.isNotBlank() && !key.contains("REPLACE_WITH")
                })
            }.onFailure { problems += "Firebase configuration needs a real com.goldmine.uncc client" }
        }
        check(problems.isEmpty()) { "Play release blocked:\n" + problems.joinToString("\n") }
        logger.lifecycle("Production configuration is present. Credentials and live services still require device testing.")
    }
}

tasks.register("preparePlayRelease") {
    group = "build"
    description = "Validates production settings, runs checks, and builds the signed Play bundle."
    dependsOn(verifyPlayRelease, "testDebugUnitTest", "lintRelease", "bundleRelease")
}

tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    mustRunAfter(verifyPlayRelease)
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.common)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.maps.compose)

    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
