plugins {
    id("com.android.library")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.huanli233.renderscript"
    compileSdk = 36

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    defaultConfig {
        minSdk = 14

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(files("src/main/libs/androidx-rs.jar"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}

mavenPublishing {
    coordinates("com.huanli233.renderscript", "renderscript", "1.0.0")
    pom {
        name = "RenderScript"
        description = "RenderScript support library"
        url = "https://github.com/Dimezis/BlurView"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "huanli233"
                name = "huanli233"
            }
        }

        scm {
            connection = "scm:git:github.com/Dimezis/BlurView.git"
            developerConnection = "scm:git:ssh://github.com/Dimezis/BlurView.git"
            url = "https://github.com/Dimezis/BlurView/tree/master"
        }
    }
}