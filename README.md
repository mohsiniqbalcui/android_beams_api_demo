# Android Beams client API Demo

Demonstration of the Pusher Beams client SDK for Android

This is a supplemental project for the article [Pusher Beams client API demo for Android](TODO).

## Setup

To set up this demo app, perform the following tasks:

1. Clone the GitHub repo
2. [Create a Beams instance and configure FCM](https://pusher.com/docs/beams/getting-started/android/configure-fcm). You can follow the quick start guide. The Android package name is `com.example.beamsapidemo`. Enter the FCM Server Key and download the `google-service.json` file. At that point you can quit the quick start wizard. Go to your [Beams dashboard](https://dash.pusher.com/beams), open your new instance, and go to the Credentials tab. You will find your Instance ID and Secret Key there.
3. In the cloned repo, replace `app/google-services.json` with the one you downloaded from the FCM setup.
4. In the cloned repo’s `MainActivity.kt` file, set the `INSTANCE_ID` constant to your Instance ID.
5. Run the app on the Android emulator.
