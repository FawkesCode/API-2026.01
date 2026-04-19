package com.fawkes.api.Repositories;
import com.fawkes.api.Entities.OrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {

 boolean existsByNumberNoteAndSerie(String numberNote,String serie);
 
 
}
