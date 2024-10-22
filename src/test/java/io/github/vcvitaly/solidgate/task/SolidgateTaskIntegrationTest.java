package io.github.vcvitaly.solidgate.task;

import io.github.vcvitaly.solidgate.task.dto.BalanceUpdateRequestDto;
import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql({"/test-data.sql"})
class SolidgateTaskIntegrationTest {

	@Container
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

	@LocalServerPort
	private int serverPort;

	private RestClient restClient;

	@BeforeEach
	void setUp() {
		restClient = RestClient.builder()
				.baseUrl("http://localhost:%d/api/v1".formatted(serverPort))
				.build();
	}

	@Test
	void test() {
		final ResponseEntity<Void> createdResp = restClient.post()
				.uri("/balance-update/{idempotencyKey}/set-users-balance", UUID.randomUUID().toString())
				.body(Map.of(1, 100))
				.retrieve()
				.toBodilessEntity();

		assertThat(createdResp.getStatusCode().is2xxSuccessful()).isTrue();

		final ResponseEntity<List<BalanceUpdateRequestDto>> resp = restClient.get()
				.uri("/balance-update/in-progress-requests")
				.retrieve()
				.toEntity(new ParameterizedTypeReference<>() {});

		assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(resp.getBody()).isNotNull();
		final List<BalanceUpdateRequestDto> balanceUpdateRequestDtos = resp.getBody();
		assertThat(balanceUpdateRequestDtos.getFirst().status()).isEqualTo(BalanceUpdateRequestStatus.NEW);
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
	}
}
