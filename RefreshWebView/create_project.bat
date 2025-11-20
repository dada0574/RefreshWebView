@echo off
chcp 65001 >nul
echo 正在创建项目文件结构...

REM 创建目录
mkdir .github\workflows 2>nul
mkdir app\src\main\java\com\example\autorefreshweb 2>nul
mkdir app\src\main\res\layout 2>nul
mkdir app\src\main\res\values 2>nul
mkdir app\src\main\res\mipmap-xxxhdpi 2>nul

REM 创建 .github/workflows/build.yml
(
echo name: Android CI
echo.
echo on:
echo   workflow_dispatch:
echo   push:
echo     branches: [ main ]
echo.
echo jobs:
echo   build:
echo     runs-on: ubuntu-latest
echo.    
echo     steps:
echo     - uses: actions/checkout@v3
echo.    
echo     - name: Set up JDK 11
echo       uses: actions/setup-java@v3
echo       with:
echo         java-version: '11'
echo         distribution: 'temurin'
echo.    
echo     - name: Setup Gradle
echo       uses: gradle/gradle-build-action@v2
echo       with:
echo         gradle-version: 7.0.2
echo.        
echo     - name: Build with Gradle
echo       run: gradle assembleRelease
echo.      
echo     - name: Upload APK
echo       uses: actions/upload-artifact@v3
echo       with:
echo         name: app-release
echo         path: app/build/outputs/apk/release/app-release-unsigned.apk
) > .github\workflows\build.yml

REM 创建 build.gradle
(
echo buildscript {
echo     repositories {
echo         google^(^)
echo         mavenCentral^(^)
echo     }
echo     dependencies {
echo         classpath 'com.android.tools.build:gradle:7.0.4'
echo     }
echo }
echo.
echo allprojects {
echo     repositories {
echo         google^(^)
echo         mavenCentral^(^)
echo     }
echo }
echo.
echo task clean^(type: Delete^) {
echo     delete rootProject.buildDir
echo }
) > build.gradle

REM 创建 settings.gradle
(
echo rootProject.name = "AutoRefreshWebView"
echo include ':app'
) > settings.gradle

REM 创建 app/build.gradle
(
echo plugins {
echo     id 'com.android.application'
echo }
echo.
echo android {
echo     compileSdkVersion 30
echo.    
echo     defaultConfig {
echo         applicationId "com.example.autorefreshweb"
echo         minSdkVersion 29
echo         targetSdkVersion 30
echo         versionCode 1
echo         versionName "1.0"
echo     }
echo.
echo     buildTypes {
echo         release {
echo             minifyEnabled false
echo             proguardFiles getDefaultProguardFile^('proguard-android-optimize.txt'^), 'proguard-rules.pro'
echo         }
echo     }
echo.    
echo     compileOptions {
echo         sourceCompatibility JavaVersion.VERSION_1_8
echo         targetCompatibility JavaVersion.VERSION_1_8
echo     }
echo }
echo.
echo dependencies {
echo     implementation 'androidx.appcompat:appcompat:1.3.1'
echo     implementation 'com.google.android.material:material:1.4.0'
echo }
) > app\build.gradle

REM 创建 AndroidManifest.xml
(
echo ^<?xml version="1.0" encoding="utf-8"?^>
echo ^<manifest xmlns:android="http://schemas.android.com/apk/res/android"
echo     package="com.example.autorefreshweb"^>
echo.
echo     ^<uses-permission android:name="android.permission.INTERNET" /^>
echo     ^<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /^>
echo.
echo     ^<application
echo         android:allowBackup="true"
echo         android:icon="@mipmap/ic_launcher"
echo         android:label="自动刷新"
echo         android:usesCleartextTraffic="true"
echo         android:theme="@style/Theme.AppCompat.Light.NoActionBar"^>
echo.        
echo         ^<activity
echo             android:name=".MainActivity"
echo             android:configChanges="orientation|screenSize"
echo             android:exported="true"^>
echo             ^<intent-filter^>
echo                 ^<action android:name="android.intent.action.MAIN" /^>
echo                 ^<category android:name="android.intent.category.LAUNCHER" /^>
echo             ^</intent-filter^>
echo         ^</activity^>
echo     ^</application^>
echo.
echo ^</manifest^>
) > app\src\main\AndroidManifest.xml

echo 文件创建完成！
echo.
echo 接下来需要手动创建以下文件：
echo 1. app\src\main\java\com\example\autorefreshweb\MainActivity.java
echo 2. app\src\main\res\layout\activity_main.xml
echo 3. app\src\main\res\layout\dialog_input_url.xml
echo 4. app\src\main\res\values\strings.xml
echo.
pause