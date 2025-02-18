package com.schoolmanagement.tests;

import com.schoolmanagement.models.Student;
import com.schoolmanagement.models.Course;
import com.schoolmanagement.services.StudentService;
import com.schoolmanagement.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class StudentServiceIntegrationTest {

    private static StudentService studentService;
    private Session session;

    @BeforeAll
    static void setupOnce() {
        studentService = new StudentService();
    }

    @BeforeEach
    void setup() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    @AfterEach
    void teardown() {
        session.close();
    }

    @Test
    @DisplayName("Test Registering a Student to a Course")
    void testRegisterStudentToCourse() {
        Transaction tx = session.beginTransaction();

        // Create a new student
        Student student = new Student("test3@email.com", "John Smith", "pass123");
        studentService.createStudent(student);

        // Fetch an existing course (Assuming course ID 1 exists)
        Course course = session.get(Course.class, 1);
        assertNotNull(course, "Course must exist to proceed with registration.");

        // Register student to course
        studentService.registerStudentToCourse(student.getEmail(), course.getId());

        // Fetch updated student courses
        List<Course> courses = studentService.getStudentCourses(student.getEmail());

        tx.commit();

        // Assertions
        assertFalse(courses.isEmpty(), "Student should be registered in at least one course.");
        assertTrue(courses.stream().anyMatch(c -> c.getId() == 1), "Student should be enrolled in Course ID 1.");
    }

    @Test
    @DisplayName("Test Retrieving All Students")
    void testGetAllStudents() {
        List<Student> students = studentService.getAllStudents();
        assertNotNull(students, "List should not be null");
        assertFalse(students.isEmpty(), "Students list should not be empty.");
    }

    @Test
    @DisplayName("Test Validating Student Login")
    void testValidateStudent() {
        // Assuming a student with email 'test3@email.com' was created earlier
        boolean isValid = studentService.validateStudent("test3@email.com", "pass123");
        assertTrue(isValid, "Student login should be valid with correct credentials.");

        boolean isInvalid = studentService.validateStudent("test3@email.com", "wrongpass");
        assertFalse(isInvalid, "Student login should be invalid with incorrect credentials.");
    }
}
