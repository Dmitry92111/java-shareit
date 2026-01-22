package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("""
           select b from Booking b
           where b.booker.id = :bookerId
             and :now between b.start and b.end
           order by b.start desc
           """)
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId,
                                      @Param("now") LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
           order by b.start desc
           """)
    List<Booking> findAllByOwner(@Param("ownerId") Long ownerId);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.end < :now
           order by b.start desc
           """)
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("now") LocalDateTime now);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.start > :now
           order by b.start desc
           """)
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and :now between b.start and b.end
           order by b.start desc
           """)
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.status = :status
           order by b.start desc
           """)
    List<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                       @Param("status") BookingStatus status);

    @Query("""
       select b from Booking b
       where b.item.id in :itemIds
         and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
         and b.start <= :now
       order by b.start desc
       """)
    List<Booking> findLastApprovedBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                                   @Param("now") LocalDateTime now);

    @Query("""
       select b from Booking b
       where b.item.id in :itemIds
         and b.status = ru.practicum.shareit.booking.model.BookingStatus.APPROVED
         and b.start > :now
       order by b.start asc
       """)
    List<Booking> findNextApprovedBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                                   @Param("now") LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime time
    );
}
