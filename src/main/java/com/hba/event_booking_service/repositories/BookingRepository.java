package com.hba.event_booking_service.repositories;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.hba.event_booking_service.models.entities.Booking;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findAllByUserId(String userId);

    List<Booking> findAllByUserIdAndEventId(String userId, String eventId);

    @Aggregation({
        "{ $addFields: { eventObjId: { $toObjectId: '$eventId' } } }",
        "{ $lookup: { from: 'events', localField: 'eventObjId', foreignField: '_id', as: 'event' } }",
        "{ $unwind: '$event' }",
        "{ $group: { _id: '$event.title', totalBookings: { $sum: 1 } } }"
    })
    List<Map<String, Object>> getTotalBookingsPerEvent();

    @Aggregation({
        "{ $addFields: { eventObjId: { $toObjectId: '$eventId' } } }",
        "{ $lookup: { from: 'events', localField: 'eventObjId', foreignField: '_id', as: 'event' } }",
        "{ $unwind: '$event' }",
        "{ $group: { _id: '$event.title', seatsSold: { $sum: '$numberOfSeats' } } }",
        "{ $sort: { seatsSold: -1 } }"
    })
    List<Map<String, Object>> getMostPopularEvents();

    @Aggregation({
        "{ $addFields: { eventObjId: { $toObjectId: '$eventId' } } }",
        "{ $lookup: { from: 'events', localField: 'eventObjId', foreignField: '_id', as: 'event' } }",
        "{ $unwind: '$event' }",
        "{ $group: { _id: '$event.title', revenue: { $sum: '$totalPrice' } } }"
    })
    List<Map<String, Object>> getRevenuePerEvent();

    @Aggregation({
        "{ $group: { _id: { month: { $month: '$bookingDate' }, year: { $year: '$bookingDate' } }, totalBookings: { $sum: 1 } } }",
        "{ $sort: { '_id.year': 1, '_id.month': 1 } }"
    })
    List<Map<String, Object>> getMonthlyBookingTotals();

    @Aggregation({
        "{ $addFields: { eventObjId: { $toObjectId: '$eventId' } } }",
        "{ $lookup: { from: 'events', localField: 'eventObjId', foreignField: '_id', as: 'event' } }",
        "{ $unwind: '$event' }",
        "{ $group: { _id: '$event.category', seatsSold: { $sum: '$numberOfSeats' } } }"
    })
    List<Map<String, Object>> getSeatsSoldByCategory();
}
