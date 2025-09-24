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

public class OrderPojo extends AbstractPojo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;


    private Boolean isInvoiced = false;
}
