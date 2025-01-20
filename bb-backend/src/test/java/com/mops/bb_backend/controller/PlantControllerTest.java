package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.PlantDetailsDto;
import com.mops.bb_backend.dto.PlantPaginationDto;
import com.mops.bb_backend.dto.PlantRegistrationDto;
import com.mops.bb_backend.dto.PlantUpdateDto;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.ActionType;
import com.mops.bb_backend.service.PlantService;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestTester requestTester;

    @MockBean
    private PlantService plantService;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        account = requestTester.createTestAccount();
        requestTester.authenticateAccount();
    }

    @AfterEach
    public void tearDown() {
        requestTester.cleanupTestAccount();
    }

    @Test
    void addPlant_validRequest_createsPlant() throws Exception {
        var plantDto = new PlantRegistrationDto("Rose", "Rosa", "Rosaceae", "http://example.com/photo.jpg");

        mockMvc.perform(requestTester.authenticatedPost("/plants", plantDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Mockito.verify(plantService).addPlant(
                eq(plantDto.commonName()),
                eq(plantDto.scientificName()),
                eq(plantDto.family()),
                eq(plantDto.photoUrl()));
    }

    @Test
    void getPlantList_validRequest_returnsPlantPagination() throws Exception {
        ArrayList<PlantDetailsDto> plantList = new ArrayList<>();
        plantList.add(new PlantDetailsDto(UUID.randomUUID().toString(), "Rose", "Rosa", "Rosaceae",
                "http://example.com/photo.jpg", "2025-01-01", "Water daily", 10, "Abundent", "Acid", "20", false, null));
        var plantPaginationDto = new PlantPaginationDto(plantList, 10, 10, 1, 1, true);
        Mockito.when(plantService.getPlantList(1, 10, false)).thenReturn(plantPaginationDto);

        mockMvc.perform(requestTester.authenticatedGet("/plants").contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .param("isArchived", "false"))
                .andExpect(status().isOk());

        Mockito.verify(plantService).getPlantList(0, 10, false);
    }

    @Test
    void getPlantDetails_validId_returnsPlantDetails() throws Exception {
        var plantDetailsDto = new PlantDetailsDto("1", "Rose", "Rosa", "Rosaceae",
                "http://example.com/photo.jpg", "2025-01-01", "Water daily", 10, "Abundent", "Acid", "20", false, null);
        Mockito.when(plantService.getPlantDetails("1")).thenReturn(plantDetailsDto);

        mockMvc.perform(requestTester.authenticatedGet("/plants/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new JSONObject()
                        .put("id", plantDetailsDto.id())
                        .put("commonName", plantDetailsDto.commonName())
                        .put("scientificName", plantDetailsDto.scientificName())
                        .put("family", plantDetailsDto.family())
                        .put("photoUrl", plantDetailsDto.photoUrl())
                        .toString()));

        Mockito.verify(plantService).getPlantDetails("1");
    }

    @Test
    void updatePlantDetails_validRequest_updatesPlant() throws Exception {
        var plantUpdateDto = new PlantUpdateDto(ActionType.WATER);

        mockMvc.perform(requestTester.authenticatedPatch("/plants/1", plantUpdateDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(plantService).updatePlantDetails("1", plantUpdateDto.actionType());
    }
}
