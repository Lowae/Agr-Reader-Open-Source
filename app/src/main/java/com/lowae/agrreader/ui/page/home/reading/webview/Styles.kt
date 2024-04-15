package com.lowae.agrreader.ui.page.home.reading.webview

import com.lowae.agrreader.data.model.preference.BasicFontsPreference
import com.lowae.agrreader.ui.page.home.reading.ReadingStylesConfiguration

fun getStyle(readingStyles: ReadingStylesConfiguration, colorScheme: HexColorScheme): String {
    return """
:root {
  ${if (readingStyles.font == BasicFontsPreference.External.value) """--font-family: "external font";""" else ""}
  --background-color: ${colorScheme.surface};
  --primary: ${colorScheme.primary};
  --primary-hover: ${colorScheme.primary};
  --primary-focus: ${colorScheme.secondaryContainer};
}        

[data-theme="light"],
:root:not([data-theme="dark"]) {
  --background-color: ${colorScheme.surface};
  --primary: ${colorScheme.primary};
  --primary-hover: ${colorScheme.primary};
  --primary-focus: ${colorScheme.secondaryContainer};
}

[data-theme="dark"] {
  --background-color: ${colorScheme.surface};
  --primary: ${colorScheme.primary};
  --primary-hover: ${colorScheme.primary};
  --primary-focus: ${colorScheme.secondaryContainer};
}

body {
  --font-size: ${readingStyles.fontSize}px;
  --font-weight: ${readingStyles.fontWeight};
  font-size: ${readingStyles.fontSize}px;
  font-weight: ${readingStyles.fontWeight};
  text-align: ${readingStyles.textAlign};
  letter-spacing: ${readingStyles.letterSpacing}px;
  line-height: ${readingStyles.lineHeight};
}

td,th {
  padding: calc(var(--spacing)/ 4) calc(var(--spacing)/ 4);
}

pre {
  padding: calc(var(--spacing)/ 2);
}

iframe {
  max-width: 100%
}
"""
}