package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class JPAUserEMTests {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    public void saveBootStrappingTest() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@yandex.ru");

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
    }


    @Test
    public void saveTest() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@yandex.ru");

        Assertions.assertNull(user.getId());
        repository.save(user);
        Assertions.assertNotNull(user.getId());
    }

}
