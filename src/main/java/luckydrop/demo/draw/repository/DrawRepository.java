package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.Draw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    boolean existsByInventoryId(Long inventoryId); // inventory_id UNIQUE 체크용
}



