# Task Programming Challenge - Java Application

This repository contains the solution for the Task Programming Challenge, which involves fixing and extending a Java application. The application is built using JDK 11, Spring Boot 2, and Maven.

## Task 1: Dependency Injection

The project initially fails to start due to a problem with dependency injection. This issue has been identified and fixed, allowing the application to start correctly.

## Task 2: Extend the Application   

The application has been extended to include a new task type, task progress monitoring, and task cancellation mechanism.

### New Task Type: Counter

A new task type, called "Counter," has been implemented. It takes two input parameters, `x` and `y`, both of type `integer`. When the task is executed, a counter starts in the background and progresses by one every second. The counting starts from `x` and continues until it reaches `y`, at which point the task is considered successfully finished.

### Task Progress Monitoring

The progress of the task execution is now exposed via the API. This allows web clients to monitor the progress of a running task. The API endpoints provide real-time information about the current count and the total count of the task.

### Task Cancellation

A task cancellation mechanism has been implemented, allowing users to cancel a task that is currently being executed. When a task is canceled, the execution stops, and the task is marked as canceled.

## Task 3: Periodic Task Cleanup

The API allows users to create tasks, but they are not required to execute those tasks. To prevent clutter and unnecessary data, tasks that have not been executed for an extended period, such as a week, are periodically cleaned up (deleted).

## Running the Application

To run the application, follow the steps below:

1. Ensure that JDK 11 is installed on your system.

2. Clone this repository to your local machine.

3. Open the project in your preferred Java IDE (e.g., IntelliJ or Eclipse).

4. Build the project using Maven to download the dependencies:

   ```shell
   mvn clean install
   ```

5. Run the main class of the Spring Boot application, which is annotated with `@SpringBootApplication` and contains a `main` method.

6. The Spring Boot service will start running, and you will see log messages indicating that the application has started successfully.

7. Once the application is running, you can test the API using tools like Postman or cURL. The API endpoints should be accessible at `http://localhost:8080` or a different port if configured differently.

## Running Test Cases

The project includes test cases implemented using JUnit 5. To run the test cases, follow these steps:

   ```shell
   mvn clean test
   ```
Ensure that all the test cases pass successfully to verify the correctness of the implementation.

## Conclusion

Feel free to reach out if you have any questions or need further assistance.