# Accessibility Implementation Matrix

This matrix converts the recovered Unity scene labels into a testable implementation plan. Extracted names are clues, not proof of visibility or clickability; every target must be verified in the running app.

## Screen and control matrix

| Screen / flow | Recovered targets | Required accessible name / behavior | Verification |
|---|---|---|---|
| Consent / first launch (`level0`) | `TermsOfService`, `PrivacyPolicy`, `TermsConditions`, `Accepted`, `StartPlaying` | Read the welcome and consent explanation; expose the acceptance state; announce the Start Playing action; do not activate it merely because focus moved | With TalkBack on, reach each control in order, hear label + role + checked state, activate links and start button |
| Menus and overlays (`level1`) | `Back`, `Close`, `Title`, `Buttons`, `Windows`, `CoinsPopup`, `LevelReward`, `SpecialReward` | Give each icon button a meaningful label; announce overlay title on open; confine focus to modal overlays and return focus to opener on close | Open/close each popup; verify no focus loss and no background controls reached while modal |
| Gameplay keyboard (`level3` / `level4`) | `Keyboard`, `MainKeyboard_NEW`, `BackspaceButton`, `KeyButton`, `KeyText_New`, `Letters`, `LetterText` | Speak each key's character; announce typed letters and deletion; provide a non-gesture route to every key; preserve a predictable focus order | Type a word using TalkBack navigation; delete a letter; verify Persian/Arabic characters and layout direction |
| Level / hints / shop | `LevelNumber`, `Hints`, `Hint_Erase`, `Hint_Free`, `Hint_Blast`, `ShopItemsRoot`, `HintCoinsNumber`, `FreeUseNumber` | Announce current level, hint action, remaining uses and coin cost before activation; confirm purchase/spend actions | Move focus through hints/shop; check costs and remaining counts are spoken before committing |
| Tutorials | `STEP_03`, `Tutorial_09_RestartLevel_GIRL`, `Tutorial_06_Hint_VideoAdFree` | Announce each tutorial instruction when it changes; offer a labeled next/skip/replay action; do not rely on character animation alone | Complete tutorial using audio and TalkBack only; repeat and skip each instruction |
| Dynamic rewards and ads | `RewardNumber`, `PriceNumber`, `OfferIcon` and popups | Announce reward changes once, label reward and price units, avoid repeatedly interrupting speech; clearly identify ad-related controls | Trigger rewards and offers; confirm speech does not loop and buttons are distinguishable |

## Implementation requirements

1. **Use real accessibility semantics.** Text-to-Speech announcements alone do not make Unity controls part of Android TalkBack's accessibility node tree. The implementation must expose controls through a tested native accessibility bridge or a supported Unity accessibility API.
2. **Do not infer labels solely from GameObject names.** Prefer visible text, then explicit localization/accessibility metadata, then a curated fallback dictionary.
3. **Keep state and actions separate.** Focusing a control must never trigger purchases, consume hints, accept terms, or start a level.
4. **Dynamic UI:** after a popup opens, a letter is entered/deleted, a hint is consumed, or a level changes, announce the meaningful state change and preserve focus.
5. **Persian support:** verify Android TTS language availability, RTL ordering, Persian letter names, numerals, and punctuation on-device. English fallback should remain usable.
6. **Keyboard and touch:** provide a route through all essential controls that does not depend on precise swipes or visual targeting. Any custom gesture must be discoverable and must not conflict with TalkBack gestures.
7. **Error recovery:** if native accessibility integration is unavailable, do not present the TTS-only prototype as a complete screen-reader solution.

## Exit criteria before publishing an APK

- Clean build and signing; no missing native libraries.
- Install and launch on a real Android device.
- TalkBack can identify, focus, and activate every essential control.
- No invisible/duplicate focus stops in normal flows.
- Consent, navigation, gameplay, shop, rewards, and tutorials are usable without sight.
- Persian speech and right-to-left text are verified on device.
- Regression pass after each scene/overlay transition.
- Publish the APK only after the exact artifact's SHA-256 and install/test result are recorded.

## Current status

Static asset recovery has identified candidate controls, but this matrix is a plan, not a claim that these controls have been fixed or tested. The supplied files still do not provide a complete editable Unity project, and the inspected APK contains no `.so` native libraries.
