---
source: https://developers.google.com/cardboard/develop/c/quickstart
fetched: 2026-07-19T13:55:35.9289729-04:00
name: cardboard-ndk-quickstart
---

Title: Quickstart for Google Cardboard for Android NDK

URL Source: https://developers.google.com/cardboard/develop/c/quickstart

Markdown Content:
*   This guide explains how to use the Cardboard SDK for Android to create Virtual Reality experiences, allowing a smartphone to function as a VR platform with features like stereoscopic rendering, head tracking, and viewer button interaction.

*   The tutorial utilizes the HelloCardboard demo game to illustrate core SDK features, including setting up the development environment, building the app, scanning a Cardboard viewer QR code, tracking head movements, and rendering stereoscopic images.

*   Setting up the development environment requires specific hardware (Android 8.0+ device, Cardboard viewer) and software (Android Studio 2024.1.2+, Android SDK 15.0+, Android NDK).

*   Building the demo app involves cloning the Cardboard SDK repository, opening the project in Android Studio, assembling the SDK via Gradle, and running the hellocardboard-android target on a connected device.

*   The guide covers key technical aspects like configuring device parameters by scanning the viewer QR code, enabling the Android Studio x86 emulator, creating and managing the head tracker, applying lens distortion based on viewer parameters, and the steps involved in rendering VR content including texture creation, getting eye matrices, setting up the distortion renderer, and rendering each frame with head orientation.

This guide shows you how to use the [Cardboard SDK](https://github.com/googlevr/cardboard) for Android to create your own Virtual Reality (VR) experiences.

You can use the Cardboard SDK to turn a smartphone into a VR platform. A smartphone can display 3D scenes with stereoscopic rendering, track and react to head movements, and interact with apps by detecting when the user presses the viewer button.

To get started, you'll use **HelloCardboard**, a demo game that demonstrates the core features of the Cardboard SDK. In the game, users look around a virtual world to find and collect objects. It shows you how to:

*   Set up your development environment
*   Download and build the demo app
*   Scan the QR code of a Cardboard viewer to save its parameters
*   Track the user’s head movements
*   Render stereoscopic images by setting the correct view projection matrix for each eye

HelloCardboard uses the Android NDK. Every native method is:

*   Bounded uniquely to a `HelloCardboardApp` class method, or
*   Creates or deletes an instance of that class

## Set up your development environment

Hardware requirements:

*   Android device running Android 8.0 "Oreo" (API level 26) or higher
*   [Cardboard viewer](https://vr.google.com/cardboard/get-cardboard/)

Software requirements:

*   [Android Studio](https://developer.android.com/studio) version 2024.1.2 "Koala Feature Drop" or higher
*   Android SDK 15.0 "Vanilla Ice Cream" (API level 35) or higher
*   Latest version of the [Android NDK](https://developer.android.com/ndk/downloads) framework

To review or update installed SDKs, go to **Preferences**>**Appearance and Behavior**

> **System Settings**>**Android SDK** in Android Studio.

## Download and build the demo app

The Cardboard SDK is built using a pre-compiled [Vulkan](https://www.vulkan.org/) header file for each shader. Steps to build the header files from scratch can be found [here](https://developers.google.com/cardboard/develop/c/vulkan).

1.   Run the following command to clone the Cardboard SDK and the HelloCardboard demo app from GitHub:

git clone https://github.com/googlevr/cardboard.git
2.   In Android Studio, select **Open an existing Android Studio Project**, then select the directory where the Cardboard SDK and the HelloCardboard demo app were cloned into.

Your code will appear in the Project window in Android Studio.

![Image 1](https://developers.google.com/static/cardboard/images/c/android-studio-project-window.png%201x)

3.   To assemble the Cardboard SDK, double-click the **assemble** option within **cardboard/:sdk/Tasks/build** folder in the Gradle tab (View > Tool Windows > Gradle).

![Image 2](https://developers.google.com/static/cardboard/images/c/assemble-gradle.png%201x)

4.   Run the HelloCardboard demo app on your phone by selecting **Run**>**Run...** and select the `hellocardboard-android` target.

## Scan the QR code

To save the device parameters, scan the QR code on the Cardboard viewer: ![Image 3](https://developers.google.com/static/cardboard/images/c/c-cardboard-scan-qr-code.png%202x)

If the user presses "SKIP" and there are no previously saved parameters, Cardboard saves Google Cardboard v1 (launched at Google I/O 2014) parameters.

## Try the demo

In HelloCardboard, you'll look for and collect geodesic spheres in 3D space.

To find and collect a sphere:

1.   Move your head around in any direction until you see a floating shape.

![Image 4](https://developers.google.com/static/cardboard/images/c/c-cardboard-floating-shape-blue.png%202x)

2.   Look directly at the sphere. This causes it to change colors.

![Image 5](https://developers.google.com/static/cardboard/images/c/c-cardboard-floating-shape-pink.png%202x)

3.   Press the Cardboard viewer button to "collect" the sphere.

## Configure the device

When the user taps the gear icon to switch Cardboard viewers, the `nativeSwitchViewer` method is called. `nativeSwitchViewer` calls `CardboardQrCode_scanQrCodeAndSaveDeviceParams`, which opens the window to scan the viewer’s QR code. The viewer's lens distortion and other parameters are updated once the QR code is scanned.

```
// Called by JNI method
void HelloCardboardApp::SwitchViewer() {
  CardboardQrCode_scanQrCodeAndSaveDeviceParams();
}
```

## Enable Android Studio x86 emulator

To build for the Android Studio x86 emulator, remove the following line from `build.gradle` files in [SDK](https://github.com/googlevr/cardboard/blob/master/sdk/build.gradle) and [Sample](https://github.com/googlevr/cardboard/blob/master/hellocardboard-android/build.gradle):

```
abiFilters 'armeabi-v7a', 'arm64-v8a'
```

This enables all ABIs and will significantly increase the size of the generated `.aar` file. See [Android ABIs](https://developer.android.com/ndk/guides/abis) for more information.

## Head tracking

### Create head tracker

The head tracker is created once in the constructor of `HelloCardboardApp`:

```
HelloCardboardApp::HelloCardboardApp(JavaVM* vm, jobject obj, jobject asset_mgr_obj) {
  Cardboard_initializeAndroid(vm, obj); // Must be called in constructor
  head_tracker_ = CardboardHeadTracker_create();
}
```

When `VrActivity` is created, an instance of the `HelloCardboardApp` class is generated by calling the `nativeOnCreate` method:

```
public void onCreate(Bundle savedInstance) {
  super.onCreate(savedInstance);
  nativeApp = nativeOnCreate(getAssets());
  //...
}
```

### Pause and resume head tracker

To pause, resume, and destroy the head tracker, `CardboardHeadTracker_pause(head_tracker_)`, `CardboardHeadTracker_resume(head_tracker_)`, and `CardboardHeadTracker_destroy(head_tracker_)` must be called, respectively. In the "HelloCardboard" app, we call them in `nativeOnPause`, `nativeOnResume`, and `nativeOnDestroy`:

```
// Code to pause head tracker in hello_cardboard_app.cc

void HelloCardboardApp::OnPause() { CardboardHeadTracker_pause(head_tracker_); }

// Call nativeOnPause in VrActivity
@Override
protected void onPause() {
  super.onPause();
  nativeOnPause(nativeApp);
  //...
}

// Code to resume head tracker in hello_cardboard_app.cc
void HelloCardboardApp::onResume() {
  CardboardHeadTracker_resume(head_tracker_);
  //...
}

// Call nativeOnResume in VrActivity
@Override
protected void onResume() {
  super.onResume();
  //...
  nativeOnResume(nativeApp);
}

// Code to destroy head tracker in hello_cardboard_app.cc
HelloCardboardApp::~HelloCardboardApp() {
  CardboardHeadTracker_destroy(head_tracker_);
  //...
}

// Call nativeOnDestroy in VrActivity
@Override
protected void onDestroy() {
  super.onDestroy();
  nativeOnDestroy(nativeApp);
  nativeApp = 0;
}
```

## Lens distortion

Every time Cardboard scans a new QR code, the following code reads the saved parameters and uses them to create the lens distortion object, which applies the proper lens distortion to the rendered content:

```
CardboardQrCode_getSavedDeviceParams(&buffer, &size);

CardboardLensDistortion_destroy(lens_distortion_);
lens_distortion_ = CardboardLensDistortion_create(
    buffer, size, screen_width_, screen_height_);

CardboardQrCode_destroy(buffer);
```

## Rendering

Rendering content in Cardboard involves the following:

*   Creating textures
*   Getting view and projection matrices for the left and right eyes
*   Creating the renderer and setting the distortion mesh
*   Rendering each frame

### Create textures

All content is drawn onto a texture, which is split into sections for the left and right eyes. These sections are initialized in `_leftEyeTexture` and `_rightEyeTexture`, respectively.

```
void HelloCardboardApp::GlSetup() {
  LOGD("GL SETUP");

  if (framebuffer_ != 0) {
    GlTeardown();
  }

  // Create render texture.
  glGenTextures(1, &texture_);
  glBindTexture(GL_TEXTURE_2D, texture_);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, screen_width_, screen_height_, 0,
               GL_RGB, GL_UNSIGNED_BYTE, 0);

  left_eye_texture_description_.texture = texture_;
  left_eye_texture_description_.left_u = 0;
  left_eye_texture_description_.right_u = 0.5;
  left_eye_texture_description_.top_v = 1;
  left_eye_texture_description_.bottom_v = 0;

  right_eye_texture_description_.texture = texture_;
  right_eye_texture_description_.left_u = 0.5;
  right_eye_texture_description_.right_u = 1;
  right_eye_texture_description_.top_v = 1;
  right_eye_texture_description_.bottom_v = 0;

  //...
  CHECKGLERROR("GlSetup");
}
```

These textures are passed in as parameters to `CardboardDistortionRenderer_renderEyeToDisplay`.

### Get view and projection matrices for left and right eye

First, retrieve eye matrices for the left and right eyes:

```
CardboardLensDistortion_getEyeFromHeadMatrix(
    lens_distortion_, kLeft, eye_matrices_[0]);
CardboardLensDistortion_getEyeFromHeadMatrix(
    lens_distortion_, kRight, eye_matrices_[1]);
CardboardLensDistortion_getProjectionMatrix(
    lens_distortion_, kLeft, kZNear, kZFar, projection_matrices_[0]);
CardboardLensDistortion_getProjectionMatrix(
    lens_distortion_, kRight, kZNear, kZFar, projection_matrices_[1]);
```

Next, get the distortion meshes for each of the eyes and pass it to the distortion renderer:

```
CardboardLensDistortion_getDistortionMesh(lens_distortion_, kLeft, &left_mesh);
CardboardLensDistortion_getDistortionMesh(lens_distortion_, kRight, &right_mesh);
```

### Create the renderer and set the correct distortion mesh

The renderer needs to be initialized only once. Once the renderer is created, set the new distortion mesh for the left and right eyes according to the mesh values returned from the `CardboardLensDistortion_getDistortionMesh` function.

```
distortion_renderer_ = CardboardOpenGlEs2DistortionRenderer_create();
CardboardDistortionRenderer_setMesh(distortion_renderer_, &left_mesh, kLeft);
CardboardDistortionRenderer_setMesh(distortion_renderer_, &right_mesh, kRight);
```

### Rendering the content

For each frame, retrieve the current head orientation from `CardboardHeadTracker_getPose`:

```
CardboardHeadTracker_getPose(head_tracker_, monotonic_time_nano, &out_position[0], &out_orientation[0]);
```

Use the current head orientation with the view and projection matrices to compose a view projection matrix for each of the eyes and render content to the screen:

```
// Draw eyes views
for (int eye = 0; eye < 2; ++eye) {
  glViewport(eye == kLeft ? 0 : screen_width_ / 2, 0, screen_width_ / 2,
             screen_height_);

  Matrix4x4 eye_matrix = GetMatrixFromGlArray(eye_matrices_[eye]);
  Matrix4x4 eye_view = eye_matrix * head_view_;

  Matrix4x4 projection_matrix =
      GetMatrixFromGlArray(projection_matrices_[eye]);
  Matrix4x4 modelview_target = eye_view * model_target_;
  modelview_projection_target_ = projection_matrix * modelview_target;
  modelview_projection_room_ = projection_matrix * eye_view;

  // Draw room and target. Replace this to render your own content.
  DrawWorld();
}
```

Use `CardboardDistortionRenderer_renderEyeToDisplay` to apply the distortion correction to the content, and render the content to the screen.

```
// Render
CardboardDistortionRenderer_renderEyeToDisplay(
    distortion_renderer_, /* target_display = */ 0, /* x = */ 0, /* y = */ 0,
    screen_width_, screen_height_, &left_eye_texture_description_,
    &right_eye_texture_description_);
```

