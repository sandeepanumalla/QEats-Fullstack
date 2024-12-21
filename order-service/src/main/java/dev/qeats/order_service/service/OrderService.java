    package dev.qeats.order_service.service;

    import dev.qeats.order_service.model.DeliveryAddress;
    import dev.qeats.order_service.model.Order;
    import dev.qeats.order_service.model.OrderItem;
    import dev.qeats.order_service.model.OrderStatus;
    import dev.qeats.order_service.repository.OrderRepository;
    import dev.qeats.order_service.request.OrderRequestVO;
    import dev.qeats.order_service.response.OrderResponseVo;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.modelmapper.ModelMapper;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.client.RestTemplate;

    import java.time.LocalDateTime;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class OrderService {

        private final OrderRepository orderRepository;
        private final ModelMapper modelMapper;
        private final RestTemplate restTemplate;


//        public OrderResponseVo createOrder(OrderRequestVO orderRequestVO) {
//            // Create order
//            Order order = new Order();
//            order.setCustomerId(orderRequestVO.getCustomerId());
//            order.setRestaurantId(orderRequestVO.getRestaurantId());
//            order.setPaymentId(orderRequestVO.getPaymentId());
//            order.setDeliveryAddress(orderRequestVO.getAddress());
//            order.setOrderStatus(OrderStatus.valueOf(orderRequestVO.getOrderStatus()));
//            order.setTotalCost(orderRequestVO.getTotalAmount());
//            order.setPaymentStatus(orderRequestVO.getPaymentStatus());
//
//            order.setItems(orderRequestVO.getItems().stream().map(itemVO -> {
//                OrderItem item = new OrderItem();
//                item.setProductName(itemVO.getProductName());
//                item.setQuantity(itemVO.getQuantity());
//                item.setOrder(order); // Set the relationship with order
//                return item;
//            }).collect(Collectors.toList()));
//
//            Order savedOrder = orderRepository.save(order);
//            return modelMapper.map(savedOrder, OrderResponseVo.class);
//        }

        public void cancelOrder(long orderId) {
            // Cancel order
            Order order = orderRepository.findByOrderId(orderId);
            if (order != null) {
                // Mark the order as cancelled
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            } else {
                throw new RuntimeException("Order not found");
            }
        }

        public OrderResponseVo updateOrder(Long orderId, OrderRequestVO orderRequestVO) {

            // Update order
    //        Order order = orderRepository.findByOrderId(orderRequestVO.getOrderId());
    //        if (order != null) {
    //            // Update the order details
    //            order.setOrderStatus(OrderStatus.valueOf(orderRequestVO.getOrderStatus()));
    //            order.setPaymentStatus(orderRequestVO.getPaymentStatus());
    //            order.setTotalCost(orderRequestVO.getTotalAmount());
    //            order.setDeliveryAddress(orderRequestVO.getAddress());
    //
    //            // Update the items (or you can handle this separately)
    //            order.getItems().clear();
    //            order.setItems(orderRequestVO.getItems().stream().map(itemVO -> {
    //                OrderItem item = new OrderItem();
    //                item.setProductName(itemVO.getProduc);
    //                item.setQuantity(itemVO.getQuantity());
    //                item.setOrder(order);
    //                return item;
    //            }).collect(Collectors.toList()));
    //
    //            // Save the updated order
    //            orderRepository.save(order);
    //        } else {
    //            throw new RuntimeException("Order not found");
    //        }
            return null;

        }

        @Transactional
        public OrderResponseVo processOrder(OrderRequestVO orderRequestVO) {
            // Create and populate the Order entity
            Order order = new Order();
            order.setCustomerId(orderRequestVO.getCustomerId());
            order.setRestaurantId(orderRequestVO.getRestaurantId());
            order.setPaymentId(orderRequestVO.getPaymentId());
            order.setPayerId(orderRequestVO.getPayerId());
            order.setDeliveryAddress(modelMapper.map(orderRequestVO.getAddress(), DeliveryAddress.class));
            order.setOrderStatus(OrderStatus.PROCESSING);
            order.setOrderTime(LocalDateTime.now().toString());
            order.setAmount(orderRequestVO.getTotalAmount());

            // Populate OrderItems and associate them with the Order
            order.setItems(orderRequestVO.getItems().stream().map(itemVO -> {
                OrderItem item = new OrderItem();
                item.setProductId(itemVO.getProductId());
                item.setProductName(itemVO.getProductName());
                item.setDescription(itemVO.getDescription());
                item.setQuantity(itemVO.getQuantity());
                item.setPrice(itemVO.getPrice());
                item.setCurrency(itemVO.getCurrency());
                item.setOrder(order); // Set the relationship
                return item;
            }).collect(Collectors.toList()));

            // Save the order to the database
            Order savedOrder = orderRepository.save(order);

            // Update order status after processing
            savedOrder.setOrderStatus(OrderStatus.PROCESSING);
            savedOrder.setLastUpdateTime(LocalDateTime.now().toString());
            orderRepository.save(savedOrder);

            // Return the response
            return modelMapper.map(savedOrder, OrderResponseVo.class);
        }

        public void sendOrderToRestaurant(OrderResponseVo orderResponse) {
            // Define the restaurant webhook URL (this should be configurable)
            String restaurantWebhookUrl = "http://restaurant-service/api/orders";

            try {
                // Send the order details to the restaurant webhook
                ResponseEntity<String> response = restTemplate.postForEntity(restaurantWebhookUrl, orderResponse, String.class);

                // Log the response from the restaurant
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Order sent to restaurant successfully: {}", response.getBody());
                } else {
                    log.error("Failed to send order to restaurant. Response code: {}", response.getStatusCode());
                }
            } catch (Exception ex) {
                log.error("Error occurred while sending order to restaurant: {}", ex.getMessage(), ex);
                throw new RuntimeException("Failed to send order to restaurant", ex);
            }
        }

        public OrderResponseVo getOrdersByRestaurant(String restaurantId) {
            Order order = orderRepository.findByRestaurantId(restaurantId);
            if (order != null) {
                return modelMapper.map(order, OrderResponseVo.class);
            }
            return null;
        }
    }
