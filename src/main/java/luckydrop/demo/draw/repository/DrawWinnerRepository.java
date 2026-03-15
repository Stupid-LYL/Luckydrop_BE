package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.DrawWinner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawWinnerRepository  extends JpaRepository<DrawWinner, Long> {

    boolean existsByDrawId(Long drawId); // 드로우 추첨 완료 여부

    boolean existsByDrawIdAndUserId(Long drawId, Long userId); // 현재 유저 당첨 여부 확인

    List<DrawWinner> findByDrawId(Long drawId); // 드로우 전체 당첨자

    //유저별 당첨 조회
    List<DrawWinner> findByUserId(Long userId);
}
