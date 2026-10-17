# QuickChat - PROG5121 Part 2

A Java 17 console application extending the uploaded Part 1 `Main.java`.
Open this entire folder as a Maven project. **Do not copy only Main.java**: it now works with the other classes.

## Run in NetBeans

1. Extract the ZIP first.
2. Select **File > Open Project**, then select the `QuickChat-Part2` folder containing `pom.xml`.
3. Set the project Java platform to **JDK 17 or later** and allow Maven to download dependencies.
4. Choose **Clean and Build**, then **Run Project**. If prompted, select `com.mycompany.main.Main`.
5. Choose **Test Project** to run all JUnit tests.

## Run from a terminal

With JDK 17+ and Maven installed, open a terminal in this folder:

```sh
mvn clean verify
java -jar target/quickchat-2.0.jar
```

Or run directly with Maven:

```sh
mvn compile exec:java
```

The packaged runnable application is also included as `dist/quickchat-2.0.jar`:

```sh
java -jar dist/quickchat-2.0.jar
```

The JAR contains the Gson dependency. No Maven download is needed just to run that JAR; Java is still required. The source and JUnit tests remain the primary submission.

## Try the exact assignment scenario

Register with any first and last name, username `kyl_1`, password `Ch&&sec@ke99!`, and cellphone `+27838968976`. Log in with the same username and password. Enter **2** as the message limit.

1. Choose menu **1**. Enter recipient `+27718693002` and message `Hi Mike, can you join us for dinner tonight?`. Choose action **1** to send.
2. Enter recipient `08575975889` and message `Hi Keegan, did you receive the payment?`. The recipient is rejected for sending/storage. Choose action **2**, then **0** to disregard it, as the assignment specifies.
3. The sent total is **1**. Choose menu **1** again to check the session limit. Choose **2** for `Coming Soon.` and **3** to quit.

To test storage, restart, set the limit to 1, enter a valid recipient/message and choose action **3**. Inspect `stored_messages.json` in the directory from which the app runs. It is created on first storage; it is not a prefilled submission file. Existing stored messages are preserved.

## Project contents

| File | Responsibility |
| --- | --- |
| `src/main/java/com/mycompany/main/Main.java` | Registration prompts, login, numeric menu, while loop, bounded for loop and message action prompts |
| `Login.java` in the same package | Reusable validation and authenticated state |
| `Message.java` | Required Part 2 methods, random IDs, hashes, actions, sent total and sent-message report |
| `MessageData.java` | One message's ID, number, recipient, text, hash and status |
| `MessageStore.java` | Attributed JSON storage using Gson and safe file replacement |
| `src/test/java/com/mycompany/main/` | JUnit tests using the assignment data, boundaries and console scenarios |
| `.github/workflows/TestJava.yml` | Runs all tests on pushes, pull requests and manual workflow dispatch |
| `docs/RUBRIC_CHECKLIST.md` | Every Part 2 mark category mapped to the implementation |
| `docs/DESIGN_DECISIONS.md` | How contradictory wording in the brief was resolved |
| `docs/VIDEO_WALKTHROUGH.md` | Demonstration plan and explanation prompts for your own recording |
| `docs/REFERENCES.md` | Source attribution and AI assistance disclosure |
| `docs/TEST_RESULTS.md` | Actual local verification and remaining submission requirements |

## GitHub and final submission

Use your existing Part 1 GitHub repository. The ZIP is a project handover, not evidence of a GitHub push.

1. Create a feature branch, for example `part2-messaging`, from your existing tested work.
2. Integrate the project files, preserving your repository history. Ensure `pom.xml` and `.github` are at the repository root. Enable hidden-file display if your file manager hides `.github`.
3. Keep **at least six genuine commits for Part 2** as required on page 5. Review, understand and test your work as you integrate it; do not invent or backdate development history. Useful stages to review are login integration, message validation/IDs, menu loops/actions, JSON storage, tests and CI/documentation.
4. Push the feature branch and check GitHub **Actions > QuickChat Java tests**. A local passing test run does not establish that the hosted workflow ran successfully.
5. Review the changes, merge the tested branch into your main branch, and submit the correct repository link on Arc.
6. Record the required demonstration **using your own voice**, upload it as an unlisted video and submit its link. See the walkthrough notes.

This project simulates sending by recording a sent state in memory. It does not deliver SMS or network messages; no external delivery service is required in Part 2. Registration and sent history last for the current run. Only messages explicitly stored for later persist as JSON.
