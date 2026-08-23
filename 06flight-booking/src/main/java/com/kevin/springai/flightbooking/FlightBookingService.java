package com.kevin.springai.flightbooking;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class FlightBookingService {

    private final BookingData db;

    public FlightBookingService() {
        db = new BookingData();
        initDemoData();
    }

    private void initDemoData() {
        List<String> names = List.of("王大锤", "诸葛", "百里", "楼兰", "庄周");
        List<String> airportCodes = List.of("北京", "上海", "广州", "深圳", "杭州", "南京", "青岛", "成都", "武汉", "西安", "重庆", "大连", "天津");
        Random random = new Random();

        var customers = new ArrayList<Customer>();
        var bookings = new ArrayList<Booking>();

        for (int i = 0; i < 5; i++) {
            String from = airportCodes.get(random.nextInt(airportCodes.size()));
            String to = airportCodes.get(random.nextInt(airportCodes.size()));
            BookingClass bookingClass = BookingClass.values()[random.nextInt(BookingClass.values().length)];
            LocalDate date = LocalDate.now().plusDays(2 * (i + 1));

            Customer customer = new Customer();
            customer.setName(names.get(i));

            Booking booking = new Booking("10" + (i + 1), date, customer, BookingStatus.CONFIRMED, from, to, bookingClass);
            customer.getBookings().add(booking);

            customers.add(customer);
            bookings.add(booking);
        }

        db.setCustomers(customers);
        db.setBookings(bookings);
    }

    // 获取所有已预订的航班
    public List<BookingDetails> getBookings() {
        return db.getBookings().stream().map(this::toBookingDetails).toList();
    }

    private BookingDetails toBookingDetails(Booking booking) {
        return new BookingDetails(booking.getBookingNumber(),
                booking.getCustomer().getName(),
                booking.getDate(),
                booking.getBookingStatus(),
                booking.getFrom(),
                booking.getTo(),
                booking.getBookingClass().toString());
    }

    // 取消预订
    public void cancelBooking(String bookingNumber, String name) {
        var booking = findBooking(bookingNumber, name);
        if (booking.getDate().isBefore(LocalDate.now().plusDays(2))) {
            throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
    }

    // 根据预定号+姓名查询航班
    private Booking findBooking(String bookingNumber, String name) {
        return db.getBookings()
                .stream()
                .filter(b -> b.getBookingNumber().equalsIgnoreCase(bookingNumber))
                .filter(b -> b.getCustomer().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    // 查询航班详情
    public BookingDetails getBookingDetails(String bookingNumber, String name) {
        Booking booking = findBooking(bookingNumber, name);
        return toBookingDetails(booking);
    }

    public record BookingDetails(String bookingNumber,
                                 String name,
                                 LocalDate begindate,
                                 BookingStatus bookingStatus,
                                 String from,
                                 String to,
                                 String bookingClass) {
    }
}
