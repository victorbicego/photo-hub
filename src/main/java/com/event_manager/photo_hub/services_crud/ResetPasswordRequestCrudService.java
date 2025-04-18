package com.event_manager.photo_hub.services_crud;

import com.event_manager.photo_hub.models.entities.ResetPasswordRequest;
import java.util.Optional;

public interface ResetPasswordRequestCrudService {

  ResetPasswordRequest findResetPasswordByCodeAndUsername(String code, String username);

  Optional<ResetPasswordRequest> findResetPasswordByUsername(String username);

  ResetPasswordRequest save(ResetPasswordRequest resetPasswordRequest);

  void delete(Long id);
}
