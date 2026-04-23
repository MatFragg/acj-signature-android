# Design System Document: High-End Digital Integrity

## 1. Overview & Creative North Star

### The Creative North Star: "The Digital Notary"
This design system moves beyond the utility of a standard mobile app to evoke the gravitas of a high-end legal atelier. We are replacing the "hand-drawn" aesthetic of the initial sketches with a philosophy of **Architectural Precision**. 

The system is built on the tension between **Manrope’s** geometric authority and **Inter’s** functional clarity. By leveraging deep teals (`primary: #004354`) and a sophisticated layering of cool neutrals, we create an environment that feels fortified yet breathable. We intentionally break the standard Android grid through asymmetrical content distribution and "floating" functional groups, ensuring the app feels like a custom-tailored tool rather than a generic template.

---

## 2. Colors & Tonal Depth

### The "No-Line" Rule
To achieve a premium, editorial feel, **1px solid borders are strictly prohibited for sectioning.** Physical boundaries must be defined solely through background color shifts or tonal nesting. 
*   *Implementation:* A `surface-container-low` (`#f0f4f5`) card should sit on a `surface` (`#f6fafb`) background. The change in hex value provides the "edge," not a stroke.

### Surface Hierarchy & Nesting
Treat the UI as a series of stacked, physical layers. 
- **Base:** `surface` (#f6fafb)
- **Secondary Content Areas:** `surface-container-low` (#f0f4f5)
- **Interactive Cards:** `surface-container-lowest` (#ffffff)
- **Elevated Overlays:** `surface-bright` (#f6fafb) with 85% opacity.

### The Glass & Gradient Rule
To prevent a "flat" corporate look, use glassmorphism for floating elements (like the "New Document" action button). Use `surface-tint` at 10% opacity with a `20px` backdrop blur.
- **Signature Texture:** Primary CTAs should utilize a subtle linear gradient from `primary` (#004354) to `primary_container` (#005c72) at a 135° angle to provide "soul" and depth.

---

## 3. Typography

The typographic system is a dialogue between two typefaces: **Manrope** (Display/Headlines) for character and **Inter** (Body/Labels) for high-performance legibility.

| Level | Token | Font | Size | Weight | Role |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Display** | `display-md` | Manrope | 2.75rem | 700 | Hero moments / Welcome |
| **Headline** | `headline-sm` | Manrope | 1.5rem | 600 | Section headers |
| **Title** | `title-md` | Inter | 1.125rem | 500 | Card titles / Form labels |
| **Body** | `body-md` | Inter | 0.875rem | 400 | Primary reading text |
| **Label** | `label-md` | Inter | 0.75rem | 600 | Metadata / Small buttons |

---

## 4. Elevation & Depth

### The Layering Principle
Depth is achieved by "stacking" container tiers. In the digital signature list, the individual document cards should use `surface-container-lowest` (#ffffff) to naturally "pop" against a `surface-container-low` (#f0f4f5) background.

### Ambient Shadows
For floating elements (FABs or Modals), use "The Digital Notary" shadow:
- **Color:** `on-surface` (#181c1d) at 6% opacity.
- **Blur:** 24px.
- **Offset:** Y: 8px.
This mimics natural, diffused light rather than an artificial "drop shadow."

### The "Ghost Border" Fallback
If a border is required for accessibility (e.g., input focus), use the `outline-variant` (#bfc8cc) at **20% opacity**. Never use 100% opaque strokes.

---

## 5. Components

### Primary Action Buttons
- **Shape:** `md` (0.375rem) roundedness for a disciplined, professional look.
- **Color:** Gradient from `primary` to `primary_container`.
- **Text:** `label-md` in `on_primary` (#ffffff), all-caps with 0.05em letter spacing.

### Form Inputs
- **Style:** Understated. No "boxed" inputs. Use a `surface-container-highest` (#dfe3e4) bottom-only border (2px).
- **States:** On focus, the border transitions to `primary` (#004354) and the label (using `label-sm`) floats above the input area.
- **Error:** Use `error` (#ba1a1a) for the bottom border and helper text only.

### Signature Cards (Custom Component)
Replacing the hand-drawn list in the sketches:
- **Container:** `surface-container-lowest` (#ffffff).
- **Rounding:** `xl` (0.75rem).
- **Content:** An asymmetrical layout where the icon (left-aligned) is housed in a `secondary_container` (#d6e5eb) soft-square, and the text sits to the right with 24px of horizontal breathing room.
- **Separation:** Forbid dividers. Use 16px of vertical spacing (`surface_container_low`) to separate cards.

### Security Tooltips
- **Color:** `inverse_surface` (#2c3132).
- **Text:** `on_inverse_surface` (#edf1f2).
- **Context:** Use these to explain encryption methods when a user hovers over the "Secure" shield icon.

---

## 6. Do's and Don'ts

### Do
- **DO** use white space as a structural element. A "crowded" UI feels untrustworthy.
- **DO** use `tertiary` (#1d4520) for "Success" states (e.g., Document Signed) to provide a sophisticated alternative to "standard" bright green.
- **DO** align text to a strict baseline grid to maintain the editorial feel.

### Don't
- **DON'T** use 100% black (#000000) for text. Always use `on_surface` (#181c1d) to maintain tonal softness.
- **DON'T** use the `full` (9999px) roundedness for buttons; keep them `md` to maintain the "Architectural" brand identity.
- **DON'T** use dividers or lines to separate list items. Rely on the "No-Line" background shifts.
- **DON'T** use high-saturation teals. Stick to the muted, deep palette provided to ensure a sense of legal-grade security.