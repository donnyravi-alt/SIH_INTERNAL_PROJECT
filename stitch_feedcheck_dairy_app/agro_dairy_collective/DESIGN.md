---
name: Agro-Dairy Collective
colors:
  surface: '#fcf9f8'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0eded'
  surface-container-high: '#eae7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1b1b1b'
  on-surface-variant: '#42493e'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#72796e'
  outline-variant: '#c2c9bb'
  surface-tint: '#3b6934'
  primary: '#154212'
  on-primary: '#ffffff'
  primary-container: '#2d5a27'
  on-primary-container: '#9dd090'
  inverse-primary: '#a1d494'
  secondary: '#006e1c'
  on-secondary: '#ffffff'
  secondary-container: '#91f78e'
  on-secondary-container: '#00731e'
  tertiary: '#393a29'
  on-tertiary: '#ffffff'
  tertiary-container: '#50513f'
  on-tertiary-container: '#c3c4ac'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#bcf0ae'
  primary-fixed-dim: '#a1d494'
  on-primary-fixed: '#002201'
  on-primary-fixed-variant: '#23501e'
  secondary-fixed: '#94f990'
  secondary-fixed-dim: '#78dc77'
  on-secondary-fixed: '#002204'
  on-secondary-fixed-variant: '#005313'
  tertiary-fixed: '#e4e4cc'
  tertiary-fixed-dim: '#c8c8b0'
  on-tertiary-fixed: '#1b1d0e'
  on-tertiary-fixed-variant: '#474836'
  background: '#fcf9f8'
  on-background: '#1b1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '700'
    lineHeight: 28px
  body-lg-mobile:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 8px
  touch-target-min: 56px
  container-padding-mobile: 16px
  container-padding-desktop: 32px
  gutter-md: 16px
  stack-gap: 12px
---

## Brand & Style

The design system is engineered for utility, reliability, and extreme clarity, catering to rural dairy farmers who require immediate access to vital agricultural data. The brand personality is **grounded, professional, and supportive**, bridging the gap between traditional farming and modern technology.

The aesthetic follows a **Modern-Professional** direction with a focus on high-contrast utility. It avoids unnecessary decorative elements like blurs or deep shadows to ensure high performance on low-end mobile devices and visibility under direct sunlight. The interface uses a "Flat 2.0" approach—relying on clear containment, distinct color blocking, and large touch targets to ensure accessibility for users with varying levels of digital literacy.

## Colors

This color palette is designed for high-glare environments. The **Neutral Sand (#F5F5DC)** is the primary background color for mobile views, chosen specifically to reduce the harsh blue-light eye strain and screen glare common with pure white backgrounds in outdoor settings.

- **Primary Green (#2D5A27):** Used for headers, primary actions, and branding to establish authority.
- **Accent Green (#4CAF50):** Used for success states, growth indicators, and active toggles.
- **Functional Colors:** Warning Orange and Critical Red are used strictly for urgent alerts regarding cattle health or payment discrepancies.
- **High Contrast:** Text must maintain a minimum 4.5:1 ratio against the sand background to ensure readability for older users.

## Typography

The design system utilizes **Inter** for its exceptional legibility and support for wide character sets, essential for bilingual English and Telugu layouts. 

- **Readability First:** Body font sizes start at 16px, with a preferred 18px for mobile reading to accommodate users in low-light or vibrating environments (e.g., while traveling).
- **Bilingual Handling:** Telugu script typically requires 15-20% more vertical line height than Latin script. Always use the defined `lineHeight` tokens to prevent clipping of descenders in Indian scripts.
- **Weight Usage:** Bold weights (700) are reserved for primary data points like milk yield volume or payment amounts.

## Layout & Spacing

This design system uses a **Fluid-Fixed Hybrid** model. 

- **Mobile (Farmer App):** A single-column layout with 16px side margins. Elements are stacked vertically to prioritize clear reading order. The 56px minimum touch target height is strictly enforced for all interactive elements to accommodate "fat-finger" errors and outdoor usage.
- **Desktop (Cooperative Dashboard):** A 12-column grid with a fixed 1200px max-width. It uses a sidebar navigation to maximize vertical space for data-dense tables and charts.
- **Spacing Rhythm:** Based on an 8px grid. Most vertical gaps between related items should be 12px (`stack-gap`), while distinct sections should use 24px or 32px.

## Elevation & Depth

To remain compatible with low-end Android hardware and maintain high contrast, the design system avoids complex shadows. 

- **Surface Tiers:** Hierarchy is established through **Tonal Layering**. The base background is Sand (#F5F5DC). Cards and containers sit on top in pure White (#FFFFFF).
- **Outlines:** Instead of shadows, use 1px solid borders in a slightly darker shade of the background color (#E0E0CC) to define card boundaries.
- **Active State:** When an element is pressed, it shifts to a 2px Primary Green border or a subtle gray fill, providing immediate tactile feedback without needing GPU-heavy blur effects.

## Shapes

The shape language is **Soft and Functional**. 

- **Standard Radius:** A 4px (0.25rem) radius is used for input fields and small buttons.
- **Large Radius:** A 8px (0.5rem) radius is used for primary cards and large action buttons.
- **Logic:** Sharp corners are avoided to make the UI feel approachable, but high roundedness (pill shapes) is also avoided to maximize the internal space for bilingual text display.

## Components

### Buttons
- **Primary:** High-contrast Primary Green background with White text. Minimum height 56px.
- **Secondary:** Transparent background with 2px Primary Green border.
- **Critical:** Red background for "Delete" or "Emergency Health Alert."

### Cards
- **Structure:** White background, 1px light-sand border, 8px corner radius.
- **Usage:** Every milk entry, cattle profile, or payment record must be encapsulated in a card to provide a clear hit area.

### Input Fields
- **Design:** Outlined style with labels always visible above the field (never just as placeholders) to assist memory during multi-step forms.
- **Focus:** 2px Primary Green border when active.

### Chips & Status
- **Health/Payment Status:** Use high-contrast fills (e.g., Accent Green for "Paid", Warning Orange for "Pending").
- **Icons:** Use thick-stroke (2px+) system icons. Avoid thin or illustrative icons that might vanish in bright light.

### Bilingual Toggle
- A prominent toggle or dropdown in the top-right corner of the persistent header, allowing instant switching between English and Telugu.