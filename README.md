# Eminfo

**Eminfo** is an offline-first Android application designed to provide first responders with instant access to critical medical and contact information. Built with modern Android technologies, it prioritizes speed, privacy, and accessibility, ensuring that life-saving data is available when it matters most—even without an internet connection.

## Key Features

*   **Offline Storage**: All data is stored locally on the device using [Room Database](https://developer.android.com/training/data-storage/room). No internet connection is required, ensuring reliability in remote areas and total user privacy.
*   **Comprehensive Profile**: detailed fields for:
    *   **Personal Info**: Name, DOB, Blood Type, Height.
    *   **Medical Info**: Allergies, Medical Conditions, Current Medications.
    *   **Physician & Insurance**: Contact details for primary care and insurance policy information.
*   **Emergency QR Code**: Generates a scannable QR code containing selected medical details.
    *   **Customizable**: Users can choose which fields to include in the QR code.
    *   **Shareable**: detailed options to share the image or save it to the gallery for printing or lock screen use.
*   **Quick Access Widget**: (In progress) Home screen usage for instant visibility.
*   **Emergency Contacts**: Dedicated section for primary emergency contacts.
*   **Modern UI/UX**: Built entirely with **Jetpack Compose** and **Material 3**, featuring a fluid, responsive design with "Hero" headers and intuitive navigation.

## Tech Stack

This project leverages the latest modern Android development tools and libraries:

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Data Storage**: [Room Persistence Library](https://developer.android.com/jetpack/androidx/releases/room)
*   **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
*   **QR Generation**: [ZXing ("Zebra Crossing")](https://github.com/zxing/zxing)
*   **Asynchronous Processing**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Dependency Injection**: (Planned/In-progress - currently manual or implicit via ViewModels)

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

*   [Android Studio Iguana](https://developer.android.com/studio) or newer.
*   JDK 17 or higher (usually bundled with Android Studio).
*   Android SDK API Level 34 (UpsideDownCake).

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/ChrisQuint0/Eminfo.git
    cd Eminfo
    ```

2.  **Open in Android Studio**
    *   Launch Android Studio -> `File` -> `Open` -> Select the `Eminfo` directory.

3.  **Sync Project with Gradle Files**
    *   Click the "Sync Project with Gradle Files" button (elephant icon) or use `File` -> `Sync Project with Gradle Files`.

4.  **Run the App**
    *   Connect an Android device or start an Emulator.
    *   Click the **Run** button (green play icon) or press `Shift + F10`.

## Architecture Overview

The app follows the recommended **App Architecture Guide**:

1.  **UI Layer**: Composable functions (`ProfileScreen`, `QRCodeScreen`, etc.) that observe state from ViewModels.
2.  **ViewModel**: Manages UI state (`ProfileViewModel`) and communicates with the Data Layer.
3.  **Data Layer**: Repositories and Data Sources (Room DAO) that handle data operations.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Built with ❤️ for safety and peace of mind.*
