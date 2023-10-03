package ru.practicum.ewm.category.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    void deleteById(Integer catId);

    @Query("select c from Category c order by c.id")
    List<Category> findAllOrderById(PageRequest page);

    Optional<Category> findByName(String name);

    @Query("select c from Category c, Event e where e.category = :category")
    List<Category> findCategoryRelatedToEvents(Category category);
}