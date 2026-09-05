package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import lk.spas.backend.dto.SePerformanceCandidate;
import lk.spas.backend.dto.SePerformanceDto;
import lk.spas.backend.repository.SePerformanceRepository;

@ApplicationScoped
public class SePerformanceService {

    @Inject
    private SePerformanceRepository repository;

    public List<SePerformanceDto> getPerformance(YearMonth month) {
        List<SePerformanceCandidate> candidates = repository.findActiveSePerformanceCandidates(month.atDay(1));

        return candidates.stream()
                .map(this::toDto)
                .sorted(Comparator.comparingDouble(SePerformanceDto::getAchievementPercentage).reversed())
                .toList();
    }

    private SePerformanceDto toDto(SePerformanceCandidate candidate) {
        BigDecimal targetAmount = candidate.getTargetAmount() == null ? BigDecimal.ZERO : candidate.getTargetAmount();
        BigDecimal achievedAmount = candidate.getAchievedAmount() == null ? BigDecimal.ZERO : candidate.getAchievedAmount();
        double achievementPercentage = targetAmount.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : achievedAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();

        return new SePerformanceDto(
                candidate.getSeId(),
                candidate.getSeName(),
                candidate.getSeLevelName(),
                candidate.getSalesCount(),
                candidate.getTotalSalesAmount() == null ? BigDecimal.ZERO : candidate.getTotalSalesAmount(),
                achievedAmount,
                targetAmount,
                achievementPercentage);
    }
}
