package luckydrop.demo.draw.bookmark.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DrawBookmarkId implements Serializable {
    private Long userId;
    private Long drawId;
}
