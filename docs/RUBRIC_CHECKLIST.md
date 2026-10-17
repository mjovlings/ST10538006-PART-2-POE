# Part 2 rubric coverage (100 marks)

Source: The Independent Institute of Education (2026), PROG5121/p/w PoE, pages 11-16 and 22-24. This is an evidence map, not a predicted or awarded mark.

| Criterion | Marks | Implementation and verification |
| --- | ---: | --- |
| Welcome and numeric menu | 5 | `Main.runChat`: exact welcome, menu 1-3; `MainTest.welcomeMenuComingSoonAndQuit` |
| While loop | 5 | Menu continues until option 3; invalid input is retried; quit and repeated menu tests |
| For loop | 10 | `Main.runChat`: bounded for loop enters exactly the requested number; tests cover two, zero and no extra entries |
| Message details entered | 10 | Recipient and text prompts; `MessageData`; validation retries and exact scenario tests |
| Message number from loop | 5 | Zero-based `messageNumber` passed from the for loop; numbering checked in unit and console tests |
| Message ID and string manipulation | 10 | Ten random decimal digits built using a loop and substring; session/stored ID collision checks; 1,000 generated IDs tested |
| Message hash | 10 | First two ID digits, colon, loop number, colon, first/last words, uppercase; exact `00:0:HITONIGHT` fixture and additional hashes in a loop |
| Details displayed in order | 10 | `MessageData.details`: ID, hash, recipient, message after sending; exact output assertion |
| JSON file and attribution | 10 | `Message.storeMessage` and `MessageStore.append`; Gson attribution in code; tests read stored data and check appended records, escaping and write failure |
| Unit tests | 10 | `LoginTest`, `MessageTest`, `MainTest`; required assertEquals, assertTrue and assertFalse plus error-path tests |
| Automated unit tests | 5 | `.github/workflows/TestJava.yml` runs `mvn clean verify`; all `*Test` classes discovered by Surefire. Hosted run remains to be performed in your GitHub repository |
| Coding standards | 10 | Named classes/constants, short methods, shared phone validation, explicit datatypes, comments, isolated tests and no GUI |
| **Total** | **100** | GitHub, commit and video evidence must still be completed by the student |

## Required methods

All are in `Message.java`. Arguments make the methods directly testable; the brief does not prescribe argument lists.

- `boolean checkMessageID(String id)`
- `String checkRecipientCell(String recipient)`
- `String createMessageHash(String id, int number, String text)`
- `String SentMessage(MessageData message, int choice)`
- `String printMessages()`
- `int returnTotalMessagess()`
- `void storeMessage(MessageData message)`

`SentMessage` deliberately retains the brief's uppercase S, and `returnTotalMessagess` retains its spelling. Console interaction occurs in `Main`; the chosen action is passed into `SentMessage` so tests do not need to type into a live terminal. `printMessages()` returns all sent messages, while menu option 2 remains `Coming Soon.` as explicitly required.

## Submission items that code cannot establish

- [ ] Your Part 2 branch has at least six genuine commits.
- [ ] The complete project has been pushed to your GitHub repository.
- [ ] GitHub Actions has run and passed on the submitted commit.
- [ ] The repository link has been submitted on Arc.
- [ ] The required application demonstration has been recorded in your own voice.
- [ ] The unlisted video link has been submitted.
- [ ] You can explain and demonstrate the code and have applied your institution's AI-disclosure requirements.

The brief states a 5% penalty for not using GitHub correctly. A prepared workflow file alone cannot earn evidence-based credit for a successful hosted run.
