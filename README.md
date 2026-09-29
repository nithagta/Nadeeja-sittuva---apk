# 🇱🇰 Sittuva (සිට්ටුව) — Sri Lankan Rotating Savings App

[![Build & Release Debug APK](https://github.com/nithishnawagam/sittuva/actions/workflows/build-apk.yml/badge.svg)](https://github.com/nithishnawagam/sittuva/actions/workflows/build-apk.yml)

**Sittuva** is a modern, secure, Sinhala-first Android application designed for Sri Lankan rotating savings and credit associations (සිට්ටු ක්‍රමය / ROSCA). Members contribute an agreed fixed sum each cycle, and the full pool is disbursed directly to one participant according to a predefined, locked rotation order.

---

## 🚀 Automated APK Builds & Direct Downloading

This project includes a **GitHub Actions CI/CD workflow** (`.github/workflows/build-apk.yml`) that automatically builds, tests, and publishes installable Android Debug APKs.

### 📥 How to Download the APK:

#### Option 1: Direct Download via GitHub Releases (Recommended)
1. Navigate to the **[Releases](../../releases)** tab on GitHub.
2. Under the latest release (or `latest-debug`), click on **`sittuva-debug.apk`** under the **Assets** section.
3. Open the downloaded `.apk` file on your Android device to install.

#### Option 2: Download from GitHub Actions Artifacts
1. Go to the **[Actions](../../actions)** tab.
2. Click on the latest workflow run for `Build & Release Debug APK`.
3. Scroll down to the **Artifacts** section at the bottom of the page.
4. Click on **`sittuva-debug-apk`** to download the zipped APK.

#### Option 3: Manual Workflow Trigger
1. In the **Actions** tab, select **Build & Release Debug APK**.
2. Click **Run workflow**.
3. (Optional) Provide a custom tag or release title, and click **Run workflow**.

#### Option 4: Tag-Based Release
Pushing any Git tag starting with `v` (e.g., `v1.0.0`) automatically creates a full GitHub Release with the APK attached:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## 🛠️ Local Development & Build

### Prerequisites
- JDK 17 (Java Development Kit)
- Android SDK (API 24 to 36)

### Run Unit & Robolectric Tests:
```bash
./gradlew testDebugUnitTest
```

### Build Debug APK Locally:
```bash
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🌟 Key Application Features

- **No Sittuva Commission:** `Total Payout = Contribution Amount × Total Number of Participants`. The scheduled recipient receives 100% of the gathered pool without platform cuts.
- **Admin is a Participant:** Total participants = Admin + Members. Admin is included in the rotation and calculation.
- **Customizable Inputs:**
  - Participant count: **2 to 49** members with real-time validation.
  - Contribution amount: Custom LKR numeric entry.
- **Payment Frequencies:** Weekly (`සතියකට වරක්`) or Monthly (`මාසයකට වරක්`).
- **Automatic Duration:** Dependent on participant count (e.g., 5 participants weekly = 5 weeks).
- **Core Rules Lock:** Once a group officially starts, financial rules and rotation orders are locked to protect community funds.
- **Sri Lankan Identity & Theme:** Radiant red luminous theme (`#8E0E1F`), lion flag gold accents (`#FFB300`), Sinhala-first bilingual interface with quick English toggle.
- **Deep Link Invitations:** Shareable cards and deep links (`sittuva://invite/{code}`).
- **Phone OTP Authentication:** Simulated secure OTP verification flow with timers.
- **Tokenized Card Payments:** PCI-compliant simulated card tokenization (Visa & Mastercard) with verified provider callbacks.
- **Audit Logs:** Immutable chronological log of all administrative and financial actions.

---

## 📱 App Metadata

- **Application ID:** `com.aistudio.sittuva.nwdpke`
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36 (Android 16)
- **Language:** Kotlin & Jetpack Compose
- **Local Persistence:** Room Database with KSP
