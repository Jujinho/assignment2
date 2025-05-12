# University Equipment Lending System

## Overview
The University Equipment Lending System is a comprehensive application for managing equipment lending in a university setting. The system supports multiple user roles (visitor, student, academic staff, professional staff, and administrator) with different permissions and functionalities.

## Features
- **User Management**: Create, update, and delete users with different roles (student, academic staff, professional staff, administrator)
- **Equipment Management**: Add, update, and delete equipment with images
- **Course Management**: Create courses, assign academic staff, and enroll students
- **Lending Management**: Borrow and return equipment, approve and reject lending requests
- **Statistics**: View lending statistics for academic staff and the entire system

## User Roles
- **Visitor**: View available equipment with filtering options
- **Student**: View and update personal information, borrow and return equipment (with approval), view lending history
- **Academic Staff**: Manage personal information, approve student borrowing requests, borrow equipment, view statistics
- **Professional Staff**: Manage personal information, borrow equipment, view lending history
- **Administrator**: Full access to all functionalities, including CRUD operations on all entities

## Technical Details
- **Programming Language**: Java
- **GUI Framework**: JavaFX
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Testing Framework**: JUnit 5

## Project Structure
- `src/main/java/org/example/model`: Model classes (User, Student, AcademicStaff, ProfessionalStaff, Administrator, Equipment, Course, LendingRecord)
- `src/main/java/org/example/db`: Database connection and repository classes
- `src/main/java/org/example/service`: Service classes for business logic
- `src/main/java/org/example/controller`: JavaFX controllers
- `src/main/resources/fxml`: JavaFX FXML files
- `src/test/java/org/example`: Unit tests

## Setup Instructions
1. Clone the repository
2. Update the database connection details in `src/main/java/org/example/db/DatabaseConnection.java`
3. Build the project using Maven: `mvn clean package`
4. Run the application: `java -jar target/Assignmen-2-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Database Setup
The application requires a PostgreSQL database. The database schema is automatically created when the application starts. You need to provide the following details in the `DatabaseConnection` class:
- Database URL
- Username
- Password

### Predefined Users
The system comes with predefined users since registration is not allowed. The SQL script to create these users is located at:
- `src/main/resources/predefined_users.sql`

Documentation for the SQL script is available at:
- `src/main/resources/README_SQL_SCRIPT.md`

To use the script, connect to your database and execute the SQL file. The script creates various user types (administrator, academic staff, professional staff, students) with predefined credentials.

## Testing
The application includes unit tests for the model classes. To run the tests, use the following command:
```
mvn test
```

## Building the JAR File
To build the JAR file, use the following command:
```
mvn clean package
```
This will create a JAR file with dependencies in the `target` directory.

## Future Improvements
- Implement the remaining user views (student, academic staff, professional staff, administrator)
- Add more unit tests for all classes
- Implement password hashing for security
- Add more statistics and reporting features
- Implement email notifications for lending requests and overdue equipment

## Contributors
- Group 9

## License
This project is licensed under the MIT License - see the LICENSE file for details.
# assignment2
