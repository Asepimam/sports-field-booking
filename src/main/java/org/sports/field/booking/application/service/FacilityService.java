package org.sports.field.booking.application.service;

import java.util.List;
import java.util.UUID;

import org.sports.field.booking.application.dto.FacilityDTO;

public interface FacilityService {
    FacilityDTO createFacility(String ownerEmail, UUID groundId, FacilityDTO request);

    FacilityDTO updateFacility(String ownerEmail, UUID groundId, UUID facilityId, FacilityDTO request);

    void deleteFacility(String ownerEmail, UUID groundId, UUID facilityId);

    List<FacilityDTO> getGroundFacilities(UUID groundId);
}
