package com.schoolmanagement.services;


import com.schoolmanagement.models.Student;
import com.schoolmanagement.services.StudentService;
import com.schoolmanagement.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService studentService;
    private Session session;

    @BeforeEach
    void setup() {
        session = HibernateUtil.getSessionFactory().openSession();
        studentService = new StudentService();
    }

    @AfterEach
    void teardown() {
        session.close();
    }

    @Test
    @DisplayName("Test Creating a New Student")
    void testCreateStudent() {
        Transaction tx = session.beginTransaction();

        // Creating a new student
        Student newStudent = new Student("DNoa@email.com", "Daniel Noa", "password123");
        studentService.createStudent(newStudent);

        // Fetch the student from DB
        Student fetchedStudent = session.get(Student.class, "DNoa@email.com");

        tx.commit();

        // Assertions
        assertNotNull(fetchedStudent);
        assertEquals("DNoa@email.com", fetchedStudent.getEmail());
        assertEquals("Daniel Noa", fetchedStudent.getName());
        assertEquals("password123", fetchedStudent.getPassword());
    }
}