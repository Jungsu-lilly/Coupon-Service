package com.example.couponapi.controller;

import com.example.couponapi.dto.CouponIssueRequestDto;
import com.example.couponapi.service.CouponIssueRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/issue")
    public ResponseEntity<Void> issueCoupon(@RequestBody CouponIssueRequestDto issueRequest) {
        couponIssueRequestService.issueRequest(issueRequest);
        return ResponseEntity.ok().build();
    }
}
