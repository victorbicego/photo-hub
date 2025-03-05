package com.event_manager.photo_hub.services_crud.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.event_manager.photo_hub.services_crud.GuestCrudService;

@Service
@RequiredArgsConstructor
public class GuestCrudServiceImpl implements GuestCrudService {

    private final ConsumerRepository consumerRepository;

    @Override
    public Page<Consumer> findAllByFilter(
            String search, Pageable pageable, LocalDate fromDate, LocalDate toDate, Gender gender) {
        return consumerRepository.findAllWithFiltersAndSearch(
                fromDate, toDate, gender, search, pageable);
    }

    @Override
    public Consumer findById(Long id) throws NotFoundException {
        return consumerRepository
                .findConsumerById(id)
                .orElseThrow(
                        () -> new NotFoundException(String.format("No consumer found with id: '%d'.", id)));
    }

    @Override
    public Consumer findByUsername(String username) throws NotFoundException {
        return consumerRepository
                .findByUsername(username)
                .orElseThrow(
                        () ->
                                new NotFoundException(
                                        String.format("No consumer found with username: '%s'.", username)));
    }

    @Override
    public Consumer findByPhone(String phone) throws NotFoundException {
        return consumerRepository
                .findByPhone(phone)
                .orElseThrow(
                        () ->
                                new NotFoundException(String.format("No consumer found with phone: '%s'.", phone)));
    }

    @Override
    public Consumer save(Consumer consumer) {
        return consumerRepository.save(consumer);
    }

    @Override
    public Consumer updatePassword(Long id, String encodedPassword) throws NotFoundException {
        Consumer foundConsumer = findById(id);
        foundConsumer.setPassword(encodedPassword);
        return consumerRepository.save(foundConsumer);
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        if (!consumerRepository.existsById(id)) {
            throw new NotFoundException(String.format("No consumer found with id: '%d'.", id));
        }
        consumerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return consumerRepository.existsById(id);
    }
}
