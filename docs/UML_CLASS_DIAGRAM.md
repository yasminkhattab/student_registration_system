# UML Class Diagram

The diagrams below are written in [Mermaid](https://mermaid.js.org/) and mirror the
actual implementation in `src/main/java/com/studentregistration`. Multiplicities and
relationships use Mermaid class-diagram syntax.

## 1. Domain model

```mermaid
classDiagram
    direction LR

    class Person {
        <<abstract>>
        -String id
        -String name
        -String email
        -String phone
        #Person()
        #Person(String id, String name, String email, String phone)
        +String getRole()*
        +String getContactSummary() String
        +String getId() String
        +void setId(String id)
        +String getName() String
        +void setName(String name)
        +String getEmail() String
        +void setEmail(String email)
        +String getPhone() String
        +void setPhone(String phone)
        +String toString()
        +boolean equals(Object other)
        +int hashCode()
    }

    class Student {
        -String major
        -int level
        +Student()
        +Student(String id, String name, String email, String phone)
        +Student(String id, String name, String email, String phone, String major, int level)
        +String getRole() String
        +String getMajor() String
        +void setMajor(String major)
        +int getLevel() int
        +void setLevel(int level)
        +String getContactSummary() String
    }

    class Administrator {
        -String department
        +Administrator()
        +Administrator(String id, String name, String email, String phone, String department)
        +String getRole() String
        +String getDepartment() String
        +void setDepartment(String department)
        +String getContactSummary() String
    }

    class Registrable {
        <<interface>>
        +void enroll(Student student)
        +void drop(Student student)
        +boolean isEnrolled(Student student)
        +boolean isFull() boolean
        +int getAvailableSeats() int
        +int getEnrollmentCount() int
        +List~Student~ getEnrolledStudents()
    }

    class Course {
        -String courseId
        -String title
        -int creditHours
        -int capacity
        -String instructor
        -String schedule
        -List~Student~ enrolledStudents
        +Course()
        +Course(String id, String title, int credits, int capacity, String instructor)
        +Course(String id, String title, int credits, int capacity, String instructor, String schedule)
        +void enroll(Student student)
        +void drop(Student student)
        +boolean isEnrolled(Student student)
        +boolean isFull() boolean
        +int getAvailableSeats() int
        +int getEnrollmentCount() int
        +List~Student~ getEnrolledStudents()
        +void restoreEnrollment(Student student)
        +String getCourseId() String
        +void setCourseId(String courseId)
        +String getTitle() String
        +void setTitle(String title)
        +int getCreditHours() int
        +void setCreditHours(int creditHours)
        +int getCapacity() int
        +void setCapacity(int capacity)
        +String getInstructor() String
        +void setInstructor(String instructor)
        +String getSchedule() String
        +void setSchedule(String schedule)
    }

    class RegistrationStatus {
        <<enumeration>>
        ACTIVE
        DROPPED
        -String displayName
        +String getDisplayName() String
    }

    class Registration {
        -String registrationId
        -String studentId
        -String courseId
        -LocalDateTime registeredAt
        -RegistrationStatus status
        +Registration()
        +Registration(String id, String studentId, String courseId, LocalDateTime registeredAt, RegistrationStatus status)
        +Registration create(String studentId, String courseId)$
        +void markDropped()
        +void markActive()
        +boolean isActive() boolean
        +String getRegistrationId() String
        +String getStudentId() String
        +String getCourseId() String
        +LocalDateTime getRegisteredAt() LocalDateTime
        +RegistrationStatus getStatus() RegistrationStatus
    }

    Person <|-- Student : extends
    Person <|-- Administrator : extends
    Registrable <|.. Course : implements
    Course o-- "0..*" Student : enrolledStudents
    Registration --> RegistrationStatus : status
    Registration ..> Student : studentId
    Registration ..> Course : courseId
```

**Legend**

- `<|--` inheritance; `..|>` interface realisation; `o--` aggregation.
- `Person` is abstract and cannot be instantiated.
- `Course` **aggregates** students: the same `Student` object is shown in several
  courses and is not destroyed with the course.
- `Registration` refers to students and courses by identifier, which is exactly how
  the records are stored on disk.

## 2. Services, repositories and GUI

```mermaid
classDiagram
    direction TB

    class AppContext {
        -StudentService studentService
        -CourseService courseService
        -RegistrationService registrationService
        -Person currentUser
        -SceneNavigator navigator
        +AppContext bootstrap()$
        +StudentService getStudentService()
        +CourseService getCourseService()
        +RegistrationService getRegistrationService()
        +Student getCurrentStudent()
        +Administrator getCurrentAdministrator()
        +void setCurrentUser(Person person)
        +void logout()
        +void setNavigator(SceneNavigator navigator)
        +SceneNavigator getNavigator()
    }

    class StudentService {
        -StudentRepository repository
        -List~Student~ students
        +StudentService(StudentRepository repository)
        +List~Student~ getAllStudents()
        +int count() int
        +Optional~Student~ findOptional(String id)
        +Student findById(String id)
        +Student addStudent(String id, String name, String email, String phone, String major, int level)
        +Student addStudent(Student student)
        +void updateStudent(String id, String name, String email, String phone, String major, int level)
        +void deleteStudent(String id)
        +List~Student~ search(String keyword)
    }

    class CourseService {
        -CourseRepository repository
        -List~Course~ courses
        +CourseService(CourseRepository repository)
        +List~Course~ getAllCourses()
        +int count() int
        +int getTotalCapacity() int
        +int getTotalEnrollment() int
        +Optional~Course~ findOptional(String id)
        +Course findById(String id)
        +Course addCourse(String id, String title, int credits, int capacity, String instructor, String schedule)
        +void updateCourse(String id, String title, int credits, int capacity, String instructor, String schedule)
        +void deleteCourse(String id)
        +List~Course~ search(String keyword)
    }

    class RegistrationService {
        -StudentService studentService
        -CourseService courseService
        -RegistrationRepository repository
        -List~Registration~ registrations
        +RegistrationService(StudentService s, CourseService c, RegistrationRepository r)
        +Registration enroll(String studentId, String courseId)
        +void drop(String studentId, String courseId)
        +void deleteStudent(String studentId)
        +void deleteCourse(String courseId)
        +List~Registration~ getActiveRegistrationsForStudent(String studentId)
        +List~Course~ getActiveCoursesForStudent(String studentId)
        +List~Student~ getActiveStudentsForCourse(String courseId)
        +int countActiveRegistrations() int
    }

    class StudentRepository {
        +StudentRepository(FileStorageManager storage)
        +List~Student~ findAll()
        +void saveAll(List~Student~ students)
        ~String toCsvLine(Student student)
    }

    class CourseRepository {
        +CourseRepository(FileStorageManager storage)
        +List~Course~ findAll()
        +void saveAll(List~Course~ courses)
        ~String toCsvLine(Course course)
    }

    class RegistrationRepository {
        +RegistrationRepository(FileStorageManager storage)
        +List~Registration~ findAll()
        +void saveAll(List~Registration~ registrations)
        ~String toCsvLine(Registration registration)
    }

    class FileStorageManager {
        -Path dataDirectory
        +FileStorageManager(Path dataDirectory)
        +Path resolve(String fileName)
        +List~String~ readLines(String fileName)
        +void writeLines(String fileName, List~String~ lines)
        +Path getDataDirectory()
    }

    class SceneNavigator {
        -Stage stage
        -AppContext context
        +void showLogin()
        +void showAdminDashboard()
        +void showStudentManagement()
        +void showCourseManagement()
        +void showStudentDashboard()
        +void showCourseCatalog()
        +void showMyCourses()
    }

    class LoginController {
        +void handleLogin()
        +void handleExit()
    }

    class AdminDashboardController {
        +void handleManageStudents()
        +void handleManageCourses()
        +void handleRefresh()
        +void handleLogout()
    }

    class StudentManagementController {
        +void handleAdd()
        +void handleUpdate()
        +void handleDelete()
        +void handleSearch()
    }

    class CourseManagementController {
        +void handleAdd()
        +void handleUpdate()
        +void handleDelete()
        +void handleDropEnrollment()
    }

    class StudentDashboardController {
        +void handleBrowseCourses()
        +void handleMyCourses()
        +void handleLogout()
    }

    class CourseCatalogController {
        +void handleEnroll()
        +void handleSearch()
    }

    class MyCoursesController {
        +void handleDrop()
    }

    AppContext *-- StudentService
    AppContext *-- CourseService
    AppContext *-- RegistrationService
    AppContext --> SceneNavigator

    StudentService --> StudentRepository
    CourseService --> CourseRepository
    RegistrationService --> RegistrationRepository
    RegistrationService --> StudentService
    RegistrationService --> CourseService

    StudentRepository --> FileStorageManager
    CourseRepository --> FileStorageManager
    RegistrationRepository --> FileStorageManager

    LoginController ..> AppContext
    AdminDashboardController ..> AppContext
    StudentManagementController ..> AppContext
    CourseManagementController ..> AppContext
    StudentDashboardController ..> AppContext
    CourseCatalogController ..> AppContext
    MyCoursesController ..> AppContext
    SceneNavigator ..> AppContext : creates controllers with
```

## 3. Design notes linked to the diagram

| Concept | Where to point during the assessment |
| --- | --- |
| Encapsulation | Private fields in `Person`, `Course`, `Registration`; access through getters/setters |
| Abstraction | Abstract `Person`; `Registrable` interface |
| Inheritance | `Student` and `Administrator` extend `Person` |
| Polymorphism | `context.getCurrentUser()` can be either subclass; `getRole()` dispatches dynamically |
| Method overriding | `getRole()` and `getContactSummary()` in both subclasses; `enroll()` in `Course` |
| Method overloading | Two `Student` constructors; two `addStudent(...)` methods |
| No-argument constructors | `Person()`, `Student()`, `Administrator()`, `Course()`, `Registration()` — required by the brief |
| Parameterized constructors | Every entity, service, repository and `AppContext` is constructed with arguments |
| Package-private methods | `toCsvLine(...)` in the three repositories (visible to same-package tests only) |
| Aggregation | `Course` keeps `List<Student>` |
| Composition | `AppContext` owns services; `RegistrationService` owns registration records |
| Exception hierarchy | `RegistrationException` → validation, full, duplicate, not-found |
| File I/O | `FileStorageManager` with `IOException` and `DataAccessException` |

> The diagram is intentionally limited to public API that matters for the report.
> Getters/setters of some classes are omitted to keep it readable; they exist in the
> source and are listed in `docs/REQUIREMENTS.md`.
