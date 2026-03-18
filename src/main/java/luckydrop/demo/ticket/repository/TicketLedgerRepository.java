package luckydrop.demo.ticket.repository;

import luckydrop.demo.ticket.entity.TicketLedger;
import luckydrop.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketLedgerRepository extends JpaRepository<TicketLedger, Integer> {

    List<TicketLedger> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<TicketLedger> findByIdempotencyKey(String idempotencyKey);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
