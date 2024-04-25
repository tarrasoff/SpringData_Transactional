package com.example.springdata_transactional.service;

import com.example.springdata_transactional.entity.Customer;
import com.example.springdata_transactional.entity.Order;
import com.example.springdata_transactional.entity.Product;
import com.example.springdata_transactional.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final CustomerService customerService;

    @Transactional
    public Order placeOrder(Long customerId, List<Long> productIds) {
        Customer customer = customerService.getCustomerById(customerId);

        List<Product> products = productService.getProductsByIds(productIds);

        BigDecimal totalAmount = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (customer.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        Order order = Order.builder()
                .customer(customer)
                .products(products)
                .totalAmount(totalAmount)
                .build();

        customer.setBalance(customer.getBalance().subtract(totalAmount));
        customerService.update(customer.getId(), customer);

        productService.decreaseProductQuantities(productIds);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order update(Long id, Order order) {
        order.setId(id);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}