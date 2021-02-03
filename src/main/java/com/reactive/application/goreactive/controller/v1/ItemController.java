package com.reactive.application.goreactive.controller.v1;

import com.reactive.application.goreactive.document.Item;
import com.reactive.application.goreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.reactive.application.goreactive.constants.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemController {

    /*@ExceptionHandler(RuntimeException.class)
    //override the default behaviour
    public ResponseEntity<String> handlerRunTimeException( RuntimeException ex){
        log.error("Exception caught in handleRuntimeException: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }*/

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){

        Flux<Item> itemFlux =
                itemReactiveRepository.findAll();

        return itemFlux;
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){

        return itemReactiveRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){
        //since this is non blocking we need to return something, thus void
        return itemReactiveRepository.deleteById(id);

    }

    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runTimeException(){

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Runtime Exception Occurred")));

    }


    //It and item to be updated in the request = path variable and request body
    //using the id , get the item from the DB
    //using the body update the item
    //save the item back to DB
    //return the saved item

    @PutMapping (ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){

        return itemReactiveRepository
                .findById(id)
                .flatMap( currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }



}
