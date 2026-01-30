package luckydrop.demo.draw.inventory.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_images")
@Getter
@NoArgsConstructor
public class InventoryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "image_url", length = 50, nullable = false)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder; //이미지 노출 순서

    //private LocalDateTime created_at;

    @Builder
    public InventoryImage(Inventory inventory, String imageUrl, Integer sortOrder) {
        this.inventory = inventory;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }


}
