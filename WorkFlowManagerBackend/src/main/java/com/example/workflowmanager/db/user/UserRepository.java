package com.example.workflowmanager.db.user;

import com.example.workflowmanager.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.email like (?1)")
    List<User> getListByEmailLike(String emailSearch);

    @Query("select u from User u where u.id in (?1)")
    List<User> getListByIds(Collection<Long> ids);

    @Query("select distinct m.creator from Message m where m.chat.id = ?1")
    List<User> getListByChatId(Long chatId);
}
