# Recovered scene labels from UnityFS data

Date: 2026-10-23

## Method
The supplied APK's `assets/bin/Data/data.unity3d` is a UnityFS bundle (Unity 2022.3.62f3). Its block table and serialized-file entries were parsed, the 758 LZ4-compressed blocks were decompressed, and the embedded serialized files were separated for string inspection. This is a static asset inspection only; the APK itself was not modified.

## Scene-level findings
Readable strings from embedded scene files identify the following UI objects and text:

### `level0` — privacy/consent screen
- `Main Camera`, `Canvas`, `Panel`, `TitleBox`, `Button`, `Label`, `EventSystem`
- `TermsOfService`, `PrivacyPolicy`, `TermsConditions`, `Accepted`, `StartPlaying`
- Text: `Terms of Service & Privacy Policy`, `Start Playing`, `Hi there!`
- Consent message: `To continue playing you need to confirm that you agree Plus9Games Terms of Service and have read our Privacy Policy.`

### `level1` — menus / rewards / offers (labels found)
- `Back`, `Close`, `Title`, `Label`, `Image`, `Buttons`, `Windows`, `UIWindows`
- `PiggyBankCanvas`, `CoinsPopup`, `LevelReward`, `SpecialReward`, `GameOfferCanvas_OK`
- `CoinIcon`, `CoinsIcon`, `CoinSprite`, `RewardNumber`, `PriceNumber`, `OfferIcon`

### `level3` / `level4` — gameplay/tutorial/shop UI (labels found)
- `Menu`, `Keyboard`, `MainKeyboard_NEW`, `BackspaceButton`, `KeyButton`, `KeyText_New`
- `Letters`, `LetterText`, `LevelNumber`, `Hints`, `Hint`, `Hint_Erase`, `Hint_Free`, `Hint_Blast`
- `ShopItemsRoot`, `ShopItem_1`, `ShopItem_3`, `ShopItem_4`, `HintCoinsNumber`, `FreeUseNumber`
- Tutorial names include `STEP_03`, `Tutorial_09_RestartLevel_GIRL`, `Tutorial_06_Hint_VideoAdFree`

These names are useful for identifying likely UI targets but are not a complete or authoritative hierarchy: string extraction cannot prove which elements are active, visible, clickable, or in what focus order.

## What this changes
The Unity data is not simply an opaque blob: its UnityFS container can be decompressed and its embedded scene files can be enumerated. This is a useful recovery step. However, the supplied APK still contains no `.so` native libraries and no editable `.unity`/ `.prefab`/ `.cs` project files. Full scene reconstruction and a reliable native TalkBack implementation are not yet achieved.

## Next work
- Parse serialized scene object tables and relationships beyond strings, where possible.
- Identify Android activity and manifest details from the binary manifest.
- Determine whether the game’s matching native IL2CPP libraries can be found inside the companion ZIP or another archive entry.
- Only after reconstructing a rebuildable project or a verified patch path should an APK be built and signed.
