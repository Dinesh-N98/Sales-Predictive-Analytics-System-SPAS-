package lk.spas.manager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesTrendDto {
    private LocalDate date;
    private Integer salesCount;
    private BigDecimal totalAmount;

    public SalesTrendDto() {
    }

    public SalesTrendDto(LocalDate date, Integer salesCount, BigDecimal totalAmount) {
        this.date = date;
        this.salesCount = salesCount;
        this.totalAmount = totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
