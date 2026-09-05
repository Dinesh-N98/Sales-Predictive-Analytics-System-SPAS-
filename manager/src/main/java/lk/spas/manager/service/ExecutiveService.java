package lk.spas.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.spas.manager.model.CreateExecutiveRequest;
import lk.spas.manager.model.SalesExecutive;
import lk.spas.manager.model.SeLevel;
import java.util.Arrays;
import java.util.List;

public class ExecutiveService {
    private final ApiHttpClient apiClient = new ApiHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<SalesExecutive> getExecutives(int page, int size) throws Exception {
        String response = apiClient.getAuthenticated(
                "/sales-executives?page=" + page + "&size=" + size, 200);
        return Arrays.asList(mapper.readValue(response, SalesExecutive[].class));
    }

    public List<SeLevel> getSeLevels() throws Exception {
        String response = apiClient.getAuthenticated("/se-levels", 200);
        return Arrays.asList(mapper.readValue(response, SeLevel[].class));
    }

    public SalesExecutive createExecutive(CreateExecutiveRequest requestBody) throws Exception {
        String response = apiClient.postJson("/sales-executives", requestBody, 201);
        return mapper.readValue(response, SalesExecutive.class);
    }

    public SalesExecutive updateExecutive(int id, CreateExecutiveRequest requestBody) throws Exception {
        String response = apiClient.putJson("/sales-executives/" + id, requestBody, 200);
        return mapper.readValue(response, SalesExecutive.class);
    }

    public void deleteExecutive(int id) throws Exception {
        apiClient.delete("/sales-executives/" + id, 204);
    }
}