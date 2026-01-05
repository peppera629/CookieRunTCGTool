Started as a simple deck builder for the trading card game CookieRun: Braverse, ended up being a general-purpose card collection manager too.
- Allows for construction of decks and outputting deck overview images
- Card searching and discovery is possible through advanced filtering (such as by specific conditions like skill type, keywords, etc.)
- Manage your card collection, right down to the language and variant owned (and there's also an option to build deck from collection, too)

Note: Card images are not included in the source code, nor are any preset deck configurations.

=== Version 0.10.0 : 2025-12-03
- New collection summary view: see your collection status by pack and rarity. Enter collection mode and press "Collection Summary" to view. Currently supports 2 modes: by rarity and by rarity (secret rare)
- Traditional Chinese secret rare card images added

=== Version 0.9.1 : 2025-12-01
- Added language fallbacks for region- or language-exclusive cards (for English: EN > KR > ZH_TW, for Traditional Chinese: ZH_TW > EN > KR)
- Save reminders will now pop up when closing/switching to new deck with unsaved changes

=== Version 0.9.0 : 2025-11-29
- Collection now supports keeping track of "variants" (secret rares, promo versions, etc.). Enter collection mode and hold the corresponding number key to change currently-set variant and illustration (only shows English cards for now)
- Added buttons to toggle filter/card preview panels
- Added Booster Pack and Starter Deck quick toggles

=== Version 0.8.1 : 2025-11-20
- Added FLIP card types and filtering
- Allowed hiding of unowned cards
- Changed deck detail layout
- Added flip type display
- Added basic dialog boxes for saving deck & saving deck image

=== Version 0.8.0 : 2025-11-19
- New "Collection" mode. You can keep track of the cards you own using a dedicated menu. (Secret rare and variant tracking will be available in the future)
- "Build from Collection" mode: number of copies you own for each card will be displayed, and you'll get a warning when cards you don't own or don't have enough of are added to the deck
- Added tooltips for invalid decks (check by hovering over red UI text) and certain UI elements
- Minor UI adjustments
- Card names and card attributes are modularized for easier maintenance
- Fixed some card attribute errors

=== Version 0.7.5 : 2025-11-13
- Implemented "enlarge translation text" feature
- Fixed bugs related to translation text

=== Version 0.7.0 : 2025-11-11
- Added filtering by HP
- Added display and icons for restricted and banned cards
- Added translation text toggle (translation text size toggle WIP)

=== Version 0.6.0 : 2025-11-05
- Added translations (currently only available for zh-TW on English-only BS8 and P cards)
- Merged "load" and "select file" functions
- Added option to change card hover preview size in settings
- Minor UI tweaks

=== Version 0.5.3 : 2025-11-01
- Added deck folder fallbacks
- Minor UI tweaks

=== Version 0.5.2 : 2025-10-29
- UI language and card language can now be configured separately
- EXTRA cards are now counted separately from total cards

=== Version 0.5.1 : 2025-10-29
- More UI changes and fixes
- EXTRA card count is now displayed

=== Version 0.5.0 : 2025-10-28
- Multi-language support added (English & Traditional Chinese)
- Adaptive card grid layout (column count changes as the window is resized)
- Deck overview numbers now have anti-aliasing
- UI text changes and minor layout adjustments
- EXTRA filtering logic fixed

=== Version 0.4.0 : 2025-10-26 [Forked]
- UI overhaul
- BS8 cards and info added
- Filtering by EXTRA added

=== Version 0.3.0 : 2024-02-20
- refactor search panel.
- add all cards into database from BS2, ST4, ST5 and PR (Korean)
- Fix some minor problem

=== Version 0.2.5 : 2024-02-12
- cards in deck can be sort by different priority
- optimal the card image load strategy, which make display faster.
- Fix level 2 card count and total cookie card count display error problem
- Fix some minor problem

=== Version 0.2.2 : 2024-02-07
- deck detail can be check on another windows and print as image now

=== Version 0.1.2 : 2024-02-06
- add all cards into database from BS1, ST1, ST2 and ST3

=== Version 0.1.1 : 2024-02-05
- Cards data contain cookie level now
- Show deck summary
- Output a readable text file while saving deck
- Restore last state and last saved deck
- Fix some minor problem

=== Version 0.1.0 : 2024-02-03
first check in with following function
- List card list.
- Left click to add card into deck.
- Right click to remove card from deck.
- Show card detial while mouse move on.
- Search cards in card list by card color and type.
- Save and load deck from disk.
