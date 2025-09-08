package org.example.pojo;

import java.time.ZonedDateTime;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.enums.OrderStatus;

import javax.persistence.*;

@Entity
@Getter
@Setter

public class OrderPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime dateTime;

    @Enumerated(EnumType.STRING) // 👈 Store enum as VARCHAR instead of ordinal
    @Column(nullable = false)
    private OrderStatus status;


    private Boolean isInvoiced = false;
}
