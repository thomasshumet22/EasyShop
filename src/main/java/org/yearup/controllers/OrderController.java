package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;

import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")   // only logged-in users
@RequestMapping("/orders")
public class OrderController
{
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    public OrderController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao)
    {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    // POST http://localhost:8080/orders
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Order> createOrder(Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);

            if (user == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty.");

            // Your DAO will:
            // 1) create order
            // 2) create order_line_items
            // 3) clear shopping_cart
            Order created = orderDao.createOrder(userId, cart);

            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
