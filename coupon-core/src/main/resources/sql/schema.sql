CREATE TABLE `coupon`.`coupon`
(
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `name`              varchar(50) NOT NULL COMMENT '쿠폰명',
    `type`              varchar(50) NOT NULL COMMENT '쿠폰 타입 (선착순, ..)',
    `total_quantity`    int NULL COMMENT '발급 최대 수량 (MAX)',
    `issued_quantity`   int NOT NULL COMMENT '발급된 수량',
    `discount_price`     int NOT NULL COMMENT '할인 금액',
    `minimum_price`      int NOT NULL COMMENT '최소 사용 금액',
    `issue_start_date`  datetime(6) NOT NULL COMMENT '발급 시작 일시',
    `issue_end_date`    datetime(6) NOT NULL COMMENT '발급 종료 일시',
    `created_at`        datetime(6) NOT NULL COMMENT '생성 일시',
    `updated_at`        datetime(6) NOT NULL COMMENT '수정 일시',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `coupon`.`coupon_issuance`
(
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `coupon_id`         bigint NOT NULL COMMENT '쿠폰 ID',
    `user_id`           bigint NOT NULL COMMENT '유저 ID',
    `date_issued`       datetime(6) NOT NULL COMMENT '발급된 일시',
    `date_used`         datetime(6) NOT NULL COMMENT '사용된 일시',
    `created_at`        datetime(6) NOT NULL COMMENT '생성 일시',
    `updated_at`        datetime(6) NOT NULL COMMENT '수정 일시',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;