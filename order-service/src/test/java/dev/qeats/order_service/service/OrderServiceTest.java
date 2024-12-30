package dev.qeats.order_service.service;

import dev.qeats.order_service.OrderServiceApplication;
import dev.qeats.order_service.model.*;
import dev.qeats.order_service.model.OrderStatus;
import dev.qeats.order_service.repository.OrderRepository;
import dev.qeats.order_service.request.*;
import dev.qeats.order_service.request.OrderRequestVO;
import dev.qeats.order_service.response.OrderResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService; // Assuming this is the service containing the processOrder method

    @Autowired
    private ModelMapper modelMapper;

    private OrderRequestVO orderRequestVO;

    @BeforeEach
    void setUp() {
        // Initialize OrderRequestVO with sample data
        orderRequestVO = new OrderRequestVO();
        orderRequestVO.setCustomerId("C001");
        orderRequestVO.setRestaurantId("R001");
        orderRequestVO.setPaymentId("PAY123");
        orderRequestVO.setPayerId("PAYER123");
        orderRequestVO.setTotalAmount(500.0);

        // Set DeliveryAddress
        DeliveryAddressVO address = new DeliveryAddressVO();
        address.setStreet("123 Main St");
        address.setCity("Sample City");
        address.setState("Sample State");
        address.setZipCode("12345");
        address.setCountry("Sample Country");
        orderRequestVO.setAddress(address);

        // Set OrderItems
        OrderItemVO item1 = new OrderItemVO();
        item1.setProductId("P001");
        item1.setProductName("Burger");
        item1.setDescription("Delicious burger");
        item1.setQuantity(2);
        item1.setPrice(100.0);
        item1.setCurrency("USD");

        OrderItemVO item2 = new OrderItemVO();
        item2.setProductId("P002");
        item2.setProductName("Fries");
        item2.setDescription("Crispy fries");
        item2.setQuantity(1);
        item2.setPrice(50.0);
        item2.setCurrency("USD");

        orderRequestVO.setItems(Arrays.asList(item1, item2));
    }

    @Test
    void testProcessOrder() {
        // Execute the method under test
        OrderResponseVo response = orderService.processOrder(orderRequestVO);

        // Fetch the saved order for assertions
        Order savedOrder = orderRepository.findById(response.getOrderId()).orElse(null);

        // Assertions
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getCustomerId()).isEqualTo("C001");
        assertThat(savedOrder.getRestaurantId()).isEqualTo("R001");
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(savedOrder.getTotalAmount()).isEqualTo(500.0);

        // Verify items
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.getItems()).extracting("productName")
                .containsExactlyInAnyOrder("Burger", "Fries");

        // Verify response matches the saved order
        assertThat(response.getCustomerId()).isEqualTo(savedOrder.getCustomerId());
        assertThat(response.getOrderId()).isEqualTo(savedOrder.getOrderId());
    }

    @Test
    public void testCancelOrder_success() {
        // Mock an order
        Order mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setOrderStatus(OrderStatus.PLACED);

        // Setup mock for repository
        when(orderRepository.findByOrderId(1L)).thenReturn(mockOrder);

        // Call the cancelOrder method
        orderService.cancelOrder(1L);

        // Verify the order status has been updated to CANCELLED and save() is called
        verify(orderRepository, times(1)).findByOrderId(1L);
        verify(orderRepository, times(1)).save(Mockito.argThat(order -> order.getOrderStatus() == OrderStatus.CANCELLED));
    }

    @Test
    public void testCancelOrder_orderNotFound() {
        // Mock the scenario where the order is not found
        when(orderRepository.findByOrderId(anyLong())).thenReturn(null);

        // Expect an exception when the order is not found
        try {
            orderService.cancelOrder(1L);
        } catch (RuntimeException e) {
            // Verify that the exception is thrown
            assert(e.getMessage().contains("Order not found"));
        }

        // Verify that save() was never called since the order doesn't exist
        verify(orderRepository, never()).save(any(Order.class));
    }

}
