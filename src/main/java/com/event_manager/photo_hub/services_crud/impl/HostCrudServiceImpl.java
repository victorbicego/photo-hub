package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.repositories.HostRepository;
import com.event_manager.photo_hub.services_crud.HostCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostCrudServiceImpl implements HostCrudService {

  private final HostRepository hostRepository;

  @Override
  public Host findById(Long id) throws NotFoundException {
    return hostRepository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("No host found with id: '%d'.", id)));
  }

  @Override
  public Host findByUsername(String username) throws NotFoundException {
    return hostRepository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("No host found with username: '%s'.", username)));
  }

  @Override
  public Host save(Host host) {
    return hostRepository.save(host);
  }

  @Override
  public Host updatePassword(Long id, String encodedPassword) throws NotFoundException {
    Host foundHost = findById(id);
    foundHost.setPassword(encodedPassword);
    return hostRepository.save(foundHost);
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    if (!hostRepository.existsById(id)) {
      throw new NotFoundException(String.format("No host found with id: '%d'.", id));
    }
    hostRepository.deleteById(id);
  }
}
