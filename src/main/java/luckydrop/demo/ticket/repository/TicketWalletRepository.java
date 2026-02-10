package luckydrop.demo.ticket.repository;

import luckydrop.demo.ticket.entity.TicketWallet;
import luckydrop.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketWalletRepository extends JpaRepository<TicketWallet, Integer> {

    Optional<TicketWallet> findByUserId(Long userId);
}
