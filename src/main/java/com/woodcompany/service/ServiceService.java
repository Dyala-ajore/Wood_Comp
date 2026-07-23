package com.woodcompany.service;

import com.woodcompany.dto.service.CreateServiceRequest;
import com.woodcompany.dto.service.ServiceResponse;
import com.woodcompany.dto.service.UpdateServiceRequest;
import com.woodcompany.entity.Service;
import com.woodcompany.exception.ResourceNotFoundException;
import com.woodcompany.mapper.ServiceMapper;
import com.woodcompany.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;


    // ---------- CREATE ----------
    @Transactional
    public ServiceResponse createService(CreateServiceRequest request) {
        com.woodcompany.entity.Service service = com.woodcompany.entity.Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        com.woodcompany.entity.Service saved = serviceRepository.save(service);
        return serviceMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public ServiceResponse getServiceById(Long id) {
        return serviceMapper.toResponse(findServiceOrThrow(id));
    }

    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE (partial) ----------
    @Transactional
    public ServiceResponse updateService(Long id, UpdateServiceRequest request) {
        com.woodcompany.entity.Service service = findServiceOrThrow(id);

        if (request.getName() != null) {
            service.setName(request.getName());
        }
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            service.setPrice(request.getPrice());
        }

        // managed entity - dirty checking بيتكفل بالحفظ، بدون save() صريح
        return serviceMapper.toResponse(service);
    }

    // ---------- DELETE ----------
    @Transactional
    public void deleteService(Long id) {
        serviceRepository.delete(findServiceOrThrow(id));
    }

    public boolean exists(Long id) {
        return serviceRepository.existsById(id);
    }

    // ---------- Helper ----------
    private com.woodcompany.entity.Service findServiceOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }

    public Service getServiceEntityOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}