package com.example.couponcore.model;

import com.example.couponcore.exception.custom.CouponIssueException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_DATE;
import static com.example.couponcore.exception.ErrorCode.INVALID_COUPON_QUANTITY;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType type;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountCost;

    @Column(nullable = false)
    private int minimumCost;

    @Column(nullable = false)
    private LocalDateTime issueStartDate;

    @Column(nullable = false)
    private LocalDateTime issueEndDate;

    public boolean verifyIssueQuantity() {
        if (totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;
    }

    public boolean verifyIssueDate() {
        LocalDateTime now = LocalDateTime.now();
        return issueStartDate.isBefore(now) && issueEndDate.isAfter(now);
    }

    public boolean isIssueComplete() {
        LocalDateTime now = LocalDateTime.now();
        return issueEndDate.isBefore(now) || !verifyIssueQuantity();
    }

    public void issue() {
        if (!verifyIssueQuantity()) {
            throw new CouponIssueException(INVALID_COUPON_QUANTITY, "쿠폰 발급 불가. 발급된 수 : %s".formatted(issuedQuantity));
        }
        if (!verifyIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_DATE, "발급 가능 기간이 아닙니다.");
        }
        this.issuedQuantity++;
    }
}
