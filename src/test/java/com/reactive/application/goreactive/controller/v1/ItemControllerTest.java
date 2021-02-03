package com.reactive.application.goreactive.controller.v1;



import com.reactive.application.goreactive.constants.ItemConstants;
import com.reactive.application.goreactive.document.Item;
import com.reactive.application.goreactive.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemsList = Arrays.asList(
            new Item(null,"LG TV", 420.0),
            new Item(null,"Apple TV", 900.0),
            new Item(null,"Samsung TV", 100.0),
            new Item(null,"Oppo TV", 300.0),
            new Item("ABC","Mi TV", 300.0));

    @Before
    public void setUp(){

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemsList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted Item is:" +item.getId());
                })).blockLast();
    }

    @Test
    public void testGetAllItems(){

        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void testGetAllItems_approach2(){

        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                //to get the body
        .consumeWith((response) -> {
            List<Item> items = response.getResponseBody();
            items.forEach((item) -> {
                assertTrue(item.getId()!=null);
            });
        });
    }


    @Test
    public void testGetAllItems_approach3(){

       Flux<Item> itemFlux = webTestClient.get()
               .uri(ItemConstants.ITEM_END_POINT_V1)
               .exchange()
               .expectStatus().isOk()
               .expectHeader().contentType(MediaType.APPLICATION_JSON)
               .returnResult(Item.class)
               .getResponseBody();
        //all this will happen over the network
        StepVerifier.create(itemFlux.log("Value form Network: ")).expectNextCount(5).verifyComplete();

    }

    @Test
    public void getOneItem(){

        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 300.0);

    }

    @Test
    public void getOneItem_notFound(){

        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "123")
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void createItem(){

        Item item = new Item(null, "I-ponne X", 1000.0);

        webTestClient
                .post()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("I-ponne X");


    }

    @Test
    public void deleteItem()
    {
        webTestClient
                .delete()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){

        Item item = new Item("ABC","Mi TV-2", 500.0);

        webTestClient
                .put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 500.0);

    }


    @Test
    public void updateItem_notFount(){

        Item item = new Item("DEF","Mi TV-2", 500.0);

        webTestClient
                .put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void runTimeException(){

        webTestClient.get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/runtimeException"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Runtime Exception Occurred");


    }


}
