package com.notes.repository;

import com.notes.model.User;
import com.notes.NotesApplication;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)
@ContextConfiguration(locations = {"classpath:test-db-context.xml"})
@Slf4j
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindAll_ThenUsersShouldReturn() {
        List<User> results = userRepository.findAll();
        assertEquals(1, results.size());
    }
    @Test
    public void givenUsername_WhenFindByUsername_ThenUserShouldReturn() {
        Optional<User> result = userRepository.findByUsername("lukasz");
        assertTrue(result.isPresent());
    }
    @Test
    public void givenId_WhenFindById_ThenUserShouldReturn() {
        Optional<User> result = userRepository.findById("1");
        assertTrue(result.isPresent());
    }
    @Test
    public void whenSave_ThenUserShouldReturn() {
        User user=new User("username","password");
        user.setId("2");

        assertThat(userRepository.save(user), Matchers.instanceOf(User.class));
        assertEquals(2,userRepository.findAll().size());
    }

}

