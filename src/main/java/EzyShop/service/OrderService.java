package EzyShop.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.invoice.InvoiceDto;
import EzyShop.dto.order.OrderDto;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.OrderMapper;
import EzyShop.model.User;
import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.Order;
import EzyShop.model.orders.OrderItem;
import EzyShop.model.orders.OrderStatus;
import EzyShop.model.store.Store;
import EzyShop.repository.InvoiceRepository;
import EzyShop.repository.OrderRepository;
import EzyShop.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final InvoiceRepository invoiceRepository;
        private final StoreRepository storeRepository;
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public List<OrderDto> getOrdersByUserId(Long userId) {
                List<Order> orders = orderRepository.findAllByUser_Id(userId);
                return orders.stream()
                                .map(OrderMapper::toDto)
                                .toList();
        }

        @Transactional
        public List<OrderDto> getOrdersByStoreIdAndLimit(Long userId, Integer limit) {
                int maxResults = (limit != null && limit > 0) ? limit : 5;

                Store store = storeRepository.findByOwner(User.builder().id(userId).build())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Store not found with owner id: " + userId));

                List<Order> orders = entityManager.createQuery("""
                                    SELECT o FROM Order o
                                    WHERE o.store.id = :storeId
                                    ORDER BY o.createdAt DESC
                                """, Order.class)
                                .setParameter("storeId", store.getId())
                                .setMaxResults(maxResults)
                                .getResultList();

                return orders.stream()
                                .map(OrderMapper::toDto)
                                .toList();
        }

        @Transactional
        public List<OrderDto> getOrdersByStoreId(Long userId) {
                Store store = storeRepository.findByOwner(User.builder().id(userId).build())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Store not found with owner id: " + userId));

                List<Order> orders = orderRepository.findByStore(store);
                return orders.stream()
                                .map(OrderMapper::toDto)
                                .toList();
        }

        @Transactional
        public InvoiceDto getInvoiceByInvoiceNumber(String invoiceNumber) {
                return InvoiceDto.fromEntity(
                                invoiceRepository.findByInvoiceNumber(invoiceNumber)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Invoice not found with number: " + invoiceNumber)));
        }

        @Transactional
        public InvoiceDto getInvoiceByOrderId(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with id: " + orderId));

                return InvoiceDto.fromEntity(
                                invoiceRepository.findByOrder(order)
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Invoice not found for order id: " + orderId)));
        }

        @Transactional
        public OrderDto createOrder(Long userId, List<CartDto> carts) {
                if (carts == null || carts.isEmpty()) {
                        throw new BusinessException("Cart must not be empty", HttpStatus.BAD_REQUEST);
                }

                List<OrderItem> items = carts.stream()
                                .flatMap(cart -> cart.getItems().stream())
                                .map(cartItem -> OrderItem.builder()
                                                .productId(cartItem.getId())
                                                .productName(cartItem.getProductTitle())
                                                .productImage(cartItem.getProductThumbnail())
                                                .price(cartItem.getPrice())
                                                .quantity(cartItem.getQuantity())
                                                .build())
                                .toList();

                if (items.isEmpty()) {
                        throw new BusinessException("Cart items must not be empty", HttpStatus.BAD_REQUEST);
                }

                BigDecimal subTotal = items.stream()
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Order order = Order.builder()
                                .user(User.builder().id(userId).build())
                                .items(items)
                                .subtotal(subTotal)
                                .referenceId(generateReferenceId())
                                .channelCode(ChannelCode.XENDIT)
                                .status(OrderStatus.PENDING_PAYMENT)
                                .build();

                items.forEach(item -> item.setOrder(order));
                log.info("Creating order with reference ID: {}", order.getReferenceId());

                return OrderMapper.toDto(orderRepository.save(order));
        }

        @Transactional
        public OrderDto getOrderByReferenceId(String referenceId) {
                Order order = orderRepository.findByReferenceId(referenceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found with referenceId: " + referenceId));
                return OrderMapper.toDto(order);
        }

        private static String generateReferenceId() {
                long timestamp = Instant.now().getEpochSecond();
                return "test-" + timestamp;
        }
}
