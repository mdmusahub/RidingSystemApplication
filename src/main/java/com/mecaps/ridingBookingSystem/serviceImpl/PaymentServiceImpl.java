package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.mecaps.ridingBookingSystem.entity.PaymentMethod;
import com.mecaps.ridingBookingSystem.entity.PaymentStatus;
import com.mecaps.ridingBookingSystem.entity.Rides;
import com.mecaps.ridingBookingSystem.exception.PaymentVerificationException;
import com.mecaps.ridingBookingSystem.exception.RideNotFoundException;
import com.mecaps.ridingBookingSystem.repository.PaymentRepository;
import com.mecaps.ridingBookingSystem.repository.RideRepository;
import com.mecaps.ridingBookingSystem.request.PaymentRequestDTO;
import com.mecaps.ridingBookingSystem.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;
    private final RazorpayClient razorpayClient;  // Injected from RazorpayConfig

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              RideRepository rideRepository,
                              RazorpayClient razorpayClient) {

        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
        this.razorpayClient = razorpayClient;
    }


    @Override
    public JSONObject createPaymentOrder(PaymentRequestDTO request) throws RazorpayException {

        logger.info("Creating payment order for Ride ID: {} with amount: {}",
                request.getRideId(), request.getAmount());

        Rides rides = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> {
                    logger.error("Ride not found with ID: {}", request.getRideId());
                    return new RideNotFoundException("Ride Not Found");
                });

        PaymentMethod paymentMethod = request.getPaymentMethod();

        // CASH PAYMENT

        if (paymentMethod == PaymentMethod.CASH) {

            Payment payment = Payment.builder()
                    .rideId(rides)
                    .amount(request.getAmount())
                    .paymentMethod(PaymentMethod.CASH)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId("CASH_" + UUID.randomUUID())
                    .build();

            paymentRepository.save(payment);

            logger.info("Cash payment recorded successfully for Ride ID: {}", request.getRideId());

            return new JSONObject(Map.of(
                    "message", "Cash payment recorded successfully",
                    "paymentStatus", payment.getPaymentStatus(),
                    "paymentMethod", payment.getPaymentMethod()
            ));
        }

        // ONLINE PAYMENT (Razorpay)
        try{

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", Math.round(request.getAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + UUID.randomUUID());
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);

        logger.debug("Razorpay order created with ID: {}", (Object) order.get("id"));

        Payment payment = Payment.builder()
                .rideId(rides)
                .amount(request.getAmount())
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(order.get("id"))
                .build();

        paymentRepository.save(payment);

        logger.info("Payment entry created with PENDING status for Ride ID: {}", request.getRideId());

        return order.toJson();
    }catch (Exception e){
            logger.error("Error creating Razorpay order: {}", e.getMessage(), e);
            throw new PaymentVerificationException("Failed to create Razorpay order");
        }
    }

    @Override
    public Payment verifyPayment(String paymentId, String orderId, String signature) {

        logger.info("Verifying payment for Order ID: {}", orderId);

        try {

            JSONObject object = new JSONObject();
            object.put("razorpay_order_id", orderId);
            object.put("razorpay_payment_id", paymentId);
            object.put("razorpay_signature", signature);

            boolean isSignatureValid = Utils.verifyPaymentSignature(object, secretKey);

            logger.debug("Signature validation result for Order ID {}: {}", orderId, isSignatureValid);

            if (!isSignatureValid) {
                throw new PaymentVerificationException("Invalid payment signature.");
            }

            Payment payment = paymentRepository.findByTransactionId(orderId)
                    .orElseThrow(() ->
                            new PaymentVerificationException("No matching payment found.")
                    );

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);

            logger.info("Payment verified successfully for Order ID: {}", orderId);
            return payment;

        } catch (PaymentVerificationException ex) {
            logger.error("Payment verification failed: {}", ex.getMessage());
            throw ex;

        } catch (Exception e) {
            logger.error("Unexpected error during payment verification: {}", e.getMessage(), e);
            throw new PaymentVerificationException(
                    "Payment verification failed due to server error.", e);
        }
    }
}