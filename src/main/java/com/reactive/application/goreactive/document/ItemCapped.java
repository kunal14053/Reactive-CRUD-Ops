package com.reactive.application.goreactive.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document //@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped {

    @Id
    private String id;

    private String description;

    private double price;

}
