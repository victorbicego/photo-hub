package com.event_manager.photo_hub.services_crud.impl;

import com.event_manager.photo_hub.exceptions.NotFoundException;
import com.event_manager.photo_hub.models.entities.ResetPasswordRequest;
import com.event_manager.photo_hub.repositories.ResetPasswordRequestRepository;
import com.event_manager.photo_hub.services_crud.ResetPasswordRequestCrudService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordRequestCrudServiceImpl implements ResetPasswordRequestCrudService {

  private final ResetPasswordRequestRepository resetPasswordRequestRepository;

  @Override
  public ResetPasswordRequest findResetPasswordByCodeAndUsername(String code, String username) {
    return resetPasswordRequestRepository
        .findByCodeAndUsername(code, username)
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format(
                        "No reset password found with code: '%s' and username: '%s'.",
                        code, username)));
  }

  @Override
  public Optional<ResetPasswordRequest> findResetPasswordByUsername(String username) {
    return resetPasswordRequestRepository.findByUsername(username);
  }

  @Override
  public ResetPasswordRequest save(ResetPasswordRequest resetPasswordRequest) {
    return resetPasswordRequestRepository.save(resetPasswordRequest);
  }

  @Override
  public void delete(Long id) {
    if (!resetPasswordRequestRepository.existsById(id)) {
      throw new NotFoundException(String.format("No reset password found with id: '%s'.", id));
    }
    resetPasswordRequestRepository.deleteById(id);
  }
}
