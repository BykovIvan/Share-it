package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTests {

    private final EntityManager em;
    private final BookingService service;

    @Test
    void saveBookingTest() {


//        UserDto userDto = makeUserDto("Пётр", "some@email.com");
//        service.create(userDto);
//
//        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
//        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
//
//        assertThat(user.getId(), notNullValue());
//        assertThat(user.getName(), equalTo(userDto.getName()));
//        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

}
