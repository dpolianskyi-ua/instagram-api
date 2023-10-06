package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.dto.AccommodationDTO;
import com.example.mockmvcintframework.testsupport.MockMvcTestBase;
import com.example.mockmvcintframework.testsupport.MvcRequest;
import com.example.mockmvcintframework.testsupport.StringResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccommodationControllerIntegrationTest extends MockMvcTestBase {

    @BeforeEach
    void setUp() {
        getResultWithStatus(
                MvcRequest.builder().httpDelete("/api/v1/accommodations").build(), 204);
    }

    @Nested
    @DisplayName("GIVEN: create(...) is called")
    class CreateUseCases {
        @Test
        @DisplayName("WHEN: DTO is provided, THEN: returns 'CREATED' response")
        void create_whenDtoIsProvided_returnsCreatedResponse(
                @StringResource("mock/accommodation/create-request_CREATED.json") String expected) {
            var dto = AccommodationDTO.builder()
                    .addressId(111L)
                    .personId(555L)
                    .accommodationDate(currentTimeProvider.getLocalDate())
                    .isSingleOwned(true)
                    .build();

            var request =
                    MvcRequest.builder().httpPost("/api/v1/accommodations", dto).build();

            var actual = getResultWithStatus(request, 201);

            assertJsonEquals(expected, actual);
        }

        @Test
        @DisplayName("WHEN: DTO is NOT provided, THEN: returns BadRequest")
        void create_whenDtoIsNotProvided_returnsBadRequest() {
            var request = MvcRequest.builder()
                    .httpPost("/api/v1/accommodations", null)
                    .build();

            var actual = getResultWithStatus(request, 400);

            assertTrue(isBlank(actual));
        }
    }

    @Nested
    @DisplayName("GIVEN: getById(...) is called")
    class GetByIdUseCases {
        @Test
        @DisplayName("WHEN: id is provided AND entity is found, THEN: returns 'OK' response")
        void getById_whenEntityIsFound_returnsOkResponse(
                @StringResource("mock/accommodation/get-by-id-request_OK.json") String expected) {
            saveEntity();

            var request =
                    MvcRequest.builder().httpGet("/api/v1/accommodations/1").build();

            var actual = getResultWithStatus(request, 200);

            assertJsonEquals(expected, actual);
        }

        @Test
        @DisplayName("WHEN: id is provided AND entity is NOT found, THEN: returns 'NOT FOUND' response")
        void getById_whenEntityIsNotFound_returnsNotFoundResponse() {
            var request =
                    MvcRequest.builder().httpGet("/api/v1/accommodations/100").build();

            var actual = getResultWithStatus(request, 404);

            assertTrue(isBlank(actual));
        }
    }

    @Nested
    @DisplayName("GIVEN: findAll(...) is called")
    class FindAllUseCases {
        @Test
        @DisplayName("WHEN: entities are found, THEN: returns 'OK' response")
        void findAll_whenEntitiesAreFound_returnsOkResponse(
                @StringResource("mock/accommodation/find-all-request_OK.json") String expected) {
            saveEntity();

            var request = MvcRequest.builder().httpGet("/api/v1/accommodations").build();

            var actual = getResultWithStatus(request, 200);

            assertJsonEquals(expected, actual);
        }

        @Test
        @DisplayName("WHEN: entities are NOT found, THEN: returns 'OK' response with empty array")
        void findAll_whenEntitiesAreNotFound_returnsNotFoundResponse() {
            var request = MvcRequest.builder().httpGet("/api/v1/accommodations").build();

            var actual = getResultWithStatus(request, 200);

            assertJsonEquals("[]", actual);
        }
    }

    @Nested
    @DisplayName("GIVEN: update(...) is called")
    class UpdateUseCases {
        @Test
        @DisplayName("WHEN: id and DTO are provided, THEN: returns 'OK' response")
        void update_whenIdAndDtoAreProvided_returnsOkResponse(
                @StringResource("mock/accommodation/update-request_OK.json") String expected) {
            saveEntity();

            var dto = AccommodationDTO.builder()
                    .addressId(222L)
                    .personId(333L)
                    .accommodationDate(LocalDate.of(2022, 1, 1))
                    .isSingleOwned(false)
                    .build();

            var request = MvcRequest.builder()
                    .httpPut("/api/v1/accommodations/1", dto)
                    .build();

            var actual = getResultWithStatus(request, 200);

            assertJsonEquals(expected, actual);
        }

        @Test
        @DisplayName("WHEN: DTO is NOT provided, THEN: returns 'UNPROCESSABLE ENTITY' response")
        void update_whenIdAndDtoAreNotProvided_returnsUnprocessableEntityResponse() {
            saveEntity();

            var request = MvcRequest.builder()
                    .httpPut("/api/v1/accommodations/1", null)
                    .build();

            var actual = getResultWithStatus(request, 400);

            assertTrue(isBlank(actual));
        }
    }

    @Nested
    @DisplayName("GIVEN: delete(...) is called")
    class DeleteUseCases {
        @Test
        @DisplayName("WHEN: id is provided, THEN: returns 'NO CONTENT' response")
        void delete_returnsNoContentResponse() {
            var request =
                    MvcRequest.builder().httpDelete("/api/v1/accommodations/1").build();

            var actual = getResultWithStatus(request, 204);

            assertTrue(isBlank(actual));
        }
    }

    @Nested
    @DisplayName("GIVEN: deleteAll() is called")
    class DeleteAllUseCases {
        @Test
        @DisplayName("THEN: returns 'NO CONTENT' response")
        void delete_returnsNoContentResponse() {
            var request =
                    MvcRequest.builder().httpDelete("/api/v1/accommodations").build();

            var actual = getResultWithStatus(request, 204);

            assertTrue(isBlank(actual));
        }
    }

    private void saveEntity() {
        var dto = AccommodationDTO.builder()
                .addressId(111L)
                .personId(555L)
                .accommodationDate(currentTimeProvider.getLocalDate())
                .isSingleOwned(true)
                .build();

        var request =
                MvcRequest.builder().httpPost("/api/v1/accommodations", dto).build();

        getResultWithStatus(request, 201);
    }
}