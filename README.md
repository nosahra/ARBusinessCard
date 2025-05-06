AR Business Card Application
--------------------------------
Student Name: Newton Nath (Student Id: 220298872), Student Name: Sahra Yusuf (Student Id: 220364960)
---------
Project Overview
---------
This Android application provides a digital business card experience in augmented reality (AR) which is enhanced by text-to-speech (TTS) narration.
 Users can:

-Authenticate via email/password with offline support and inline error handling.

-Create and edit their business card data (LinkedIn, GitHub, email, introduction, education, experience, hobbies).

-Generate and download a QR code embedded Card that encodes their profile identifier.

-Scan the QR code embedded card to launch the AR experience.

-View an AR overlay of a 3D avatar model, with tap-controlled subtitles and clickable social/link icons.

-Hear text-to-speech (TTS) narration of the user’s introduction and other fields in AR.

-The app gracefully handles network outages by showing inline banners and disabling saves until connectivity is restored. Core logic is covered by unit tests for URL validation, TTS payload creation, and connectivity observer.

------------------------
Individual Contribution:
------------------------
Newton >
--------

App Architecture
- MainActivity.kt
- HomeScreen.kt
- AppDestinations.kt

Text-to-Speech Implementation
– TTSUtil.kt, RetrofitClient.kt, GoogleTTSService.kt, TTSMOdels.kt

Login/Registration
- AuthScreen.kt

Card Owner Screen(Card Creation)
- CardOwnerScreen.kt

Scanning QR code and launching AR Screen
- ScannerScreen.kt
- QrCodeUtils.kt

Observe connectivity and show offline banner
- ConnectivityObserver.kt, ConnectivityLayout.kt, OfflineBanner.kt

Integrating TTS to AR Screen
- HelloArActivity.kt, HelloArView.kt

Unit Testing
– Wrote simple unit tests: TTSUtilTest.kt, isValidHostTest.kt, AuthValidationTest.kt

Sahra >
--------
App UI
- HomeScreen.kt
- AuthScreen.kt
- HelpScreen.kt
- CardOwnerScreen.kt
- activity_mail.xml
- custom_capture_layout.kt

AR Implementation
- HelloArRenderer.kt
- HelloArView.kt
- HelloArActivity.kt

------------------------
 // How to run the code //
------------------------

1. Prerequisites
   
-Install Android Studio on your machine


2. Set up project

-Download ZIP file of the repository

-Unzip the provided archive

-Open Android Studio

-In file menu select “Open”. Then, Select the unzipped folder and press OK.

-Wait for Gradle Sync to finish (first run may download dependencies which might take few minutes if your device is capable. otherwise, it will take longer to download all dependencies)

- Once Gradle Build is finished, you can run the app.


3. Run the app

=> Option A – Android Studio Emulator(recommended) [Your device must have at least 10 GB of free space to run on an emulator]

-In Device Manager, create an AndroidVirtualDevice  running API24 or higher.

-Select the AVD in the target drop‑down.

-Press Run


=> Option B – On an android SmartPhone

-On the phone: enable Developer options (tap Build number 7times),
then open Developer options and enable USB debugging.

-Connect the phone via USB and accept the debugging prompt.

-Select the device in the target list and press Run..

Once deployed, the app launches automatically on your android phone.


