package luckydrop.demo.draw.inventory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String name;

    @Column(length = 80, nullable = false)
    private String brand;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "retail_price")
    private Integer retailPrice;

    //배송 가능 여부 (tinyint(1))
    @Column(nullable = false)
    private boolean shippable;

    //private LocalDateTime created_at;

    public Inventory(String name,
                     String brand,
                     String description,
                     Integer retailPrice,
                     Boolean shippable) {
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.retailPrice = retailPrice;
        this.shippable = shippable != null ? shippable : true;
    }
}
