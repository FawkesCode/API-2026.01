package com.fawkes.api.Repositories;
import com.fawkes.api.Entities.OrderNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNotes, Long> {
}
