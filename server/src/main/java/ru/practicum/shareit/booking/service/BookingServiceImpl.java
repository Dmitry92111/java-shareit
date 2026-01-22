package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.type.ConditionsNotMetException;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.exception.type.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.exception.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingCreateRequestDto dto) {
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", userId)));

        Item item = itemStorage.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "item", dto.getItemId())));

        validateDates(dto.getStart(), dto.getEnd());

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ConditionsNotMetException(ITEM_IS_NOT_AVAILABLE_FOR_BOOKING);
        }

        Long ownerId = item.getOwner().getId();
        if (ownerId.equals(userId)) {
            throw new ConditionsNotMetException(OWNER_CANNOT_BOOK_HIS_OWN_ITEM);
        }

        Booking booking = Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new InternalException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", ownerId)));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "booking", bookingId)));

        Long actualOwnerId = booking.getItem().getOwner().getId();
        if (!actualOwnerId.equals(ownerId)) {
            throw new ConditionsNotMetException(USER_DONT_HAVE_PERMISSION_TO_APPROVE_OR_REJECT_BOOKING);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConditionsNotMetException(BOOKING_STATUS_HAS_ALREADY_BEEN_DECIDED);
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", userId)));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "booking", bookingId)));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ConditionsNotMetException(USER_DONT_HAVE_PERMISSION_TO_GET_ACCESS_TO_BOOKING);
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByBooker(Long userId, String state) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", userId)));

        BookingState st = parseState(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (st) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBooker(userId, now);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    public List<BookingResponseDto> getByOwner(Long ownerId, String state) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", ownerId)));

        BookingState st = parseState(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (st) {
            case ALL -> bookingRepository.findAllByOwner(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwner(ownerId, now);
            case PAST -> bookingRepository.findPastByOwner(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwner(ownerId, now);
            case WAITING -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toDto).toList();
    }

    private static void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Start/end must be provided");
        }
        if (!start.isBefore(end)) {
            throw new ValidationException("Start must be before end");
        }
    }

    private static BookingState parseState(String state) {
        String raw = (state == null || state.isBlank()) ? "ALL" : state;
        try {
            return BookingState.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
