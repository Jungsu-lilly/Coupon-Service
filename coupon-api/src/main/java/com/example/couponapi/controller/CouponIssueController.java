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

    @PostMapping("/issue/mysql")
    public ResponseEntity<Void> issueCouponV1(@RequestBody CouponIssueRequestDto issueRequest) {
        couponIssueRequestService.issueCouponV1(issueRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/issue/async/v1")
    public ResponseEntity<Void> asyncIssueV1(@RequestBody CouponIssueRequestDto issueRequest) {
        couponIssueRequestService.asyncIssueRequestV1(issueRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/issue/async/v2")
    public ResponseEntity<Void> asyncIssueV2(@RequestBody CouponIssueRequestDto issueRequest) {
        couponIssueRequestService.asyncIssueRequestV2(issueRequest);
        return ResponseEntity.ok().build();
    }
}
