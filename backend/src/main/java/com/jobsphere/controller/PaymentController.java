package com.jobsphere.controller;

import com.jobsphere.entity.Course;
import com.jobsphere.entity.CoursePayment;
import com.jobsphere.entity.User;
import com.jobsphere.repository.CoursePaymentRepository;
import com.jobsphere.repository.CourseRepository;
import com.jobsphere.repository.UserProfileRepository;
import com.jobsphere.repository.UserRepository;
import com.jobsphere.service.PaymentService;
import com.jobsphere.service.TokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends BaseController {

    private final CourseRepository courses;
    private final CoursePaymentRepository payments;
    private final UserProfileRepository profiles;
    private final PaymentService paymentService;

    @Value("${payhere.enabled:false}")
    boolean payhereEnabled;

    @Value("${payhere.merchant-id:}")
    String merchantId;

    @Value("${payhere.merchant-secret:}")
    String merchantSecret;

    @Value("${payhere.sandbox:true}")
    boolean sandbox;

    @Value("${app.frontend-url:http://localhost:5500}")
    String frontendUrl;

    @Value("${app.public-base-url:http://localhost:8080}")
    String publicBaseUrl;

    public PaymentController(
            TokenService t,
            UserRepository u,
            CourseRepository c,
            CoursePaymentRepository p,
            UserProfileRepository profiles,
            PaymentService paymentService
    ) {
        super(t, u);

        this.courses = c;
        this.payments = p;
        this.profiles = profiles;
        this.paymentService = paymentService;
    }

    // =========================================================
    // PAYMENT CONFIG
    // =========================================================

    @GetMapping("/config")
    public Map<String, Object> config(
            @RequestHeader("Authorization") String h
    ) {

        currentId(h);

        boolean realPayHereAvailable =
                payhereEnabled
                        && merchantId != null
                        && !merchantId.isBlank()
                        && merchantSecret != null
                        && !merchantSecret.isBlank();

        return Map.of(
                "payhereEnabled", realPayHereAvailable,
                "sandbox", sandbox,
                "price", PaymentService.ADVANCED_PRICE,
                "currency", "LKR"
        );
    }

    // =========================================================
    // PREPARE PAYMENT
    // =========================================================

    @GetMapping("/prepare/{courseId}")
    public Map<String, Object> prepare(
            @RequestHeader("Authorization") String h,
            @PathVariable Long courseId
    ) {

        Long uid = currentId(h);

        Course c = advancedCourse(courseId);

        // Already paid
        if (hasAccess(uid, courseId)) {

            return Map.of(
                    "alreadyPaid", true,
                    "courseId", courseId
            );
        }

        String gateway = payhereEnabled ? "PAYHERE" : "DEMO";

        CoursePayment p =
                paymentService.createPending(
                        uid,
                        courseId,
                        gateway
                );

        User user = currentUser(h);

        String[] name = splitName(user.fullName);

        Map<String, Object> out =
                new LinkedHashMap<>();

        out.put(
                "gateway",
                payhereEnabled ? "PAYHERE" : "DEMO"
        );

        out.put("orderId", p.orderId);
        out.put("amount", p.amount);
        out.put("currency", "LKR");

        // =====================================================
        // PAYHERE MODE
        // =====================================================

        if (payhereEnabled) {

            String amount =
                    p.amount
                            .setScale(2)
                            .toPlainString();

            String secretHash =
                    md5(merchantSecret)
                            .toUpperCase(Locale.ROOT);

            String hashSource =
                    merchantId
                            + p.orderId
                            + amount
                            + "LKR"
                            + secretHash;

            String hash =
                    md5(hashSource)
                            .toUpperCase(Locale.ROOT);

            String actionUrl =
                    sandbox
                            ? "https://sandbox.payhere.lk/pay/checkout"
                            : "https://www.payhere.lk/pay/checkout";

            out.put(
                    "actionUrl",
                    actionUrl
            );

            out.put(
                    "merchantId",
                    merchantId
            );

            out.put(
                    "hash",
                    hash
            );

            out.put(
                    "returnUrl",
                    frontendUrl
                            + "/course-payment.html?courseId="
                            + courseId
                            + "&payment=return"
            );

            out.put(
                    "cancelUrl",
                    frontendUrl
                            + "/course-payment.html?courseId="
                            + courseId
                            + "&payment=cancel"
            );

            out.put(
                    "notifyUrl",
                    publicBaseUrl
                            + "/api/payments/payhere/notify"
            );

            out.put(
                    "firstName",
                    name[0]
            );

            out.put(
                    "lastName",
                    name[1]
            );

            out.put(
                    "email",
                    user.email
            );

            String phone =
                    profiles
                            .findByUserId(uid)
                            .map(profile ->
                                    safePhone(profile.phone)
                            )
                            .orElse("0770000000");

            out.put(
                    "phone",
                    phone
            );

            out.put(
                    "address",
                    "JOBSPHERE Learner"
            );

            out.put(
                    "city",
                    "Sri Lanka"
            );

            out.put(
                    "country",
                    "Sri Lanka"
            );

            out.put(
                    "items",
                    c.name
            );
        }

        return out;
    }

    // =========================================================
    // DEMO PAYMENT
    // =========================================================

    @PostMapping("/demo")
    public Map<String, Object> demo(
            @RequestHeader("Authorization") String h,
            @RequestBody DemoRequest x
    ) throws Exception {

        Long uid = currentId(h);

        Course c =
                advancedCourse(x.courseId());

        // Already paid
        if (hasAccess(uid, c.id)) {

            return Map.of(
                    "success", true,
                    "alreadyPaid", true
            );
        }

        // Demo payment cannot be used when real PayHere
        // mode is enabled.
        if (payhereEnabled) {

            throw new RuntimeException(
                    "Real PayHere mode is enabled. "
                            + "Please use the secure PayHere checkout."
            );
        }

        /*
         * IMPORTANT
         *
         * Card holder name is intentionally NOT validated.
         *
         * Only:
         * 1. Card number
         * 2. Expiry
         * 3. CVV
         *
         * are validated.
         */
        validateCard(
                x.cardNumber(),
                x.expiry(),
                x.cvv()
        );

        if (x.orderId() == null
                || x.orderId().isBlank()) {

            throw new RuntimeException(
                    "Payment order ID is required."
            );
        }

        CoursePayment p =
                payments
                        .findByOrderId(x.orderId())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Payment session expired. "
                                                        + "Start again."
                                        )
                        );

        // Make sure payment belongs to logged-in user
        if (!uid.equals(p.userId)
                || !c.id.equals(p.courseId)) {

            throw new RuntimeException(
                    "Payment session does not match your account."
            );
        }

        // Payment can only be processed once
        if (!"PENDING".equalsIgnoreCase(p.status)) {

            throw new RuntimeException(
                    "This payment has already been processed."
            );
        }

        String digits =
                x.cardNumber()
                        .replaceAll("\\D", "");

        String cardBrand =
                brand(digits);

        String last4 =
                digits.substring(
                        digits.length() - 4
                );

        User user =
                currentUser(h);

        /*
         * IMPORTANT:
         *
         * Full card number and CVV are NOT passed
         * to PaymentService.
         *
         * Only card brand and last 4 digits are saved.
         */
        p =
                paymentService.markPaid(
                        p,
                        user,
                        c,
                        "DEMO-" + p.orderId,
                        "CARD - DEMO",
                        cardBrand,
                        last4
                );

        return response(p);
    }

    // =========================================================
    // PAYHERE NOTIFICATION
    // =========================================================

    @PostMapping(
            value = "/payhere/notify",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<String> notify(
            @RequestParam Map<String, String> form
    ) throws Exception {

        String merchant =
                form.get("merchant_id");

        String orderId =
                form.get("order_id");

        String amount =
                form.get("payhere_amount");

        String currency =
                form.get("payhere_currency");

        String status =
                form.get("status_code");

        String signature =
                form.get("md5sig");

        // =====================================================
        // REQUIRED DATA CHECK
        // =====================================================

        if (merchant == null
                || orderId == null
                || amount == null
                || currency == null
                || status == null
                || signature == null) {

            return ResponseEntity
                    .badRequest()
                    .body("missing");
        }

        // =====================================================
        // VERIFY MERCHANT
        // =====================================================

        if (merchantId == null
                || merchantId.isBlank()
                || !merchantId.equals(merchant)) {

            return ResponseEntity
                    .status(403)
                    .body("invalid_merchant");
        }

        // =====================================================
        // VERIFY PAYHERE SIGNATURE
        // =====================================================

        String localSignature =
                md5(
                        merchant
                                + orderId
                                + amount
                                + currency
                                + status
                                + md5(merchantSecret)
                                .toUpperCase(Locale.ROOT)
                ).toUpperCase(Locale.ROOT);

        if (!localSignature.equalsIgnoreCase(signature)) {

            return ResponseEntity
                    .status(403)
                    .body("invalid_signature");
        }

        // =====================================================
        // FIND PAYMENT
        // =====================================================

        CoursePayment p =
                payments
                        .findByOrderId(orderId)
                        .orElse(null);

        if (p == null) {

            return ResponseEntity
                    .ok("ignored");
        }

        // =====================================================
        // VERIFY CURRENCY
        // =====================================================

        if (!"LKR".equalsIgnoreCase(currency)) {

            p.status = "FAILED";
            payments.save(p);

            return ResponseEntity
                    .badRequest()
                    .body("invalid_currency");
        }

        // =====================================================
        // VERIFY AMOUNT
        // =====================================================

        BigDecimal receivedAmount;

        try {

            receivedAmount =
                    new BigDecimal(amount);

        } catch (NumberFormatException ex) {

            return ResponseEntity
                    .badRequest()
                    .body("invalid_amount");
        }

        if (p.amount == null
                || receivedAmount.compareTo(p.amount) != 0) {

            p.status = "FAILED";
            payments.save(p);

            return ResponseEntity
                    .badRequest()
                    .body("amount_mismatch");
        }

        // =====================================================
        // SUCCESS
        // =====================================================

        if ("2".equals(status)) {

            User user =
                    users
                            .findById(p.userId)
                            .orElse(null);

            Course course =
                    courses
                            .findById(p.courseId)
                            .orElse(null);

            if (user == null || course == null) {

                return ResponseEntity
                        .badRequest()
                        .body("user_or_course_not_found");
            }

            /*
             * If already PAID, do not process again.
             */
            if (!"PAID".equalsIgnoreCase(p.status)) {

                String paymentId =
                        form.getOrDefault(
                                "payment_id",
                                "PAYHERE-" + p.orderId
                        );

                String method =
                        form.getOrDefault(
                                "method",
                                "CARD"
                        );

                String cardNumber =
                        form.get("card_no");

                String last4 =
                        last4(cardNumber);

                paymentService.markPaid(
                        p,
                        user,
                        course,
                        paymentId,
                        "PAYHERE " + method,
                        method,
                        last4
                );
            }

        } else if ("0".equals(status)) {

            // Payment is still pending
            p.status = "PENDING";
            payments.save(p);

        } else {

            // Any other PayHere status is considered failed
            p.status = "FAILED";
            payments.save(p);
        }

        return ResponseEntity
                .ok("OK");
    }

    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    @GetMapping("/status")
    public Map<String, Object> status(
            @RequestHeader("Authorization") String h,
            @RequestParam String orderId
    ) {

        Long uid =
                currentId(h);

        CoursePayment p =
                payments
                        .findByOrderId(orderId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Payment not found."
                                        )
                        );

        if (!uid.equals(p.userId)) {

            throw new RuntimeException(
                    "Unauthorized payment access."
            );
        }

        return response(p);
    }

    // =========================================================
    // MY PAYMENTS
    // =========================================================

    @GetMapping("/my")
    public java.util.List<Map<String, Object>> myPayments(
            @RequestHeader("Authorization") String h
    ) {

        Long uid =
                currentId(h);

        java.util.List<Map<String, Object>> out =
                new java.util.ArrayList<>();

        for (
                CoursePayment p :
                payments.findByUserIdOrderByCreatedAtDesc(uid)
        ) {

            Map<String, Object> m =
                    new LinkedHashMap<>();

            m.put("id", p.id);
            m.put("courseId", p.courseId);
            m.put("orderId", p.orderId);
            m.put("amount", p.amount);
            m.put("status", p.status);
            m.put("paidAt", p.paidAt);
            m.put(
                    "invoiceAvailable",
                    p.invoiceData != null
            );

            courses
                    .findById(p.courseId)
                    .ifPresent(
                            c ->
                                    m.put(
                                            "courseName",
                                            c.name
                                    )
                    );

            out.add(m);
        }

        return out;
    }

    // =========================================================
    // COURSE ACCESS
    // =========================================================

    @GetMapping("/course/{courseId}/access")
    public Map<String, Object> access(
            @RequestHeader("Authorization") String h,
            @PathVariable Long courseId
    ) {

        Long uid =
                currentId(h);

        advancedCourse(courseId);

        return Map.of(
                "paid",
                hasAccess(uid, courseId),
                "courseId",
                courseId
        );
    }

    // =========================================================
    // DOWNLOAD INVOICE
    // =========================================================

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> invoice(
            @RequestHeader("Authorization") String h,
            @PathVariable Long id
    ) {

        Long uid =
                currentId(h);

        CoursePayment p =
                payments
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Payment not found."
                                        )
                        );

        if (!uid.equals(p.userId)
                || !"PAID".equalsIgnoreCase(p.status)) {

            throw new RuntimeException(
                    "Invoice is not available."
            );
        }

        if (p.invoiceData == null
                || p.invoiceData.length == 0) {

            throw new RuntimeException(
                    "Invoice file is not available."
            );
        }

        String filename =
                p.invoiceFileName == null
                        ? "JOBSPHERE-Payment-"
                        + p.orderId
                        + ".pdf"
                        : p.invoiceFileName
                        .replace("\"", "");

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + filename
                                + "\""
                )
                .body(p.invoiceData);
    }

    // =========================================================
    // ADVANCED COURSE CHECK
    // =========================================================

    private Course advancedCourse(Long id) {

        Course c =
                courses
                        .findById(id)
                        .filter(
                                x ->
                                        Boolean.TRUE.equals(
                                                x.active
                                        )
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Course not found."
                                        )
                        );

        if (!"ADVANCED".equalsIgnoreCase(c.courseLevel)) {

            throw new RuntimeException(
                    "Payment is only required for advanced courses."
            );
        }

        return c;
    }

    // =========================================================
    // CHECK PAYMENT ACCESS
    // =========================================================

    private boolean hasAccess(
            Long uid,
            Long cid
    ) {

        return payments
                .findTopByUserIdAndCourseIdAndStatusOrderByPaidAtDesc(
                        uid,
                        cid,
                        "PAID"
                )
                .isPresent();
    }

    // =========================================================
    // PAYMENT RESPONSE
    // =========================================================

    private Map<String, Object> response(
            CoursePayment p
    ) {

        Map<String, Object> m =
                new LinkedHashMap<>();

        m.put(
                "success",
                "PAID".equalsIgnoreCase(p.status)
        );

        m.put(
                "paymentId",
                p.id
        );

        m.put(
                "orderId",
                p.orderId
        );

        m.put(
                "status",
                p.status
        );

        m.put(
                "paidAt",
                p.paidAt
        );

        m.put(
                "emailSent",
                p.emailSent
        );

        m.put(
                "invoiceAvailable",
                p.invoiceData != null
        );

        return m;
    }

    // =========================================================
    // CARD VALIDATION
    // =========================================================

    /*
     * IMPORTANT:
     *
     * There is NO card-holder-name validation here.
     *
     * The holder can contain any value.
     *
     * Only card number, expiry and CVV are checked.
     */
    private void validateCard(
            String number,
            String expiry,
            String cvv
    ) {

        // =====================================================
        // CARD NUMBER
        // =====================================================

        String d =
                number == null
                        ? ""
                        : number.replaceAll("\\D", "");

        if (d.length() < 13
                || d.length() > 19
                || !luhn(d)) {

            throw new RuntimeException(
                    "Enter a valid card number."
            );
        }

        // =====================================================
        // EXPIRY
        // =====================================================

        if (expiry == null
                || !expiry.matches(
                "(0[1-9]|1[0-2])/\\d{2}"
        )) {

            throw new RuntimeException(
                    "Expiry must use MM/YY."
            );
        }

        int yy =
                2000
                        + Integer.parseInt(
                        expiry.substring(3)
                );

        int mm =
                Integer.parseInt(
                        expiry.substring(0, 2)
                );

        YearMonth expiryMonth =
                YearMonth.of(
                        yy,
                        mm
                );

        if (
                expiryMonth.isBefore(
                        YearMonth.now()
                )
        ) {

            throw new RuntimeException(
                    "The card has expired."
            );
        }

        // =====================================================
        // CVV
        // =====================================================

        if (cvv == null
                || !cvv.matches("\\d{3,4}")) {

            throw new RuntimeException(
                    "Enter a valid CVV."
            );
        }
    }

    // =========================================================
    // LUHN
    // =========================================================

    private boolean luhn(String s) {

        int sum = 0;
        boolean alt = false;

        for (
                int i = s.length() - 1;
                i >= 0;
                i--
        ) {

            int n =
                    s.charAt(i) - '0';

            if (alt) {

                n *= 2;

                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;

            alt = !alt;
        }

        return sum % 10 == 0;
    }

    // =========================================================
    // CARD BRAND
    // =========================================================

    private String brand(String d) {

        if (d.startsWith("4")) {
            return "VISA";
        }

        if (
                d.matches("^5[1-5].*")
                        || d.matches("^2[2-7].*")
        ) {
            return "MASTERCARD";
        }

        if (d.matches("^3[47].*")) {
            return "AMEX";
        }

        return "CARD";
    }

    // =========================================================
    // LAST FOUR DIGITS
    // =========================================================

    private String last4(String s) {

        if (s == null) {
            return "";
        }

        String d =
                s.replaceAll("\\D", "");

        return d.length() >= 4
                ? d.substring(d.length() - 4)
                : "";
    }

    // =========================================================
    // MD5
    // =========================================================

    private String md5(String s) {

        try {

            MessageDigest m =
                    MessageDigest.getInstance("MD5");

            byte[] b =
                    m.digest(
                            s.getBytes(
                                    java.nio.charset.StandardCharsets.UTF_8
                            )
                    );

            StringBuilder x =
                    new StringBuilder();

            for (byte v : b) {

                x.append(
                        String.format(
                                "%02x",
                                v
                        )
                );
            }

            return x.toString();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    // =========================================================
    // SPLIT USER NAME
    // =========================================================

    private String[] splitName(
            String full
    ) {

        if (
                full == null
                        || full.trim().isBlank()
        ) {

            return new String[]{
                    "JOBSPHERE",
                    "Learner"
            };
        }

        String[] p =
                full
                        .trim()
                        .split("\\s+", 2);

        if (p.length == 1) {

            return new String[]{
                    p[0],
                    "Learner"
            };
        }

        return p;
    }

    // =========================================================
    // PHONE
    // =========================================================

    private String safePhone(String p) {

        if (
                p != null
                        && p.matches("07\\d{8}")
        ) {

            return p;
        }

        return "0770000000";
    }

    // =========================================================
    // DEMO REQUEST
    // =========================================================

    public record DemoRequest(
            Long courseId,
            String orderId,
            String cardHolder,
            String cardNumber,
            String expiry,
            String cvv
    ) {
    }
}