package dev.qeats.order_service.repository;

import dev.qeats.order_service.OrderServiceApplication;
import dev.qeats.order_service.model.Order;
import dev.qeats.order_service.model.OrderItem;
import dev.qeats.order_service.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import dev.qeats.order_service.model.DeliveryAddress;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


//@SpringBootTest(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Commit
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

//    @BeforeEach
//    public void setUp() {
//        // Setup a sample order before each test
//        order = new Order();
//        order.setOrderId(1L);
//        order.setCustomerId("customer123");
//        order.setRestaurantId("restaurant123");
//        order.setOrderStatus(OrderStatus.PLACED);
//        order.setPaymentStatus("PENDING");
//        order.setDeliveryAddress("123 Street");
//        order.setTotalCost(200.0);
//
//        // Setup a sample order item
//        OrderItem item = new OrderItem();
//        item.setProductName("Burger");
//        item.setQuantity(2);
//        item.setOrder(order);
//
//        order.setItems(Collections.singletonList(item));
//    }

    @Test
    void testSaveAndFindOrder() {
        // Create Delivery Address
        DeliveryAddress address = new DeliveryAddress();
        address.setStreet("123 Main St");
        address.setCity("Sample City");
        address.setState("Sample State");
        address.setZipCode("12345");
        address.setCountry("Sample Country");

        // Create OrderItems
        OrderItem item1 = new OrderItem();
        item1.setProductId("P001");
        item1.setProductName("Product 1");
        item1.setDescription("Description 1");
        item1.setQuantity(2);
        item1.setPrice(100.0);
        item1.setCurrency("USD");

        OrderItem item2 = new OrderItem();
        item2.setProductId("P002");
        item2.setProductName("Product 2");
        item2.setDescription("Description 2");
        item2.setQuantity(1);
        item2.setPrice(200.0);
        item2.setCurrency("USD");

        // Create Order
        Order order = new Order();
        order.setCustomerId("C001");
        order.setRestaurantId("R001");
        order.setPaymentId("PAY123");
        order.setDeliveryAddress(address);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setOrderTime("2024-12-19T10:00:00");
        order.setLastUpdateTime("2024-12-19T10:00:00");
        order.setExpectedDeliveryTime("2024-12-19T12:00:00");
        order.setAmount(400.0);

        // Associate items with the order
        item1.setOrder(order);
        item2.setOrder(order);
        order.setItems(Arrays.asList(item1, item2));

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Fetch the order
        Order fetchedOrder = orderRepository.findById(savedOrder.getOrderId()).orElse(null);

        // Assertions
        assertThat(fetchedOrder).isNotNull();
        assertThat(fetchedOrder.getCustomerId()).isEqualTo("C001");
        assertThat(fetchedOrder.getRestaurantId()).isEqualTo("R001");
        assertThat(fetchedOrder.getItems()).hasSize(2);
        assertThat(fetchedOrder.getDeliveryAddress().getCity()).isEqualTo("Sample City");
    }



    @Test
    public void testFindByOrderId() {
        // Save the order
//        orderRepository.save(order);

        // Retrieve the order by orderId
        Order foundOrder = orderRepository.findByOrderId(6L);

        // Ensure the order was found correctly
        assertNotNull(foundOrder);
        assertEquals(6L, foundOrder.getOrderId());
        assertEquals("customer123", foundOrder.getCustomerId());
        assertEquals(OrderStatus.PLACED, foundOrder.getOrderStatus());
        assertEquals(1, foundOrder.getItems().size());
    }

    @Test
    public void testDeleteOrder() {
        // Save the order
//        orderRepository.save(order);

        // Delete the order
        orderRepository.deleteById(order.getOrderId());

        // Verify the order is deleted
        Optional<Order> deletedOrder = orderRepository.findById(order.getOrderId());
        assertTrue(deletedOrder.isEmpty());
    }
}
