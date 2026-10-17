# Local verification results

Verified on 16 October 2026 using OpenJDK 17 and Apache Maven 3.9.9.

| Test class | Tests | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| LoginTest | 5 | 0 | 0 | 0 |
| MessageTest | 21 | 0 | 0 | 0 |
| MainTest | 9 | 0 | 0 | 0 |
| **Total** | **35** | **0** | **0** | **0** |

Result: **BUILD SUCCESS** from Maven `clean verify`. The build used environment-specific proxy settings to reach Maven Central; these settings are not part of the project. The normal project command is `mvn clean verify`.

The runnable shaded JAR was separately executed with scripted console input covering registration/login, Coming Soon, send, disregard, store, session-limit enforcement and quit. Its exit status was 0. The JSON file was parsed independently with Python: it contained exactly one stored message with status Stored. The final sent count was 1. See CONSOLE_RUN.txt for actual console output and junit-results/ for Maven's per-class summaries.

The build reports harmless duplicate manifest/module metadata warnings from the bundled dependencies. The executable entry point was verified by running the JAR. This application is a console simulation; no real network message delivery was tested or claimed.

**Not yet verified:** opening the project in the student's own NetBeans installation, a hosted GitHub Actions run, the student's required six-commit history, Arc submission or the own-voice presentation. The workflow is prepared, but those external submission steps are not represented as completed.
