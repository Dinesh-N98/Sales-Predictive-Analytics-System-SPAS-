package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lk.spas.backend.dto.SalesTrendDto;
import lk.spas.backend.repository.SalesTrendsRepository;

@ApplicationScoped
public class SalesTrendsService {

    @Inject
    private SalesTrendsRepository repository;

    public List<SalesTrendDto> getSalesTrends(Integer seId, Integer seLevelId,
            String startDate, String endDate) {
        LocalDate parsedStartDate = parseDate(startDate, "startDate");
        LocalDate parsedEndDate = parseDate(endDate, "endDate");

        if (parsedStartDate != null && parsedEndDate != null && parsedStartDate.isAfter(parsedEndDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }

        return repository.findSalesTrends(seId, seLevelId, parsedStartDate, parsedEndDate);
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must use yyyy-MM-dd format");
        }
    }
}
