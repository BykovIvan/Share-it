package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ShareItTests {

	@Autowired
	ObjectMapper mapper;

	@MockBean
	ItemService itemService;

	@Autowired
	private MockMvc mvc;


	private ItemDto itemDto = ItemDto.builder()
			.id(1L)
			.name("Hammer")
			.description("Hammer for you")
			.available(true)
//            .ownerId(1L)
			.owner(UserDto.builder()
					.id(1L)
					.name("Ivan")
					.email("ivan@yandex.ru")
					.build())
//            .requestId(1L)
			.build();


	@Test
	void saveNewItem() throws Exception {
		when(itemService.create(anyLong(), any()))
				.thenReturn(itemDto);

		mvc.perform(post("/items")
						.content(mapper.writeValueAsString(itemDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
				.andExpect(jsonPath("$.name", is(itemDto.getName())))
				.andExpect(jsonPath("$.description", is(itemDto.getDescription())))
				.andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
				.andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId())))
				.andExpect(jsonPath("$.owner", is(itemDto.getOwner())))
				.andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
	}

}
