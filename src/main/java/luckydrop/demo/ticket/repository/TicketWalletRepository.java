package luckydrop.demo.ticket.repository;

import jakarta.persistence.LockModeType;
import luckydrop.demo.ticket.entity.TicketWallet;
import luckydrop.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketWalletRepository extends JpaRepository<TicketWallet, Integer> {

    Optional<TicketWallet> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tw FROM TicketWallet tw WHERE tw.user.id = :userId")
    Optional<TicketWallet> findByUserIdWithLock(@Param("userId") Long userId);
}
