package luckydrop.demo.draw.inventory.repository;

import luckydrop.demo.draw.inventory.entity.InventoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryImageRepository extends JpaRepository<InventoryImage, Long> {

    List<InventoryImage> findByInventoryIdOrderBySortOrderAsc(Long inventoryId);

    @Query("""
        select ii.imageUrl
        from InventoryImage ii
        where ii.inventory.id = :inventoryId
        order by ii.sortOrder asc
    """)
    List<String> findImageUrlsByInventoryId(@Param("inventoryId") Long inventoryId);
}
