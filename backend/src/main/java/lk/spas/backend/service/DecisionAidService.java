package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.YearMonth;
import java.util.List;
import lk.spas.backend.dto.ActivityOutcomeDetailDto;
import lk.spas.backend.dto.HotLeadCandidate;
import lk.spas.backend.dto.SePaceCandidate;
import lk.spas.backend.dto.SeTargetForecastDetailDto;
import lk.spas.backend.ml.ActivityOutcomeRequestItem;
import lk.spas.backend.ml.ActivityOutcomeResult;
import lk.spas.backend.ml.MlServiceClient;
import lk.spas.backend.ml.SeTargetForecastRequestItem;
import lk.spas.backend.ml.SeTargetForecastResult;
import lk.spas.backend.repository.HotLeadsRepository;
import lk.spas.backend.repository.SePaceForecastRepository;
import lk.spas.backend.util.ActivityDateUtil;

@ApplicationScoped
public class DecisionAidService {
    @Inject private HotLeadsRepository hotLeadsRepository;
    @Inject private SePaceForecastRepository sePaceForecastRepository;

    public ActivityOutcomeDetailDto getActivityOutcome(Integer activityLogId) {
        HotLeadCandidate candidate = hotLeadsRepository.findPendingHotLead(activityLogId);
        if (candidate == null) {
            return null;
        }
        ActivityOutcomeRequestItem item = new ActivityOutcomeRequestItem();
        item.setId(candidate.getId());
        item.setSeLevelName(candidate.getSeLevelName());
        item.setActivityName(candidate.getActivityName());
        item.setDurationMinutes(value(candidate.getDurationMinutes()));
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
        List<ActivityOutcomeResult> results = MlServiceClient.predictActivityOutcome(List.of(item));
        if (results.isEmpty()) return null;
        ActivityOutcomeResult result = results.get(0);
        return new ActivityOutcomeDetailDto(candidate.getId(), candidate.getSeId(), candidate.getSeName(),
                candidate.getClientId(), candidate.getActivityName(), result.getPrediction(), probability(result, "Sold"),
                probability(result, "Pending"), probability(result, "Rejected"));
    }

    public SeTargetForecastDetailDto getSeTargetForecast(Integer seId, YearMonth month) {
        SePaceCandidate candidate = sePaceForecastRepository.findActiveSePaceCandidate(seId, month.atDay(1));
        if (candidate == null) return null;
        SeTargetForecastRequestItem item = new SeTargetForecastRequestItem();
        item.setId(candidate.getSeId());
        item.setAvgActivityPerDay(candidate.getActivityCount() / (double) month.lengthOfMonth());
        item.setAvgFollowupCount(candidate.getAvgFollowupCount());
        item.setAvgDurationMinutes(candidate.getAvgDurationMinutes());
        item.setSoldRate(candidate.getSoldRate());
        item.setSeLevelName(candidate.getSeLevelName());
        List<SeTargetForecastResult> results = MlServiceClient.predictSeTargetForecast(List.of(item));
        if (results.isEmpty()) return null;
        SeTargetForecastResult result = results.get(0);
        return new SeTargetForecastDetailDto(candidate.getSeId(), candidate.getSeName(), candidate.getSeLevelName(),
                candidate.getTargetAmount(), candidate.getAchievedAmount(), result.getPrediction(),
                result.getProbabilityHitTarget());
    }

    private double value(Integer value) { return value == null ? 0 : value; }
    private double probability(ActivityOutcomeResult result, String name) {
        return result.getProbabilities() == null ? 0.0 : result.getProbabilities().getOrDefault(name, 0.0);
    }
}