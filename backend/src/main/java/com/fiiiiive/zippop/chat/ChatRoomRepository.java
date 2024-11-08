package com.fiiiiive.zippop.chat;

import com.fiiiiive.zippop.chat.model.entity.ChatRoom;
import com.fiiiiive.zippop.member.model.entity.Company;
import com.fiiiiive.zippop.member.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByName(String name);

    List<ChatRoom> findByCustomer(Customer customer);
    List<ChatRoom> findByCompany(Company company);
    Optional<ChatRoom> findByCustomerAndCompany(Customer customer, Company company);
}
