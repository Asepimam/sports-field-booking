package org.sports.field.booking.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.sports.field.booking.domain.entity.BookingEntity;
import org.sports.field.booking.domain.entity.BookingStatus;
import org.sports.field.booking.domain.repository.BookingRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookingRepositoryImpl implements BookingRepository, PanacheRepositoryBase<BookingEntity, UUID> {

    @Override
    public void save(BookingEntity booking) {
        persist(booking);
    }

    @Override
    public List<BookingEntity> getBookingsByCustomerEmail(String customerEmail, int page, int size) {
        return find("customer.email", customerEmail).page(page - 1, size).list();
    }

    @Override
    public List<BookingEntity> getBookingsByOwnerEmail(String ownerEmail, int page, int size) {
        return find("ground.owner.email", ownerEmail).page(page - 1, size).list();
    }

    @Override
    public long countBookingsByCustomerEmail(String customerEmail) {
        return count("customer.email", customerEmail);
    }

    @Override
    public long countBookingsByOwnerEmail(String ownerEmail) {
        return count("ground.owner.email", ownerEmail);
    }

    @Override
    public long sumRevenueByOwnerEmail(String ownerEmail) {
        Long revenue = getEntityManager()
                .createQuery("""
                        select coalesce(sum(booking.totalPrice), 0)
                        from BookingEntity booking
                        where booking.ground.owner.email = :ownerEmail
                        and booking.status <> :cancelledStatus
                        """, Long.class)
                .setParameter("ownerEmail", ownerEmail)
                .setParameter("cancelledStatus", BookingStatus.CANCELLED)
                .getSingleResult();
        return revenue == null ? 0 : revenue;
    }
}
