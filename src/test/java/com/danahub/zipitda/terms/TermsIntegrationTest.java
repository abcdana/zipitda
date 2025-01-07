package com.danahub.zipitda.terms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TermsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetTermsList() throws Exception {
        mockMvc.perform(get("/api/terms/list") // 약관 목록 가져오기
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getTermsByTitleAndVersion() throws Exception {
        mockMvc.perform(get("/api/terms/{title}/{version}", "개인정보 처리방침", 1) // 약관 상세 조회
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}