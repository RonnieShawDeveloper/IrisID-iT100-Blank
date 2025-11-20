# IrisID iT100 Blank

## Overview

**IrisID iT100 Blank** is a sample Android application project designed for the IrisID iT100 biometric device. This project demonstrates the integration and use of the iT100 device SDK, providing a foundation for further development of biometric authentication, user management, and device configuration features.

## Features
- Iris recognition and biometric authentication
- Admin and system login flows
- User management (add, edit, search users)
- Device settings and configuration (network, display, date/time, etc.)
- QR code scanning
- Custom UI components for dialogs, toasts, and input fields
- Service for background operations

## Project Structure

```
IrisID-iT100-Blank/
├── app/
│   ├── build.gradle
│   ├── libs/
│   │   └── it100_v1_08_02.aar   # IrisID iT100 SDK
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/
│   │       │   └── com/irisid/user/it100_sample/
│   │       │       ├── Activity/         # Main, Admin, Capture, Login, etc.
│   │       │       ├── Common/           # Constants, UI, Utilities
│   │       │       ├── Service/          # LauncherService
│   │       │       ├── Settings/         # Fragments for device settings
│   │       │       └── UserList/         # User management and list
│   │       ├── res/                      # Layouts, drawables, values, etc.
│   │       └── ...
│   └── ...
├── build.gradle
├── settings.gradle
└── ...
```

## Key Modules & Components

### Activities
- **MainActivity**: Main entry point and home screen
- **CaptureActivity**: Handles iris capture and recognition
- **AdminLoginActivity, IdPwActivity, SystemAdminActivity, InitPasswordActivity**: Authentication and admin flows
- **ScanQRCodeActivity**: QR code scanning for device/user setup

### Fragments (Settings)
- **ActivationFragment, ApplicationFragment, DateTimeFragment, DisplayBrightnessFragment, ExternalDeviceFragment, GeneralFragment, ModesFragment, NetworkFragment, NetworkFragment5, SettingOptionsFragment, TabEthernetFragment, TabImagesFragment, TabMoviesFragment, TabPagerAdapter, TabWifiFragment, VideoAdapter, WallpaperFragment**: Modular device and application settings

### User Management
- **ItemsListActivity, ItemsListFragment, ListViewAdapter, Item, ItemDetailFragment, ItemDetailFragmentNew, ItemDetailFragmentPolicy, ItemDetailFragmentWithCard, ItemEmptyFragment, ItemEmptyUserFragment, ItemEmptyUserFragmentWithCard**: User CRUD and detail views

### Common Utilities & UI
- **ConstData**: Constants used throughout the app
- **ui/**: Custom UI components (dialogs, toasts, text views, etc.)
- **util/Logger.java**: Logging utility

### Service
- **LauncherService**: Background service for device operations

### SDK Integration
- **it100_v1_08_02.aar**: The proprietary SDK for the IrisID iT100 device, included in `app/libs/` and `libs/`

## Permissions
The app requests the following permissions in `AndroidManifest.xml`:
- SET_WALLPAPER
- INTERNET
- WAKE_LOCK
- VIBRATE
- ACCESS_WIFI_STATE
- CHANGE_WIFI_STATE
- ACCESS_NETWORK_STATE
- WRITE_EXTERNAL_STORAGE

## Build & Dependencies
- **minSdkVersion**: 23
- **targetSdkVersion**: 28
- **compileSdkVersion**: 28
- **Dependencies**:
  - Android Support Libraries (AppCompat, ConstraintLayout, Design)
  - Glide (image loading)
  - ZXing (QR code)
  - Gson (JSON parsing)
  - IrisID iT100 SDK (`it100_v1_08_02.aar`)

## Getting Started
1. Clone the repository:
   ```
   git clone https://github.com/RonnieShawDeveloper/IrisID-iT100-Blank.git
   ```
2. Open in Android Studio.
3. Ensure the `it100_v1_08_02.aar` SDK is present in `app/libs/` and `libs/`.
4. Build and run on a compatible Android device (API 23+).

## Customization
- Update package names, resources, and UI as needed for your deployment.
- Extend or modify Activities and Fragments to add new features.
- Integrate with your backend or authentication systems as required.

## License
This project is provided as a sample and may require a commercial license for the IrisID SDK. See the SDK documentation for details.

---
**Last updated:** November 20, 2025

