package com.example.workflowmanager.db.chat;

import com.example.workflowmanager.entity.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>
{

}
