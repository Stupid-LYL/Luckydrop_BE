package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.DrawWinner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawWinnerRepository  extends JpaRepository<DrawWinner, Long> {

    boolean existsByDrawId(Long drawId);

    List<DrawWinner> findByDrawId(Long drawId);

    //유저별 당첨 조회
    List<DrawWinner> findByUserId(Long userId);
}
