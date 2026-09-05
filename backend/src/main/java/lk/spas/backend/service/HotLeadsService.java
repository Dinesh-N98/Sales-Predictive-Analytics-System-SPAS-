package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lk.spas.backend.dto.HotLeadCandidate;
import lk.spas.backend.dto.HotLeadDto;
import lk.spas.backend.ml.ActivityOutcomeRequestItem;
import lk.spas.backend.ml.ActivityOutcomeResult;
import lk.spas.backend.ml.MlServiceClient;
import lk.spas.backend.repository.HotLeadsRepository;
import lk.spas.backend.util.ActivityDateUtil;

@ApplicationScoped
public class HotLeadsService {

    @Inject
    private HotLeadsRepository hotLeadsRepository;

    public List<HotLeadDto> getHotLeads(int limit) {
        List<HotLeadCandidate> candidates = hotLeadsRepository.findPendingHotLeads();
        if (candidates.isEmpty()) {
            return List.of();
        }

        List<ActivityOutcomeRequestItem> requestItems = new ArrayList<>();
        for (HotLeadCandidate candidate : candidates) {
            ActivityOutcomeRequestItem item = new ActivityOutcomeRequestItem();
            item.setId(candidate.getId());
            item.setSeLevelName(candidate.getSeLevelName());
            item.setActivityName(candidate.getActivityName());
            item.setDurationMinutes(candidate.getDurationMinutes() == null ? 0 : candidate.getDurationMinutes());
            item.setFollowupCount(candidate.getFollowupCount() == null ? 0 : candidate.getFollowupCount());
            item.setClientTypeName(candidate.getClientTypeName());
            item.setFinancialLevelName(candidate.getFinancialLevelName());
            item.setLeadSourceName(candidate.getLeadSourceName());
            item.setPolicyName(candidate.getPolicyName());
            item.setHasFeedback(candidate.getHasFeedback() == null ? 0 : candidate.getHasFeedback());
            item.setRating(candidate.getRating() == null ? null : candidate.getRating().doubleValue());
            item.setStrengthName(candidate.getStrengthName());
            item.setImprovementName(candidate.getImprovementName());
            item.setRunningAchieved(candidate.getRunningAchieved() == null ? 0.0 : candidate.getRunningAchieved());
            item.setHasAchievedTarget(candidate.getHasAchievedTarget() == null ? 0 : candidate.getHasAchievedTarget());
            item.setDayOfWeek(ActivityDateUtil.formatDayOfWeek(candidate.getActivityDate()));
            requestItems.add(item);
        }

        List<ActivityOutcomeResult> results = MlServiceClient.predictActivityOutcome(requestItems);
        results.sort((left, right) -> Double.compare(probabilitySold(right), probabilitySold(left)));

        int cappedLimit = Math.min(Math.max(limit, 1), 50);
        List<ActivityOutcomeResult> topResults = results.subList(0, Math.min(cappedLimit, results.size()));

        Map<Integer, HotLeadCandidate> candidateById = new HashMap<>();
        for (HotLeadCandidate candidate : candidates) {
            if (candidate.getId() != null) {
                candidateById.put(candidate.getId(), candidate);
            }
        }

        List<HotLeadDto> hotLeads = new ArrayList<>();
        for (ActivityOutcomeResult result : topResults) {
            HotLeadCandidate candidate = candidateById.get(result.getId());
            if (candidate == null) {
                continue;
            }
            Double probabilitySold = result.getProbabilities() == null ? 0.0 : result.getProbabilities().getOrDefault("Sold", 0.0);
            hotLeads.add(new HotLeadDto(
                    candidate.getId(),
                    candidate.getSeId(),
                    candidate.getSeName(),
                    candidate.getClientId(),
                    candidate.getActivityName(),
                    result.getPrediction(),
                    probabilitySold));
        }

        return hotLeads;
    }

    private double probabilitySold(ActivityOutcomeResult result) {
        if (result == null || result.getProbabilities() == null) {
            return 0.0;
        }
        return result.getProbabilities().getOrDefault("Sold", 0.0);
    }

}
