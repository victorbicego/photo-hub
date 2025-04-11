package com.event_manager.photo_hub.config.initializate_data;

import com.event_manager.photo_hub.models.entities.Host;
import com.event_manager.photo_hub.repositories.HostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminInitializationService {

  private static final String HOST_1_USERNAME = "host1@host1.com";
  private static final String HOST_1_PASSWORD = "host1@host1.com";

  private static final String HOST_2_USERNAME = "host2@host2.com";
  private static final String HOST_2_PASSWORD = "host2@host2.com";

  private static final String HOST_3_USERNAME = "host3@host3.com";
  private static final String HOST_3_PASSWORD = "host3@host3.com";

  private final PasswordEncoder passwordEncoder;
  private final HostRepository hostRepository;

  @PostConstruct
  public void initializeDefaultData() {
    initializeHosts();
  }

  private void initializeHosts() {
    if (!hostRepository.existsByUsername(HOST_1_USERNAME)) {
      Host host1 = createHost(HOST_1_USERNAME, HOST_1_PASSWORD);
      hostRepository.save(host1);
    }

    if (!hostRepository.existsByUsername(HOST_2_USERNAME)) {
      Host host2 = createHost(HOST_2_USERNAME, HOST_2_PASSWORD);
      hostRepository.save(host2);
    }

    if (!hostRepository.existsByUsername(HOST_3_USERNAME)) {
      Host host3 = createHost(HOST_3_USERNAME, HOST_3_PASSWORD);
      hostRepository.save(host3);
    }
  }

  private Host createHost(String hostUsername, String hostPassword) {
    Host host = new Host();
    host.setUsername(hostUsername);
    host.setPassword(passwordEncoder.encode(hostPassword));
    host.setFirstName("Host");
    host.setLastName("Host");
    host.setEnabled(true);
    return host;
  }
}
