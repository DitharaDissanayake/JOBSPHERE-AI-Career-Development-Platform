package com.jobsphere.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="course_payments",
       indexes = {
           @Index(name="idx_course_payment_user", columnList="user_id"),
           @Index(name="idx_course_payment_order", columnList="order_id")
       })
public class CoursePayment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    @Column(name="user_id", nullable=false)
    public Long userId;

    @Column(name="course_id", nullable=false)
    public Long courseId;

    @Column(name="order_id", nullable=false, unique=true, length=100)
    public String orderId;

    @Column(nullable=false, precision=10, scale=2)
    public BigDecimal amount;

    @Column(nullable=false, length=3)
    public String currency="LKR";

    @Column(nullable=false, length=30)
    public String status="PENDING";

    @Column(nullable=false, length=30)
    public String gateway="DEMO";

    @Column(name="payment_id", length=120)
    public String paymentId;

    @Column(name="payment_method", length=40)
    public String paymentMethod;

    @Column(name="card_brand", length=30)
    public String cardBrand;

    @Column(name="card_last4", length=4)
    public String cardLast4;

    @Column(name="invoice_file_name", length=255)
    public String invoiceFileName;

    @Lob @Column(name="invoice_data", columnDefinition="LONGBLOB")
    public byte[] invoiceData;

    @Column(name="email_sent", nullable=false)
    public Boolean emailSent=false;

    @Column(name="created_at", nullable=false)
    public LocalDateTime createdAt;

    @Column(name="paid_at")
    public LocalDateTime paidAt;
}
