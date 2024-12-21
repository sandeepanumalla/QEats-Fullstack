    package dev.qeats.user_service.service.impl;

    import dev.qeats.user_service.model.CartItem;
    import dev.qeats.user_service.repository.CartRepository;
    import dev.qeats.user_service.repository.UserRepository;
    import dev.qeats.user_service.response.CartItemResponseVO;
    import dev.qeats.user_service.response.CartResponseVO;
    import dev.qeats.user_service.service.CartService;
    import org.springframework.stereotype.Service;
    import reactor.core.publisher.Mono;

    import java.util.List;

    @Service
    public class CartServiceImpl implements CartService {

        private final CartRepository cartRepository;
        private final UserRepository userRepository;

        public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository) {
            this.cartRepository = cartRepository;
            this.userRepository = userRepository;
//            entityManager.clear();
        }

        @Override
        public Mono<CartResponseVO> getUserCart(String userId) {
//            return userRepository.findById(userId)
//                    .switchIfEmpty(Mono.error(new Exception("User does not exist")))
//                    .flatMap(user -> Mono.justOrEmpty(null))
//                    .flatMap(cart -> {
//                        List<CartItemResponseVO> cartItemResponseVOs = cart.getItems()
//                                .stream()
//                                .map(CartItem::toCartItemResponseVO)
//                                .toList();
//                        return Mono.just(cart.toCartResponseVO(cartItemResponseVOs));
//                    });
            return null;
        }

        @Override
        public Mono<Void> addToCart(String userId, List<CartItem> cartItems) {
//            return userRepository.findById(userId)
//                    .switchIfEmpty(Mono.error(new Exception("User does not exist")))
//                    .flatMap(user -> Mono.justOrEmpty(user.getCart()))
//                    .flatMap(cart -> {
//                        for (CartItem cartItem : cartItems) {
//                            boolean itemExists = false;
//
//                            // Check if the item already exists in the cart
//                            for (CartItem existingItem : cart.getItems()) {
//                                if (existingItem.getProductId().equals(cartItem.getProductId())) {
//                                    // If the item exists, update the quantity
//                                    existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
//                                    itemExists = true;
//                                    break;
//                                }
//                            }
//
//                            if (!itemExists) {
//                                cart.getItems().add(cartItem);
//                            }
//                        }
//                        return cartRepository.save(cart).then();
//                    });
            return null;
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
    }
