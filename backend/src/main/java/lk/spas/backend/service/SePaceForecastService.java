package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lk.spas.backend.dto.SePaceCandidate;
import lk.spas.backend.dto.SePaceForecastDto;
import lk.spas.backend.ml.MlServiceClient;
import lk.spas.backend.ml.SeTargetForecastRequestItem;
import lk.spas.backend.ml.SeTargetForecastResult;
import lk.spas.backend.repository.SePaceForecastRepository;

@ApplicationScoped
public class SePaceForecastService {

    @Inject
    private SePaceForecastRepository repository;

    public List<SePaceForecastDto> getForecast(YearMonth month) {
        List<SePaceCandidate> candidates = repository.findActiveSePaceCandidates(month.atDay(1));
        if (candidates.isEmpty()) {
            return List.of();
        }

        double daysInMonth = month.lengthOfMonth();
        List<SeTargetForecastRequestItem> requestItems = new ArrayList<>();
        for (SePaceCandidate candidate : candidates) {
            SeTargetForecastRequestItem item = new SeTargetForecastRequestItem();
            item.setId(candidate.getSeId());
            item.setAvgActivityPerDay(candidate.getActivityCount() / daysInMonth);
            item.setAvgFollowupCount(candidate.getAvgFollowupCount());
            item.setAvgDurationMinutes(candidate.getAvgDurationMinutes());
            item.setSoldRate(candidate.getSoldRate());
            item.setSeLevelName(candidate.getSeLevelName());
            requestItems.add(item);
        }

        List<SeTargetForecastResult> results = MlServiceClient.predictSeTargetForecast(requestItems);
        results.sort(Comparator.comparingDouble(SeTargetForecastResult::getProbabilityHitTarget));

        Map<Integer, SePaceCandidate> candidateById = new HashMap<>();
        for (SePaceCandidate candidate : candidates) {
            candidateById.put(candidate.getSeId(), candidate);
        }

        List<SePaceForecastDto> forecasts = new ArrayList<>();
        for (SeTargetForecastResult result : results) {
            SePaceCandidate candidate = candidateById.get(result.getId());
            if (candidate == null) {
                continue;
            }
            forecasts.add(new SePaceForecastDto(
                    candidate.getSeId(), candidate.getSeName(), candidate.getSeLevelName(),
                    candidate.getTargetAmount(), candidate.getAchievedAmount(),
                    result.getPrediction(), result.getProbabilityHitTarget()));
        }
        return forecasts;
    }
}