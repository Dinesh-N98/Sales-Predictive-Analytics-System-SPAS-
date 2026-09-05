package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lk.spas.backend.dto.AchievementVsTargetCandidate;
import lk.spas.backend.dto.AchievementVsTargetDto;
import lk.spas.backend.repository.AchievementVsTargetRepository;

@ApplicationScoped
public class AchievementVsTargetService {

    @Inject
    private AchievementVsTargetRepository repository;

    public List<AchievementVsTargetDto> getAchievementVsTarget(Integer seId,
            String startMonth, String endMonth) {
        LocalDate parsedStartMonth = parseMonth(startMonth, "startMonth");
        LocalDate parsedEndMonth = parseMonth(endMonth, "endMonth");

        if (parsedStartMonth != null && parsedEndMonth != null && parsedStartMonth.isAfter(parsedEndMonth)) {
            throw new IllegalArgumentException("startMonth must be before or equal to endMonth");
        }

        return repository.findAchievementVsTarget(seId, parsedStartMonth, parsedEndMonth)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private LocalDate parseMonth(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyy-MM")).atDay(1);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must use yyyy-MM format");
        }
    }

    private AchievementVsTargetDto toDto(AchievementVsTargetCandidate candidate) {
        BigDecimal targetAmount = candidate.getTargetAmount() == null ? BigDecimal.ZERO : candidate.getTargetAmount();
        BigDecimal achievedAmount = candidate.getAchievedAmount() == null ? BigDecimal.ZERO : candidate.getAchievedAmount();
        double achievementPercentage = targetAmount.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : achievedAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();

        return new AchievementVsTargetDto(
                candidate.getSeId(),
                candidate.getSeName(),
                candidate.getSeLevelName(),
                YearMonth.from(candidate.getMonth()),
                targetAmount,
                achievedAmount,
                achievementPercentage);
    }
}
