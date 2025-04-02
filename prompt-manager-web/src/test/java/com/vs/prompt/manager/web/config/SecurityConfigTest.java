package com.vs.prompt.manager.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(SecurityConfig.class)
                    .withPropertyValues("spring.profiles.active=dev");

    @Test
    void testAdminUserExists() {
        contextRunner.run(context -> {
            var uds = context.getBean(UserDetailsService.class);
            var user = uds.loadUserByUsername("admin");

            assertThat(user).isNotNull();
            assertThat(user.getUsername()).isEqualTo("admin");
            assertThat(user.getPassword()).isEqualTo("{noop}admin123");
        });
    }
}
