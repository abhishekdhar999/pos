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

    @Column(nullable = false)
    private String imageUrl = "https://imgs.search.brave.com/c2UELGaT7Yb45HXevtqf8jglYfdABTD5IkT2iFPbENU/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/ZG1vZG90LmNvbS9j/ZG4vc2hvcC9wcm9k/dWN0cy9zcHJhenpv/LW1hcnJvbmUtNDAw/NTI3LmpwZz92PTE3/NTA2ODc0Njgmd2lk/dGg9NTMz";

}
