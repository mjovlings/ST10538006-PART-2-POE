# Design decisions and changes from Part 1

## Resolve the brief using its supplied test data

1. **Phone length:** pages 12 and 14 say ten characters, but the required valid example `+27718693002` contains twelve characters including `+27`. The code preserves the supplied Part 1 South African rule: `+27` followed by nine digits. The national number is nine digits without its local leading zero, or ten with it. The number is stored as a String. Other country codes are outside this project's stated South African registration scope.
2. **Random ID versus exact hash:** the runtime generates ten random digits, including possible leading zeroes, and checks uniqueness against generated and stored IDs. `0012345678` appears only in tests to make the required `00:0:HITONIGHT` assertion repeatable. Real runtime hashes use the real random prefix. IDs are not derived from the loop counter because page 12 requires randomness; the rubric's loop and substring requirements are met through digit generation, substring and the hash's message counter.
3. **Punctuation:** the required expected hash omits the final question mark from `tonight?`. Punctuation is removed for hash calculation only. Stored/displayed message text is unchanged. A one-word message uses that word as both its first and last word.
4. **250 characters:** exactly 250 are accepted, matching the stated maximum. Page 15's detailed unit-test responses take precedence over page 12's inconsistent “less than 250” wording. The validator returns `Message ready to send.` or the exact excess count. Successful sending returns `Message successfully sent.`
5. **Invalid second recipient and discard:** the supplied `08575975889` fails phone validation. A draft with it may still be disregarded, matching the prescribed second test case. Send and store both require correction to a valid number first.
6. **Numbers and counts:** message numbers start at 0, as in the required hash. Every completed draft (sent, stored or disregarded) consumes one entry in the user's session limit. Only sent messages increase `returnTotalMessagess()`. Retrying invalid text or cancelling deletion does not consume another entry. Re-selecting menu 1 cannot reset the limit.
7. **Action confirmation:** disregard returns the prescribed prompt and requires 0 to delete. Choosing 1 cancels deletion and returns to the action menu for the same draft.
8. **Part 2 scope:** menu option 2 deliberately prints `Coming Soon.`. Searching, deleting by hash and displaying stored-message reports are Part 3 functionality and are not added to the Part 2 menu. The required `printMessages()` method is implemented and tested separately.
9. **Rubric's “Task Class”:** the messaging brief requires a `Message` class. The CI rubric appears to retain “Task Class” wording from another task. The workflow runs all actual test classes, including `MessageTest`, rather than introducing an unrelated Task class.

## Improvements to the uploaded Main.java

- Kept package `com.mycompany.main`, Scanner input and registration-before-login flow.
- Moved credential validation and authentication into `Login` to avoid duplicated phone rules and make testing easier.
- Changed username length from exactly five to no more than five characters, while requiring an underscore.
- Removed the additional lowercase-password requirement because it is not in the brief.
- Added first and last names for the required personalised greeting.
- Added login retries and an exit option before messaging, plus an authentication check inside message operations.
- Replaced a one-off program ending after login with a persistent console menu and a bounded message-entry loop.
- Separated console prompts, message data and file persistence so unit tests can exercise them independently.

## Storage and limitations

Stored JSON is an array of objects with `messageId`, `messageNumber`, `recipient`, `message`, `messageHash` and `status`. Gson handles escaping and serialization (Google, n.d.). Java file operations write a temporary file and replace the destination only after writing succeeds (Oracle, n.d.b). Existing malformed JSON is not silently overwritten. A storage failure leaves the draft pending, allowing another action.

IDs are unique within the current session and against IDs in the current JSON file. This is a single-process student console application; concurrent writers and global cross-device uniqueness are not claimed. Message hashes follow the prescribed formula and can collide; they are not cryptographic hashes. Length is measured with Java String.length(), which counts UTF-16 code units. Password input is visible in the console, as in the supplied Scanner-based exercise, and credentials are not persisted to disk.

References are listed in REFERENCES.md.
