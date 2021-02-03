package com.reactive.application.goreactive.repository;


import com.reactive.application.goreactive.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest //this won't load the full application context
@RunWith(SpringRunner.class)
@DirtiesContext //every test will get a new context
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    //pass id as null as its generated once we put the item in DB
    List<Item> itemsList = Arrays.asList(
            new Item(null,"LG TV", 420.0),
            new Item(null,"Apple TV", 900.0),
            new Item(null,"Samsung TV", 100.0),
            new Item(null,"Oppo TV", 300.0),
            //for this it won't generate the id and use what we supplied
            new Item("ABC","Mi TV", 300.0));

    @Before
    public void setUp(){

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemsList))
        .flatMap(itemReactiveRepository::save)
        .doOnNext((item -> {
          System.out.println("Inserted Item is:" +item.getId());
        }))//wait until all the item are saved, thus block last,
                // only to be used in test cases
                .blockLast();


    }

    @Test
    public void getAllItems(){
        //when we run the test it won't connect to the actual DB,
        // it will connect to the embedded mongo db
        // even if you don't have mongodb setup on your system
        Flux<Item> itemFlux = itemReactiveRepository
                .findAll()
                .log();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void getItemById(){
        //when we run the test it won't connect to the actual DB,
        // it will connect to the embedded mongo db
        // even if you don't have mongodb setup on your system
        Mono<Item> itemFlux = itemReactiveRepository
                .findById("ABC")
                .log();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextMatches((item ->
                    item.getDescription().equals("Mi TV")))
                .verifyComplete();

    }

    @Test
    public void findItemByDescription(){

        Mono<Item> itemFlux = itemReactiveRepository
                .findByDescription("LG TV")
                .log();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();


    }


    @Test
    public void saveItem(){

        Item item = new Item(null, "Google TV", 500.0);

        Mono<Item> itemMono = itemReactiveRepository.save(item).log("SaveItem: ");


        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches(item1 -> (
                    item1.getId()!=null && item1.getDescription().equals("Google TV")
                    ))
                .verifyComplete();
    }


    @Test
    public void updateItem(){

        double newPrice = new Double(520);

        Mono<Item> updateFlux = itemReactiveRepository.findByDescription("LG TV")
                .map(item ->
                {
                    item.setPrice(newPrice); //setting the new price
                    return item;
                }).flatMap( item -> {
                    return itemReactiveRepository.save(item); //saving the item with new price
        }).log("Update Item-> ");

        StepVerifier.create(updateFlux)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice()==520.00)
                .verifyComplete();
    }


    @Test
    public void deleteItemById(){

        Mono<Void> itemFlux = itemReactiveRepository.findById("ABC")
                .map(Item::getId) //get the id -> transform from one type to another
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });


        StepVerifier.create(itemFlux.log("Delete Item: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("Item List: "))
                .expectNextCount(4)
                .verifyComplete();

    }

    @Test
    public void deleteItem(){

        Mono<Void> itemFlux = itemReactiveRepository.findByDescription("LG TV")
                .flatMap((item) -> {
                    return itemReactiveRepository.delete(item);
                });


        StepVerifier.create(itemFlux.log("Delete Item: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("Item List: "))
                .expectNextCount(4)
                .verifyComplete();

    }



}
