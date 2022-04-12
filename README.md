# GitLab compatible JSON reporter for ktlint
This is a custom reporter for [ktlint](https://github.com/pinterest/ktlint). 
It provides output in JSON format that can be parsed by GitLab automatically.

## Why? 
With the correct artifact format, the GitLab CI can show you what code style violations are present.
This helps you with inspecting Merge Requests before merging or keeping your code clean in general.

## How do I use the reporter?
1. Download the `ktlint-json-reporter.jar` from this repository.
2. Copy the JAR file to your Kotlin project.
3. To use the custom reporter, run `ktlint --reporter=gitlab,artifact=/path/to/ktlint-json-reporter.jar`

## Export artifacts in GitLab CI
