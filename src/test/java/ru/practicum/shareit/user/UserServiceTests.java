package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTests {

    private final EntityManager em;
    private final UserService service;

    @Test
    void saveUserTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        service.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUserTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto userGet = service.create(userDto);
        UserDto userDtoUpdate = makeUserDto("Иван", "ivan@email.com");
        service.update(userGet.getId(), userDtoUpdate);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDtoUpdate.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDtoUpdate.getName()));
        assertThat(user.getEmail(), equalTo(userDtoUpdate.getEmail()));
    }

    @Test
    void findBuIdUserTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto userGet = service.create(userDto);
        UserDto userGetById = service.findById(userGet.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userGet.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userGetById.getName()));
        assertThat(user.getEmail(), equalTo(userGetById.getEmail()));
    }

    @Test
    void findAllUsersTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        service.create(userDto);
        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        service.create(userDto2);
        List<UserDto> listOfUserDto = service.findAll();

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<User> listOfUser = query.getResultList();

        assertThat(listOfUser.get(0).getId(), notNullValue());
        assertThat(listOfUser.get(0).getName(), equalTo(listOfUserDto.get(0).getName()));
        assertThat(listOfUser.get(0).getEmail(), equalTo(listOfUserDto.get(0).getEmail()));
        assertThat(listOfUser.get(1).getId(), notNullValue());
        assertThat(listOfUser.get(1).getName(), equalTo(listOfUserDto.get(1).getName()));
        assertThat(listOfUser.get(1).getEmail(), equalTo(listOfUserDto.get(1).getEmail()));
    }

    @Test
    void deleteByUserIdTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        service.create(userDto);
        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto userGet2 = service.create(userDto2);

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<User> listOfUser = query.getResultList();

        assertThat(listOfUser.size(), equalTo(2));
        assertThat(listOfUser.get(0).getId(), notNullValue());
        assertThat(listOfUser.get(0).getName(), equalTo(userDto.getName()));
        assertThat(listOfUser.get(0).getEmail(), equalTo(userDto.getEmail()));
        assertThat(listOfUser.get(1).getId(), notNullValue());
        assertThat(listOfUser.get(1).getName(), equalTo(userDto2.getName()));
        assertThat(listOfUser.get(1).getEmail(), equalTo(userDto2.getEmail()));

        service.deleteById(userGet2.getId());

        TypedQuery<User> query2 = em.createQuery("Select u from User u ", User.class);
        List<User> listOfUser2 = query2.getResultList();

        assertThat(listOfUser2.size(), equalTo(1));
        assertThat(listOfUser2.get(0).getId(), notNullValue());
        assertThat(listOfUser2.get(0).getName(), equalTo(listOfUser.get(0).getName()));
        assertThat(listOfUser2.get(0).getEmail(), equalTo(listOfUser.get(0).getEmail()));

    }




    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }

}
