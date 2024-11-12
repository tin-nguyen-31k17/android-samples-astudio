# Datalogic SDK Sample Template App

Welcome guys! I hope this template app designed can make working with the Datalogic SDK easier, consistent, and efficient! This README is aimed to help you through using this unified UI template to implement various Datalogic SDK APIs in both Kotlin and Java, with a family feeling that makes each app screen feel more "Datalogic".

## Quick Start

1. **Clone this repository** into your local environment.
2. **Open the project in Android Studio** (or your preferred IDE).
3. Build and run the project to get familiar with the base template and UI structure.

## Unified Look & Feel

Our template app uses a cohesive set of colors to ensure a visually consistent experience across screens. The color palette includes:
- **Primary Color (`brand_primary`)**: `#002596` - Our bold main color for headers and footers.
- **Secondary Color (`brand_secondary`)**: `#48f` - Used for secondary elements.
- **Accent Color (`brand_accent`)**: `#26e` - Adds a splash of contrast for buttons and links.
- **Outline and Shadow**: For text and subtle background shadows.

Use these colors to maintain a consistent family feeling across new UI elements you add!

## Navigating the Project Structure

Our app is organized into modular fragments for ease of API integration:
- **HomeFragment** - An intro screen displaying app information.
- **Func0Fragment** - A fully wired-up sample where you can implement Datalogic’s barcode scanning API.
- **Func1Fragment** & **Func2Fragment** - Placeholder fragments where you (or another developer on the team) can add additional Datalogic SDK API functionality.

Each fragment has a predefined spot in the **Bottom Navigation** bar, which allows quick switching between different SDK functionalities.

### Updating Bottom Navigation Icons

Find and replace the icons in the `res/drawable` directory:
- Replace `ic_home`, `ic_func_0`, `ic_func_1`, and `ic_func_2` with your own icons as needed.
- Update the `bottom_navigation_menu.xml` file under `res/menu` to reflect new icons if you add or modify them.

## Implementing Datalogic SDK API Calls

Each `FuncXFragment` (e.g., `Func0Fragment`, `Func1Fragment`) is set up to easily integrate Datalogic SDK functionalities. Here’s a quick guide for each language:

### Kotlin

1. **Get Started with Func0Fragment**: Open `Func0Fragment.kt` to see a sample implementation using Datalogic’s BarcodeManager API. Follow this structure to integrate other SDK functions.
2. **Add a New Fragment**: Duplicate `Func0Fragment.kt` and rename it (e.g., `Func3Fragment.kt`) to create new areas for your SDK features.
3. **UI Consistency**: Use the color resources defined in `res/values/colors.xml` to style buttons, text views, and other elements for a cohesive look.

### Java

1. **Check Out Func0Fragment**: Head over to `Func0Fragment.java` for a sample implementation. Follow this pattern to integrate other Datalogic SDK features.
2. **Create New Fragments**: Copy and rename `Func0Fragment.java` (e.g., `Func3Fragment.java`) to add more SDK functionalities.
3. **Style Elements Consistently**: Refer to `res/values/colors.xml` for color resources, ensuring UI consistency across different features.

## Notes on Styles & Themes

This app applies a unified style for headers, body text, and footers:
- **Footer Background**: The footer uses the primary color with centered text to keep the focus on the brand.
- **Text Style**: Headers and body text are designed for easy readability and professional presentation.

Keep these guidelines in mind to maintain a unified look and feel as you add new features.

## Additional Resources

- **Datalogic SDK Documentation**: Refer to the official [Datalogic Github Documentation](https://datalogic.github.io/) & [Datalogic Developer Guide](https://developer.datalogic.com) for API details and usage examples.
- **Customizable Dimensions**: Tweak dimensions in `res/values/dimens.xml` for different screen sizes.

Now you're ready to create something amazing! Go ahead, implement those Datalogic SDK features, keep the UI consistent, and make this app shine. Happy coding!

