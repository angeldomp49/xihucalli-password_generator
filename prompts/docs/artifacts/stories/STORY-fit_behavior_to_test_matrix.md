
# Fit Behavior to Test Matrix #

Based on the following information please create a detailed specification which will serve
as input for Claude Sonnet 4 in Agent mode for Copilot to implement the feature.

## Structure ##

Add the following section first:

```markdown
Status: draft
Owner: @angeldomp49
Source Model: Claude Opus 4.1 (Ask Mode)
Last Sync: %%timestamp%%

```

### Project Information ###

- **Project Name**: Password Generator

## Feature information ##

### Role ###

As a senior backend developer, your task is to fix the current source code in order to accomplish the test matrix expected behavior.

## Context ##

Please read the documentation of the project and follow to the libraries links to make sure you
understand how the project works and why are used these libraries.

## Dependencies ##

This project doesn't use the ioc_container library as dependency.

## Task ##

You have to read the project documentation at the `docs` folder to understand the expected behavior of the project.

You have to read the test fixtures at the `src/test/resources` folder to understand the expected behavior of the project.

You have to read all the external documentations to understand how the libraries should be used.

You have to run the tests and read the reports to understand the current status of the tests.

If the tests fails, you have to fix the test and/or source code in order to make the tests pass.

## Conditions to Stop ##

You will stop when you run successfully all the tests for all the components.


