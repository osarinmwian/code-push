USAGE
import com.codepushsdk.react.CodePushBuilder;
import com.codepushsdk.react.CodePush;

...

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String deploymentKey = "staging_deployment_key_abc123";
        Context context = getApplicationContext();

        CodePush codePush = new CodePushBuilder(deploymentKey, context)
                .setIsDebugMode(true) // Optional
                .setServerUrl("http://localhost:3000")
                .build();

        // Now you can use the codePush instance
        // For example: codePush.checkForUpdate();
    }
}

installation 
yarn add react-native-codepush-sdk@https://github.com/osarinmwian/code-push.git


Here's a well-structured documentation for your custom react-native-codepush-sdk Android integration, specifically covering:

Overview

Installation

Configuration (Gradle Setup)

Usage in React Native

Customization Options

Advanced Notes

📘 react-native-codepush-sdk Android SDK – Documentation
📌 1. Overview
The react-native-codepush-sdk is a custom CodePush implementation tailored for React Native Android apps. It supports dynamic updates by syncing JavaScript bundle changes at runtime. The library is written in Kotlin and integrates seamlessly with React Native and Android build systems.

⚙️ 2. Installation
Install the SDK into your React Native project:

bash
Copy
Edit
yarn add react-native-codepush-sdk@<your-custom-git-url>
Or if hosted on GitHub:

bash
Copy
Edit
yarn add react-native-codepush-sdk@github:yourusername/code-push#branch-or-commit
🛠️ 3. Configuration
3.1 Root android/build.gradle
Add the required SDK and Kotlin versions (or let the SDK use defaults):

groovy
Copy
Edit
buildscript {
    ext {
        compileSdkVersion = 34
        minSdkVersion = 21
        targetSdkVersion = 34
        kotlinVersion = '1.9.0'
    }

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }
}
3.2 Project settings.gradle
Ensure your project includes:

groovy
Copy
Edit
include ':react-native-codepush-sdk'
project(':react-native-codepush-sdk').projectDir = new File(rootProject.projectDir, 'node_modules/react-native-codepush-sdk/android')
3.3 App-level android/app/build.gradle
Make sure your app links the SDK correctly:

groovy
Copy
Edit
dependencies {
    implementation project(':react-native-codepush-sdk')
}
🧩 4. Usage in React Native
To integrate in JavaScript:

ts
Copy
Edit
import CodePush from 'react-native-codepush-sdk';

// Sync update
CodePush.sync();
Or customize sync behavior:

ts
Copy
Edit
CodePush.sync({
  installMode: CodePush.InstallMode.IMMEDIATE,
  updateDialog: true,
});
🔧 5. Customization Options
The SDK build script dynamically reads the following from the root project:

Property	Description	Default
compileSdkVersion	Android Compile SDK version	34
minSdkVersion	Minimum supported Android version	21
targetSdkVersion	Target Android version	34
kotlinVersion	Kotlin version for the library	1.9.0

These can be overridden by defining them in your root build.gradle as shown in section 3.1.

🚀 6. Advanced Notes
✅ Dynamic Bundling: The SDK handles pre-bundle tasks and hashes via custom Gradle tasks that run during the asset merge phase.

✅ Supports Debug/Release Modes: Automatically skips processing for debug builds to improve speed.

❌ No Autolinking: You must manually link the module in settings.gradle and build.gradle.

✅ Kotlin Ready: Plugin org.jetbrains.kotlin.android is pre-configured.

🛠️ Proguard: Includes proguard-rules.pro as a consumer rule for library consumers.

📂 Example Directory Structure
java
Copy
Edit
MyApp/
├── android/
│   ├── app/
│   ├── build.gradle
│   └── settings.gradle
├── node_modules/
│   └── react-native-codepush-sdk/
│       └── android/
├── index.js
└── package.json
📞 Support / Contribution
If you're publishing this SDK or working in a team:

📦 Add a README.md file to the repo.

📬 Document supported APIs and usage scenarios.

🤝 Accept PRs and issue reports with proper versioning.

