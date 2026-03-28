package com.fawkes.api.Repository;
import com.fawkes.api.Entities.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {
}
