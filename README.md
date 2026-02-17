#  PetManager AI

**PetManager AI** is a high-performance Android application designed for modern pet owners seeking a data-driven approach to pet care. This application seamlessly combines traditional record-keeping with state-of-the-art **Multimodal AI** to provide an intelligent, ad-free experience.    



##  Project Overview

PetManager AI serves as a comprehensive hub for managing your pet's lifestyle and health. By utilizing advanced LLM processing, the app transforms simple photos into actionable health logs.

### 1. Core Use Cases
* **Health & Behavioral Tracking**: Monitor a pet's growth and mood over time through a visual timeline.
* **Emergency Readiness**: Keep vital statistics—such as weight, age, and medications—accessible at a moment's notice.
* **Automated Documentation**: Utilize AI to generate descriptions and care logs, significantly reducing manual data entry.  



##  Key Implementations & Features

### Advanced AI Integration
The core selling point of this application is the integration of the **meta-llama/llama-4-scout-17b-16e-instruct** model via the **Groq Cloud API**. This allows for:
* **Image-to-Insight Processing**: When a user uploads a pet photo, the AI analyzes pixels to determine the breed and current "emotional state".
* **Predictive Care**: Based on the identified breed, the AI suggests standard medications and dietary needs specific to that animal.
* **Visual Summarization**: The AI generates a natural language description (approx. 30 words) to help owners remember specific visual details of their pet at that time.

### Sleek, Personalized UI
* **Dynamic Log Stacking**: Implemented a "Latest-First" logic where the newest health logs automatically appear at the top of the feed.
* **Modern Log Cards**: Each log is encapsulated in a "Safe-Style" card with rounded corners, subtle shadows, and bolded headers for high readability.
* **Profile Personalization**: The app tracks specific pet data, including weight (Kgs), gender, breed, and a calculated age based on the Date of Birth.

### Performance & Stability Enhancements
* **Ad-Free Experience**: We completely stripped the Google AdMob SDK from the project to ensure a clean, distraction-free environment for the user.
* **Memory-Efficient Imaging**: Implemented custom Bitmap decoding and resizing logic (512px max) to prevent `OutOfMemory` crashes while ensuring fast API response times.
* **Robust Data Persistence**: Used **Google GSON** to handle local JSON storage, ensuring that even if the app is closed, every AI log is saved and retrieved perfectly upon the next launch.



##  Technical Processing Logic

### The "Safe-Style" Rendering Pipeline
To ensure the app never crashes, we utilized a **Programmatic UI** approach rather than complex XML inflation for logs:
1.  **JSON Fetch**: The app reads the `logs_[petname].json` file.
2.  **Object Mapping**: Data is mapped to a static `PetLogEntry` class.
3.  **Spannable Styling**: The Java code creates a `TextView`, applies a `log_card_bg` drawable, and uses `SpannableString` to bold specific headers like "BREED & MOOD".
4.  **Instant Injection**: The view is injected into the `logsContainer` without needing to refresh the entire activity.

### Secure API Communication
* **Asynchronous Requests**: Utilized **OkHttp** with a `Callback` system to ensure the UI does not freeze during AI processing.
* **Timeout Protection**: Configured 60-second connection and read timeouts to handle slow network conditions gracefully.



##  Unique Selling Points (USPs)
* **Privacy First**: All pet data and health logs are stored locally on the user's device.
* **High Stability**: The "Crash-Proof" UI setup includes null-checks on every major UI element, specifically optimized for modern Android architectures.
* **Niche Tech Stack**: Showcases the ability to combine traditional Android (Java) with cutting-edge AIML (Llama-4).



##  Setup & Requirements

### Technical Requirements
* **Minimum SDK**: API 21+ (Android 5.0)
* **Language**: Java
* **IDE**: Android Studio
* **Dependencies**: Google GSON, OkHttp, Material Design Components

### Installation
1.  Clone the repository to your local machine.
2.  Open the project in Android Studio.
3.  Obtain an API key from the [Groq API Portal](https://console.groq.com/).
4.  Insert your API key into `DetailActivity.java`.
5.  Build and Run the application on your physical device or emulator.



## 🤝 Acknowledgments & Baseline Credit

The foundational UI and initial logic for this application were referenced from the **PetManager-master** project.

* **Original Repository:** [PetManager-master](https://github.com/pets/dog-cat-petmanager)
* **Baseline Owner:** `pets`

This version has been significantly refactored and evolved by **Harsh Chinchakar** to include modern AI integrations, custom local storage logic, and a refined, ad-free user experience.
