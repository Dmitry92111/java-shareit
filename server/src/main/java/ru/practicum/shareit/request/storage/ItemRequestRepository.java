package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("""
           select r from ItemRequest r
           where r.requestor.id <> :userId
           order by r.created desc
           """)
    List<ItemRequest> findAllOthers(@Param("userId") Long userId);
}
