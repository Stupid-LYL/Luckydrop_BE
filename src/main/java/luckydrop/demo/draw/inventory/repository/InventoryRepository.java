package luckydrop.demo.draw.inventory.repository;

import luckydrop.demo.draw.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
