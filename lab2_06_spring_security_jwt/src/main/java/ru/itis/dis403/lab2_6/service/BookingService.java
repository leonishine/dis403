package ru.itis.dis403.lab2_6.service;

import org.springframework.stereotype.Service;
import ru.itis.dis403.lab2_6.dto.BookingDto;
import ru.itis.dis403.lab2_6.model.Booking;
import ru.itis.dis403.lab2_6.model.User;
import ru.itis.dis403.lab2_6.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public BookingDto getBookingById(Long bookingId, User user) {
        Booking b = bookingRepository.findByIdAndHotelId(bookingId, user.getHotel().getId());

        return BookingDto.builder()
                .id(b.getId())
                .arrivaldate(b.getArrivaldate())
                .stayingdate(b.getStayingdate())
                .departuredate(b.getDeparturedate())
                .personId(b.getPerson().getId())
                .name(b.getPerson().getName())
                .room(b.getRoom())
                .build();
    }

    public BookingDto updateBooking(BookingDto dto, User user) {

        Booking b = bookingRepository.findByIdAndHotelId(dto.getId(), user.getHotel().getId());

        if (b == null) {
            throw new RuntimeException("Бронирование не найдено");
        }

        b.setArrivaldate(dto.getArrivaldate());
        b.setStayingdate(dto.getStayingdate());
        b.setDeparturedate(dto.getDeparturedate());
        b.setRoom(dto.getRoom());

        bookingRepository.save(b);

        return BookingDto.builder()
                .id(b.getId())
                .arrivaldate(b.getArrivaldate())
                .stayingdate(b.getStayingdate())
                .departuredate(b.getDeparturedate())
                .personId(b.getPerson().getId())
                .name(b.getPerson().getName())
                .room(b.getRoom())
                .build();
    }
}