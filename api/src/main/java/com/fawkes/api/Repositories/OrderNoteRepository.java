package com.fawkes.api.Repositories;
import com.fawkes.api.Entities.OrderNote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {

 boolean findByNumberNote(String numberNote);
}
