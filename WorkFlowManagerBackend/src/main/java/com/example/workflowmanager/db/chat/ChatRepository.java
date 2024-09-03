package com.example.workflowmanager.db.chat;

import com.example.workflowmanager.entity.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>
{
    @Query("select c from Chat c where c.id in (?1)")
    List<Chat> getList(Collection<Long> chatIds);

}
