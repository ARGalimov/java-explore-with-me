package ru.practicum.ewm.user.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u order by u.id")
    List<User> findAllOrderById(PageRequest page);

    List<User> findAllByIdIn(List<Integer> ids);

    void deleteUserById(Integer id);

    Optional<User> findByName(String name);
}