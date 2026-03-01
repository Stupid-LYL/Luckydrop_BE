package luckydrop.demo.draw.inventory.repository;

import luckydrop.demo.draw.inventory.entity.InventoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryImageRepository extends JpaRepository<InventoryImage, Long> {
}
