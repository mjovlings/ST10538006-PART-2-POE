# Own-voice video walkthrough

The brief requires your own voice explaining the running application, logic and flow. Use these notes to prepare your own explanation; they are not evidence that a recording has been made.

| Suggested time | Show on screen | Explain in your own words |
| --- | --- | --- |
| 0:00-0:40 | Maven project, package and class list | The project extends Part 1. Main handles prompts, Login checks credentials, Message controls messaging, MessageData holds one message, and MessageStore writes JSON. |
| 0:40-1:30 | Registration and a failed then successful login | Explain the username, password and +27 phone rules. Demonstrate that messaging is unavailable until login succeeds. |
| 1:30-2:10 | Welcome, message limit and option 2 | Explain the numeric menu, persistent while loop and Coming Soon response. |
| 2:10-3:30 | Two-message assignment scenario | Set limit 2. Send the Mike dinner message, then disregard the Keegan payment message with 0. Explain why its invalid phone prevents sending but allows discard. |
| 3:30-4:20 | Message.java and displayed details | Show random ten-digit generation, substring, zero-based loop counter, first/last word processing and uppercase hash. Explain why runtime prefixes vary. |
| 4:20-5:00 | 251-character rejection and message-limit protection | Show that invalid text is retried and only the requested number of drafts is processed. Explain why stored/disregarded drafts do not increase the sent total. |
| 5:00-5:50 | Restart, store action, actual stored_messages.json | Explain Gson serialization, appending previous data and updating status only after successful writing. Show the source attribution comment. |
| 5:50-6:50 | JUnit tests and test results | Run tests. Show the exact hash fixture, loop of hashes, 250/251 boundary tests, two-message total and JSON verification. |
| 6:50-7:30 | TestJava.yml and your real GitHub Actions run | Explain push/pull_request triggers and mvn clean verify. Show genuine commit history and the successful hosted run once available. |
| 7:30-8:00 | Quit and final total | Summarise the flow in your own words and exit with option 3. |

Aim for clarity rather than speed. Explain why each loop stops and how a draft moves from Pending to Sent, Stored or Disregarded. Demonstrate the code you actually understand and have tested.
