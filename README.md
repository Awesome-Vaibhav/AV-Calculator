
# AV Calculator — Repository Analysis

> **Repository:** `Awesome-Vaibhav/AV-Calculator`  
> **Repository URL:** https://github.com/Awesome-Vaibhav/AV-Calculator  
> **Branch analyzed:** `main`  
> **Analysis date:** 2026-09-08

## 1. Executive Summary

**AV Calculator** is a native Android calculator and conversion toolkit designed as a lightweight alternative inspired by the feature set of Mi Calculator, with a Material You-oriented interface and a broader collection of everyday, scientific, financial, and conversion utilities.

The public repository contains an Android/Gradle project with an `app` module, Kotlin source code, Compose-related Gradle configuration, database/history components, a currency API service, UI packages, utility code, ViewModels, tests, and Gemini API integration/configuration.

The project aims to provide multiple calculator and conversion utilities inside a single Android application.

## 2. Repository Snapshot

| Item | Observation |
|---|---|
| Repository | `Awesome-Vaibhav/AV-Calculator` |
| Visibility | Public |
| Main branch | `main` |
| Commit history | 33 commits shown at analysis time |
| Stars | 1 |
| Forks | 0 |
| Issues | 0 open issues shown |
| Primary platform | Android |
| Build system | Gradle / Kotlin DSL |
| Main module | `app` |
| Main language | Kotlin |
| UI direction | Jetpack Compose / Material-oriented |
| AI integration | Gemini API |
| Local persistence | Database / Room-style architecture |
| Network integration | Currency service API |
| Test structure | Unit-test and Android instrumented-test source sets |

## 3. Product Concept

The central idea is to consolidate many calculator-related utilities into a single application instead of requiring users to install separate apps.

The repository positions AV Calculator around:

- Basic calculator functionality
- Scientific calculator functionality
- Unit conversion
- Currency conversion
- EMI / financial calculations
- GST calculations
- BMI calculations
- Other everyday calculations
- Calculation history
- Material You-inspired UI
- Gemini-powered AI functionality

The project is designed to be more than a simple four-operation calculator.

## 4. High-Level Architecture

The source tree is organized into several responsibility-oriented packages:

```text
app/
└── src/
    ├── main/
    │   ├── java/com/example/
    │   │   ├── api/
    │   │   ├── db/
    │   │   ├── ui/
    │   │   │   ├── components/
    │   │   │   ├── screens/
    │   │   │   ├── theme/
    │   │   │   └── Screen.kt
    │   │   ├── util/
    │   │   ├── viewmodel/
    │   │   └── MainActivity.kt
    │   ├── res/
    │   └── AndroidManifest.xml
    ├── androidTest/
    └── test/

This is a sensible starting structure for a modern Android application because presentation, persistence, networking, reusable UI components, state-management code, and utilities are separated.

5. Package Analysis

api/

The repository contains:

CurrencyService.kt

Responsibility

The API layer handles external network-related functionality, particularly currency exchange-rate information.

Expected responsibilities include:

Retrieving currency exchange rates

Handling API communication

Separating network logic from UI

Providing exchange-rate data to ViewModels or repositories


Recommendation

As the project grows, consider separating:

api/
├── CurrencyService.kt
├── dto/
├── response/
├── error/
└── NetworkModule.kt


---

6. Database Layer

The db/ package contains:

AppDatabase.kt
HistoryDao.kt
HistoryEntity.kt
HistoryRepository.kt

This indicates a local persistence architecture for calculation history.

Responsibilities

AppDatabase.kt

Responsible for configuring the local database.

HistoryEntity.kt

Represents a calculation-history record.

HistoryDao.kt

Provides database operations such as inserting and retrieving history.

HistoryRepository.kt

Acts as an abstraction between the persistence layer and the rest of the application.

Architecture

UI
 ↓
ViewModel
 ↓
HistoryRepository
 ↓
HistoryDao
 ↓
AppDatabase

This is a good architecture for persistent calculation history.


---

7. UI Architecture

The UI package is divided into:

ui/
├── components/
├── screens/
├── theme/
└── Screen.kt

components/

Reusable UI elements can be placed here, such as:

Calculator buttons

Cards

Dialogs

Input fields

Conversion selectors

Result displays


screens/

Contains individual application screens.

Possible screen categories include:

Calculator

Scientific calculator

Finance calculators

Converter

Currency converter

History

Settings


theme/

Responsible for:

Colors

Typography

Shapes

Material theme

Dark/light appearance


Screen.kt

Likely contains screen/navigation definitions.

This separation is useful for a multi-feature calculator application.


---

8. ViewModel Layer

The repository contains a dedicated:

viewmodel/

package.

ViewModels should handle:

UI state

Calculator state

History state

Network state

User actions

Business/application logic coordination


A clean flow would be:

Composable UI
     ↓
 ViewModel
     ↓
Repository
     ↓
Data / API

The UI should ideally avoid performing database or network operations directly.


---

9. Utility Layer

The repository contains a:

util/

package.

This is appropriate for shared functionality such as:

Mathematical helpers

Formatting

Validation

Unit conversions

Financial formulas

Constants

Extensions


However, as the application grows, calculator logic should be moved into clearer domain-specific packages.

For example:

domain/
├── calculator/
├── scientific/
├── finance/
├── converter/
└── health/

This prevents a large util package from becoming difficult to maintain.


---

10. Technology Stack

The project uses a modern Android development stack involving:

Kotlin

Gradle Kotlin DSL

Android Gradle Plugin

Jetpack Compose

Material Design

ViewModel architecture

KSP

Local database

Currency API

Gemini API

Unit tests

Android instrumented tests

Roborazzi-related tooling

Secrets configuration


Why this stack fits the project

Jetpack Compose

Compose is well suited for calculator applications because:

UI components are highly reusable.

State-driven UI works well for calculator input.

Responsive layouts are easier to implement.

Theme customization is straightforward.


ViewModel

ViewModel is useful for:

Maintaining calculator state

Handling screen state

Surviving configuration changes

Separating UI and application logic


Database

A local database is appropriate for calculation history.

KSP

KSP is useful for Kotlin-based annotation processing and libraries such as Room.

Roborazzi

Roborazzi indicates an intention to support screenshot-based UI testing.

Secrets configuration

Secrets management is particularly important because the application integrates with Gemini.


---

11. Gemini / AI Integration

The repository contains:

.env.example

and the README provides instructions for configuring:

GEMINI_API_KEY=your_key_here

This indicates that Gemini integration is an intentional feature of the project.

Recommended Production Architecture

Instead of exposing a privileged API key inside an APK:

Android App
     ↓
Backend / Secure API
     ↓
Gemini API
     ↓
Validated Response
     ↓
Android App

For production:

Never commit real API keys.

Never hardcode production secrets.

Use environment variables or CI/CD secret storage.

Rotate compromised keys.

Apply request limits.

Validate AI responses.



---

12. Current Setup Process

The repository README currently provides a development setup flow roughly consisting of:

1. Open the project in Android Studio.


2. Allow Android Studio/Gradle to resolve required project configuration.


3. Create a .env file.


4. Configure the Gemini API key.


5. Follow the repository's debug-signing setup instruction.


6. Run the application on an Android emulator or physical device.



Recommended Improved Setup Documentation

A better README should provide:

Requirements
    ↓
Clone Repository
    ↓
Open in Android Studio
    ↓
Configure local secrets
    ↓
Sync Gradle
    ↓
Run tests
    ↓
Build APK
    ↓
Install / Run


---

13. Product Strengths

13.1 Broad Feature Scope

The project combines several calculator categories into one application.

This creates a stronger proposition than a simple calculator.

13.2 Modern Android Architecture

The project uses modern Android technologies including Kotlin, Compose, ViewModels, Gradle Kotlin DSL, and database architecture.

13.3 Database Architecture

The dedicated:

Entity
DAO
Database
Repository

structure is a strong foundation for calculation history.

13.4 Currency API

The presence of a dedicated currency service shows that external data integration has been considered separately from UI code.

13.5 AI Capability

Gemini integration creates opportunities for:

Natural-language calculations

Formula explanations

Step-by-step mathematical explanations

Smart calculator assistance

Educational functionality


13.6 Material You Direction

The Material-oriented UI makes the project feel more aligned with modern Android applications.


---

14. Current Weaknesses

14.1 README Documentation

The biggest visible weakness is documentation quality.

The README currently focuses heavily on setup instructions rather than presenting the application as a polished product.

A better README should include:

Screenshots

Features

Architecture

Tech stack

Installation

AI setup

Security

Testing

Roadmap

Releases

Contribution instructions

License



---

15. API Key Security

The Gemini API key must never be committed to GitHub.

The .env.example approach is useful for local development:

GEMINI_API_KEY=your_key_here

But production applications should use:

CI/CD Secret Store
        ↓
Secure Build
        ↓
Backend/API
        ↓
Gemini

If an actual key is accidentally committed:

1. Revoke it.


2. Generate a new key.


3. Remove it from the repository history if necessary.


4. Check GitHub secret scanning.


5. Update production configuration.




---

16. Testing Analysis

The project contains both:

app/src/test/

and:

app/src/androidTest/

This is a good foundation.

However, calculator applications require particularly strong correctness testing.

Recommended Tests

Basic Arithmetic

2 + 2 = 4
10 - 3 = 7
6 × 7 = 42
20 ÷ 5 = 4

Operator Precedence

2 + 3 × 4 = 14

Scientific Calculator

sin(0) = 0
sqrt(25) = 5
2^3 = 8

Error Cases

1 / 0
sqrt(-1)
invalid expression
empty input
multiple operators

Financial Calculations

Test:

EMI

GST

Interest

Discount

Loan calculations


against known reference values.

Database

Verify:

History insertion

History retrieval

History deletion

Persistence after restart

Empty-history handling



---

17. Financial Calculation Validation

Financial calculators should use deterministic formulas.

For example:

Input
  ↓
Formula
  ↓
Calculated Result
  ↓
Expected Reference Result
  ↓
Tolerance Check

Financial calculations should not depend on an LLM.

Gemini can explain the calculation, but the actual numerical result should preferably be produced by deterministic application code.


---

18. Recommended Architecture

A more scalable architecture could be:

com.example
│
├── data
│   ├── local
│   │   ├── AppDatabase
│   │   ├── HistoryDao
│   │   └── HistoryEntity
│   │
│   └── remote
│       └── CurrencyService
│
├── domain
│   ├── calculator
│   ├── scientific
│   ├── finance
│   ├── converter
│   └── health
│
├── presentation
│   ├── navigation
│   ├── screens
│   ├── components
│   └── theme
│
├── viewmodel
│
├── util
│
└── MainActivity

This creates a clearer separation between:

What the application does
        ↓
      DOMAIN

Where data comes from
        ↓
       DATA

How the application looks
        ↓
  PRESENTATION


---

19. Feature Matrix

Feature	Evidence / Positioning	Assessment

Basic calculator	Product positioning	Core
Scientific calculator	Repository topic/product positioning	Core
Unit converter	Repository topic/product positioning	Core
Currency converter	CurrencyService.kt + repository positioning	Implemented architecture
EMI calculator	Repository positioning	Core
GST calculator	Repository positioning	Core
BMI calculator	Repository positioning	Core
Calculation history	Database package	Strong architecture
Material You UI	Product positioning	Core design goal
Gemini AI	.env.example + metadata + README	Integrated capability
Unit tests	app/src/test	Test structure present
Instrumented tests	app/src/androidTest	Test structure present
UI screenshot testing	Roborazzi tooling	Tooling present



---

20. UX Improvements

The application could be made more polished by turning the home screen into a calculator hub.

Suggested structure:

AV Calculator
────────────────────────

Search calculators...

⭐ Favorites

BASIC
┌─────────────────────┐
│ Calculator           │
└─────────────────────┘

SCIENTIFIC
┌─────────────────────┐
│ Scientific           │
│ Formula              │
└─────────────────────┘

FINANCE
┌─────────────────────┐
│ EMI                  │
│ GST                  │
│ Interest             │
└─────────────────────┘

CONVERTERS
┌─────────────────────┐
│ Currency             │
│ Length               │
│ Weight               │
│ Temperature          │
│ Data                 │
└─────────────────────┘

HEALTH
┌─────────────────────┐
│ BMI                  │
│ BMR                  │
└─────────────────────┘

RECENT CALCULATIONS
────────────────────────

Recommended UX features:

Search calculators

Favorites

Recently used tools

Persistent history

Copy result

Share result

Haptic feedback

Dark mode

Light mode

System theme

Keyboard support

Landscape scientific calculator

Tablet/adaptive layouts

Accessibility support

Better error messages



---

21. AI Math Assistant

Gemini could be positioned as:

AI Math Assistant

instead of being presented simply as an API integration.

Example requests:

Calculate EMI for ₹5,00,000 at 9% for 5 years.

Explain compound interest.

Convert 72°F to Celsius.

Solve 2x + 5 = 17 and explain the steps.

The AI feature could provide:

Natural-language calculations

Step-by-step explanations

Formula explanations

Learning assistance

Smart suggestions

Context-aware calculator help



---

22. Important AI Design Principle

The application should distinguish between:

Deterministic calculations

These should be handled locally:

2 + 2
EMI
GST
BMI
Unit conversion
Scientific calculations

AI tasks

These can be handled by Gemini:

Explain this formula.
Explain the steps.
Help me understand this result.
Convert natural language into a calculation.

This improves reliability because LLMs should not be treated as the source of truth for numerical calculations when deterministic formulas are available.


---

23. Security Recommendations

Secrets

Never commit API keys.

Keep .env ignored by Git.

Use .env.example only as a template.

Use CI/CD secrets for production.


Network

Use HTTPS.

Validate server responses.

Handle API failures gracefully.

Add timeout handling.

Avoid unnecessary API calls.


User Input

Validate:

Numeric input

Expression input

Currency codes

Units

Financial parameters

AI prompts


Financial Information

Clearly state that financial calculators provide estimates and may not account for:

lender-specific fees;

taxes;

rounding;

contractual conditions;

additional charges.



---

24. Performance Recommendations

Calculator operations themselves are computationally inexpensive.

Potential performance issues are more likely to come from:

unnecessary Compose recompositions;

repeated database queries;

network requests;

currency API calls;

AI requests.


Recommended:

Cache exchange rates.

Use coroutines for I/O.

Keep expensive operations outside composables.

Use stable UI state.

Debounce AI input.

Avoid unnecessary recomposition.

Keep formula engines lightweight.

Perform local calculations synchronously when appropriate.



---

25. Testing Pyramid

Recommended testing architecture:

UI / E2E
                ─────────
              Integration
             ─────────────
           ViewModel Tests
          ─────────────────
        Domain/Formula Tests
       ─────────────────────

The largest number of tests should be deterministic domain/formula tests.


---

26. Documentation Roadmap

Recommended documentation files:

README.md
ARCHITECTURE.md
CONTRIBUTING.md
SECURITY.md
CHANGELOG.md
LICENSE

Optional documentation:

docs/
├── calculator-engine.md
├── financial-formulas.md
├── ai-integration.md
└── release-process.md


---

27. Recommended README Structure

A professional README could follow:

AV Calculator
│
├── Hero section
├── Screenshots
├── Features
├── Why AV Calculator?
├── Demo
├── Installation
├── Tech Stack
├── Architecture
├── AI Assistant
├── Privacy & Security
├── Testing
├── Roadmap
├── Contributing
├── License
└── Credits


---

28. Overall Assessment

Category	Score

Product Concept	8.5/10
Architecture	8/10
Documentation	4/10
Security	6.5/10
Testing Readiness	6.5/10
Portfolio Readiness	7/10
Overall	7.1/10


Product Concept — 8.5/10

The idea is strong because it combines everyday calculation, science, finance, health, and conversion tools.

Architecture — 8/10

The separation into API, database, UI, ViewModel, and utility packages is a good foundation.

The architecture can become even stronger through domain/data/presentation separation.

Documentation — 4/10

The README is currently the biggest visible weakness.

It should communicate the product and technical architecture rather than primarily providing setup instructions.

Security — 6.5/10

The secrets configuration approach is useful, but the production Gemini architecture needs stronger documentation and preferably backend mediation.

Testing — 6.5/10

The presence of test source sets and Roborazzi tooling is positive.

More deterministic formula and financial regression tests are recommended.

Portfolio Readiness — 7/10

The project has a strong technical foundation.

Screenshots, releases, documentation, testing evidence, and polished branding would significantly improve its portfolio value.


---

29. Priority Action Plan

P0 — Before Public Release

1. Verify that no API keys are committed.


2. Make sure a fresh clone builds successfully.


3. Add comprehensive calculator tests.


4. Add financial formula tests.


5. Add screenshots to README.


6. Document supported Android versions.


7. Add a clear license.


8. Publish APK/AAB releases through GitHub Releases.



P1 — Product Polish

1. Redesign README.


2. Add favorites.


3. Add recently used calculators.


4. Improve error messages.


5. Improve accessibility.


6. Add adaptive layouts.


7. Improve calculation history.


8. Add deterministic calculation engines.



P2 — Differentiation

1. AI Math Assistant.


2. Natural-language calculation.


3. Step-by-step explanations.


4. Smart formula suggestions.


5. Advanced financial calculators.


6. Customizable calculator layout.


7. Offline-first functionality.




---

30. Suggested Future Feature Roadmap

Version 1.0
│
├── Basic Calculator
├── Scientific Calculator
├── Unit Converter
├── Currency Converter
├── EMI Calculator
├── GST Calculator
├── BMI Calculator
└── History

Version 1.1
│
├── Favorites
├── Recent Calculators
├── Improved History
├── Haptic Feedback
└── Adaptive Layout

Version 1.2
│
├── AI Math Assistant
├── Natural Language Input
├── Step-by-Step Explanations
└── Formula Suggestions

Version 2.0
│
├── Advanced Financial Suite
├── Graphing Calculator
├── Equation Solver
├── Offline Currency Cache
├── Tablet Optimization
└── Advanced Accessibility


---

31. Final Conclusion

AV Calculator has the foundation of a genuinely useful Android utility application.

Its strongest characteristics are:

Broad calculator functionality

Modern Android project structure

Kotlin/Compose direction

Persistent history architecture

Currency API integration

Gemini AI capability

Material-oriented UI


The biggest opportunity is productization.

The project can move from a developer project to a polished portfolio/release application by focusing on:

Professional Documentation
        +
Reliable Automated Tests
        +
Secure API Architecture
        +
Polished UX
        +
Screenshots / Demo
        +
Production Release

With these improvements, AV Calculator can be presented as a complete:

> All-in-One Android Calculation Platform



rather than simply another calculator application.


---

32. Source

GitHub Repository:

https://github.com/Awesome-Vaibhav/AV-Calculator

Analysis based on the publicly visible repository structure, README, configuration, and source organization available on the main branch at the time of analysis.

> Note: This document is a repository-level analysis. It does not claim runtime testing of every feature or verification of every individual calculator formula.
