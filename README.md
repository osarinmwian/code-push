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
                .setServerUrl("https://your-codepush-server.com") // Optional override
                .setPublicKeyResourceDescriptor(R.raw.codepush_public_key) // Optional, if you're using public key
                .build();

        // Now you can use the codePush instance
        // For example: codePush.checkForUpdate();
    }
}
