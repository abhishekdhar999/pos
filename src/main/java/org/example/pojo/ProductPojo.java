package org.example.pojo;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "barcode"),
        indexes = {
                @Index(name = "idx_product_client", columnList = "clientId"),
                @Index(name = "idx_client_barcode", columnList = "clientId, barcode") // composite index
        })

public class ProductPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Integer clientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;


    private String imageUrl;

}
