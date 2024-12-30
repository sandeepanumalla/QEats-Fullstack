    package dev.qeats.user_service.service.impl;

    import dev.qeats.user_service.model.Cart;
    import dev.qeats.user_service.model.CartItem;
    import dev.qeats.user_service.model.User;
    import dev.qeats.user_service.repository.CartItemRepository;
    import dev.qeats.user_service.repository.CartRepository;
    import dev.qeats.user_service.repository.UserRepository;
    import dev.qeats.user_service.request.CartRequestVO;
    import dev.qeats.user_service.response.CartItemResponseVO;
    import dev.qeats.user_service.response.CartResponseVO;
    import dev.qeats.user_service.service.CartService;
    import lombok.extern.slf4j.Slf4j;
    import org.modelmapper.ModelMapper;
    import org.modelmapper.TypeToken;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import reactor.core.publisher.Flux;
    import reactor.core.publisher.Mono;
    import dev.qeats.user_service.request.CartItemRequestVO;

    import java.util.*;
    import java.util.stream.Collectors;
    import java.util.stream.IntStream;
    import java.util.stream.Stream;

    @Slf4j
    @Service
    @Transactional
    public class CartServiceImpl implements CartService {
        private final CartItemRepository cartItemRepository;

        private final CartRepository cartRepository;
        private final UserRepository userRepository;
        private final ModelMapper modelMapper;

        public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository,
                               CartItemRepository cartItemRepository, ModelMapper modelMapper) {
            this.cartRepository = cartRepository;
            this.userRepository = userRepository;
//            entityManager.clear();
            this.cartItemRepository = cartItemRepository;
            this.modelMapper = modelMapper;
        }


        @Override
        public Mono<CartResponseVO> getUserCart(String userId, String restaurantId) {
            return userRepository.findById(userId)
                    .switchIfEmpty(Mono.error(new Exception("User does not exist")))
                    .flatMap(user -> cartRepository.findByUserId(userId)
                            .switchIfEmpty(Mono.error(new Exception("Cart does not exist for user"))))
                    .flatMap(cart ->
                            cartItemRepository.findByCartIdAndRestaurantId(cart.getCartId(), restaurantId) // Fetch cart items
                                    .collectList() // Collect items into a list
                                    .map(cartItems -> { // Transform CartItems into CartItemResponseVOs
                                        List<CartItemResponseVO> cartItemResponseVOs = cartItems.stream()
                                                .map(item -> {
                                                    CartItemResponseVO responseVO = new CartItemResponseVO();
                                                    responseVO.setCartItemId(item.getCartItemId());
                                                    responseVO.setRestaurantId(item.getRestaurantId());
                                                    responseVO.setProductId(item.getProductId());
                                                    responseVO.setProductName(item.getProductName());
                                                    responseVO.setProductDescription(item.getProductDescription());
                                                    responseVO.setQuantity(item.getQuantity());
                                                    responseVO.setPrice(item.getPrice());
//                                                    responseVO.setPrice(item.getQuantity() * item.getPrice()); // Total for this item
                                                    return responseVO;
                                                })
                                                .collect(Collectors.toList());
                                        // Map Cart to CartResponseVO
                                        return cart.toCartResponseVO(cartItemResponseVOs);
                                    })
                    );
        }


        @Override
        public Mono<Void> addToCart(String userId, List<CartItem> cartItems) {
            return userRepository.findById(userId)
                    .switchIfEmpty(Mono.error(new Exception("User does not exist")))
                    .flatMap(user -> cartRepository.findByUserId(userId)
                            .switchIfEmpty(Mono.error(new Exception("Cart does not exist for user"))))
                    .flatMap(cart -> {
                        // Update cart items
                        cartItems.forEach(newItem -> {
                            CartItem existingItem = cart.getItems().stream()
                                    .filter(item -> item.getProductId().equals(newItem.getProductId()))
                                    .findFirst()
                                    .orElse(null);

                            if (existingItem != null) {
                                // Update quantity if the item already exists
                                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                            } else {
                                // Add the new item if it doesn't exist
                                cart.getItems().add(newItem);
                            }
                        });

                        // Update the total cost of the cart
                        cart.setTotalCost();

                        // Save the updated cart
                        return cartRepository.save(cart);
                    })
                    .then(); // Complete the reactive stream with a Void return type
        }


        @Override
        public Mono<Void> removeFromCart(String userId, long cartItemId, int quantity) {
//            return userRepository.findById(userId)
//                    .flatMap(user -> Mono.justOrEmpty(user.getCart()))
//                    .flatMap(cart -> {
//                        cart.getItems().removeIf(cartItem -> cartItem.getCartItemId() == cartItemId);
//                        return cartRepository.save(cart).then();
//                    });
            return null;
        }

        @Override
        public Mono<Void> clearCart(String userId, String restaurantId) {
//            return userRepository.findById(userId)
//                    .flatMap(user -> Mono.justOrEmpty(user.getCart()))
//                    .flatMap(cart -> {
//                        cart.getItems().clear();
//                        return cartRepository.save(cart).then();
//                    });
            return null;
        }

        @Override
        public Mono<Void> checkout(String userId, String restaurantId) {
            // send an async rest call to the order service
            // once the order is placed then clear the cart
            // clearCart(userId, restaurantId);
            return Mono.empty();
        }

        @Override
        public Mono<Void> createCartForUser(String userId) {
            log.info("creating cart for the user, {}", userId);
            return userRepository.findById(userId)
                    .switchIfEmpty(Mono.error(new Exception("User does not exist")))
                    .flatMap(user -> cartRepository.findByUserId(userId)
                            .switchIfEmpty(Mono.defer(() -> {
                                // Create a new cart for the user
                                Cart newCart = new Cart();
                                newCart.setUserId(userId);
                                newCart.setItems(List.of()); // Initialize with an empty item list
                                newCart.setTotalCost(0.0);
                                return cartRepository.save(newCart);
                            }))
                            .then() // If the cart already exists, do nothing
                    );
        }

        @Override

        public Mono<ResponseEntity<String>> updateUserCart(String userId, String restaurantId, CartRequestVO cartRequestVO) {
            return cartRepository.findByUserId(userId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Cart does not exist for user")))
                    .flatMap(cart -> {
                        cart.setTotalCost(cartRequestVO.getTotalCost());
                        return cartRepository.save(cart)
                                .flatMap(savedCart -> {
                                    // Fetch existing items from the database
                                    return cartItemRepository.findByCartIdAndRestaurantId(savedCart.getCartId(), restaurantId)
                                            .collectList()
                                            .flatMap(existingItems -> {
                                                // Separate items into update, add, and delete groups
                                                List<CartItemRequestVO> requestItems = cartRequestVO.getItems();

                                                // Create maps for easy lookup
                                                Map<Long, CartItem> existingItemsMap = existingItems.stream()
                                                        .collect(Collectors.toMap(CartItem::getCartItemId, item -> item));

                                                // Items to update or remove
                                                List<CartItem> itemsToUpdateOrDelete = requestItems.stream()
                                                        .filter(existingItem -> requestItems.stream()
                                                                .anyMatch(reqItem -> reqItem.getCartItemId() == existingItem.getCartItemId()))
                                                        .toList().stream().map(each -> modelMapper.map(each, CartItem.class)).toList()
                                                        .stream().peek(cartItem -> cartItem.setCartId(cartRequestVO.getCartId())).toList();

                                                // Items to add
                                                List<CartItem> itemsToAdd = requestItems.stream()
                                                        .filter(reqItem -> reqItem.getCartItemId() == 0) // New items without an ID
                                                        .map(reqItem -> {
                                                            CartItem cartItem = modelMapper.map(reqItem, CartItem.class);
                                                            cartItem.setRestaurantId(restaurantId);
                                                            cartItem.setCartId(savedCart.getCartId());
                                                            return cartItem;
                                                        })
                                                        .collect(Collectors.toList());

                                                // Items to remove
                                                List<CartItem> itemsToRemove = existingItems.stream()
                                                        .filter(existingItem -> requestItems.stream()
                                                                .noneMatch(reqItem -> reqItem.getCartItemId() == existingItem.getCartItemId()))
                                                        .collect(Collectors.toList());

                                                // Perform update, delete, and add operations
                                                return updateAndRemoveItems(itemsToUpdateOrDelete, itemsToRemove)
                                                        .then(addNewItems(itemsToAdd))
                                                        .then(Mono.just(ResponseEntity.ok("Cart updated successfully")));
                                            });
                                });
                    });
        }

        private Mono<Void> updateAndRemoveItems(List<CartItem> itemsToUpdate, List<CartItem> itemsToRemove) {
            return Flux.concat(
                    // Update existing items
                    Flux.fromIterable(itemsToUpdate)
                            .flatMap(cartItemRepository::save)
                            .then(),
                    // Remove items not in the request
                    Flux.fromIterable(itemsToRemove)
                            .flatMap(cartItemRepository::delete)
                            .then()
            ).then();
        }

        private Mono<Void> addNewItems(List<CartItem> itemsToAdd) {
            return Flux.fromIterable(itemsToAdd)
                    .flatMap(cartItemRepository::save)
                    .then();
        }


//        @Override
//        public Mono<ResponseEntity<String>> updateUserCart(String userId, CartRequestVO cartRequestVO) {
//            return cartRepository.findByUserId(userId)
//                    .switchIfEmpty(Mono.error(new RuntimeException("Cart does not exist for user")))
//                    .flatMap(cart -> {
//                        cart.setUserId(userId);
//                        cart.setTotalCost(cartRequestVO.getTotalCost());
//                        return cartRepository.save(cart);
//                    })
//                    .flatMap(savedCart -> {
//                        // Save all cart items
//                        return Flux.fromIterable(cartRequestVO.getItems())
//                                .map(cartItemRequestVO -> modelMapper.map(cartItemRequestVO, CartItem.class))
//                                .collectList() // Collect all saved items into a list
//                                .flatMapMany(cartItemRepository::saveAll)
//                                .collectList()
//                                .flatMap(savedCartItems -> {
//                                    // Map the cart and items into a response VO
//                                    CartResponseVO responseVO = new CartResponseVO();
//                                    responseVO.setCartId(savedCart.getCartId());
//                                    responseVO.setItems(
//                                            savedCartItems.stream().map(item -> {
//                                                CartItemResponseVO itemVO = new CartItemResponseVO();
//                                                itemVO.setProductId(item.getProductId());
//                                                itemVO.setQuantity(item.getQuantity());
//                                                itemVO.setPrice(item.getPrice());
//                                                return itemVO;
//                                            }).collect(Collectors.toList())
//                                    );
//                                    responseVO.setTotalCost(savedCart.getTotalCost());
//                                    return Mono.just(ResponseEntity.ok(responseVO.toString()));
//                                });
//                    });
//        }

    }
