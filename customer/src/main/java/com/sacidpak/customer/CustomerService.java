package com.sacidpak.customer;

import com.sacidpak.clients.notification.NotificationClient;
import com.sacidpak.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.sacidpak.clients.fraud.FraudCheckResponse;
import com.sacidpak.clients.fraud.FraudClient;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        //todo: check if email valid
        //todo: check if email not taken
        customerRepository.saveAndFlush(customer);
        FraudCheckResponse response =
                fraudClient.isFraudster(customer.getId());

        if(response.isFraudster()){
            throw new IllegalStateException("frauster");
        }
        // todo: make it async. i.e add to queue
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome...",
                                customer.getFirstName())
                )
        );
    }
}
