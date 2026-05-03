package org.sports.field.booking.application.service;

import java.util.ArrayList;
import java.util.List;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.NotFoundException;
import org.sports.field.booking.application.mapper.GroundMapper;
import org.sports.field.booking.domain.entity.GroundEntity;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.domain.repository.GroundRepository;
import org.sports.field.booking.domain.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GroundServiceImpl implements GroundService {
    private final GroundRepository groundRepository;
    private final GroundMapper groundMapper;
    private final UserRepository userRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager em;

    public GroundServiceImpl(GroundMapper groundMapper, GroundRepository groundRepository,
            UserRepository userRepository) {
        this.groundMapper = groundMapper;
        this.groundRepository = groundRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public GroundResponseDTO createGround(String ownerEmail, GroundRequestDTO groundRequestDTO) {
        try {
            if (groundRepository.existsByName(groundRequestDTO.getNameGround())) {
                throw new ConflictException("Ground name already exists");
            }

            UserEntity owner = userRepository.findByEmail(ownerEmail)
                    .orElseThrow(() -> new NotFoundException("Owner not found"));

            var groundEntity = groundMapper.toEntity(groundRequestDTO);
            groundEntity.owner = owner;
            if (groundEntity.isAvailable == null) {
                groundEntity.isAvailable = true;
            }

            groundRepository.save(groundEntity);
            return groundMapper.toResponseDTO(groundEntity);
        } catch (ConflictException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to create ground", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> getGrounds(int page, int size) {
        try {
            return groundRepository.getGrounds(page, size)
                    .stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch grounds", ex);
        }
    }

    @Override
    public long countGrounds() {
        try {
            return groundRepository.countGrounds();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count grounds", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> getOwnerGrounds(String ownerEmail, int page, int size) {
        try {
            return groundRepository.getGroundsByOwnerEmail(ownerEmail, page, size)
                    .stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch owner grounds", ex);
        }
    }

    @Override
    public long countOwnerGrounds(String ownerEmail) {
        try {
            return groundRepository.countGroundsByOwnerEmail(ownerEmail);
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count owner grounds", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> getPublicGrounds(int page, int size, String sortBy, String order) {
        try {
            String validSortBy = validateSortBy(sortBy);
            String validOrder = validateOrder(order);

            int offset = (page - 1) * size;

            // Hanya tampilkan ground yang available (isAvailable = true)
            String jpql = "SELECT g FROM GroundEntity g WHERE g.isAvailable = true " +
                    "ORDER BY g." + validSortBy + " " + validOrder;

            List<GroundEntity> grounds = em.createQuery(jpql, GroundEntity.class)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();

            return grounds.stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();

        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch public grounds", ex);
        }
    }

    @Override
    public long countPublicGrounds() {
        try {
            String jpql = "SELECT COUNT(g) FROM GroundEntity g WHERE g.isAvailable = true";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count public grounds", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> getFeaturedGrounds(int limit) {
        try {
            // Untuk featured, kita bisa ambil berdasarkan popularitas atau rating
            // Karena belum ada field rating, kita bisa urutkan berdasarkan yang paling baru
            String jpql = "SELECT g FROM GroundEntity g WHERE g.isAvailable = true " +
                    "ORDER BY g.createdAt DESC";

            List<GroundEntity> grounds = em.createQuery(jpql, GroundEntity.class)
                    .setMaxResults(limit)
                    .getResultList();

            return grounds.stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch featured grounds", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> searchPublicGrounds(String keyword, String sportType, String location,
            Double minPrice, Double maxPrice, int page, int size) {
        try {
            int offset = (page - 1) * size;

            // Menggunakan Criteria API untuk dynamic query yang fleksibel
            List<GroundEntity> grounds = searchWithCriteriaAPI(keyword, sportType, location,
                    minPrice, maxPrice, offset, size);

            return grounds.stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();

        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to search public grounds", ex);
        }
    }

    @Override
    public long countSearchPublicGrounds(String keyword, String sportType, String location,
            Double minPrice, Double maxPrice) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<GroundEntity> ground = cq.from(GroundEntity.class);

            List<Predicate> predicates = buildPredicates(cb, ground, keyword, sportType, location, minPrice, maxPrice);

            cq.select(cb.count(ground));
            cq.where(predicates.toArray(new Predicate[0]));

            return em.createQuery(cq).getSingleResult();

        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count search results", ex);
        }
    }

    // ============ PRIVATE HELPER METHODS ============

    private List<GroundEntity> searchWithCriteriaAPI(String keyword, String sportType, String location,
            Double minPrice, Double maxPrice, int offset, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GroundEntity> cq = cb.createQuery(GroundEntity.class);
        Root<GroundEntity> ground = cq.from(GroundEntity.class);

        // Build dynamic predicates
        List<Predicate> predicates = buildPredicates(cb, ground, keyword, sportType, location, minPrice, maxPrice);

        // Add sorting by newest first
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(ground.get("createdAt")));

        return em.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<GroundEntity> ground,
            String keyword, String sportType, String location,
            Double minPrice, Double maxPrice) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isTrue(ground.get("isAvailable")));

        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(ground.get("nameGround")), searchPattern);
            Predicate locationPredicate = cb.like(cb.lower(ground.get("location")), searchPattern);
            predicates.add(cb.or(namePredicate, locationPredicate));
        }

        if (location != null && !location.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(ground.get("location")), "%" + location.toLowerCase() + "%"));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(ground.get("pricePerHour"), minPrice.longValue()));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(ground.get("pricePerHour"), maxPrice.longValue()));
        }

        return predicates;
    }

    private String validateSortBy(String sortBy) {
        List<String> allowedSortFields = List.of("createdAt", "nameGround", "pricePerHour", "location");
        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            return "createdAt"; // default
        }

        if ("nameGround".equals(sortBy)) {
            return "nameGround";
        }
        return sortBy;
    }

    private String validateOrder(String order) {
        if (order == null || (!order.equalsIgnoreCase("ASC") && !order.equalsIgnoreCase("DESC"))) {
            return "DESC";
        }
        return order.toUpperCase();
    }
}