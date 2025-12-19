package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
@RequestMapping("/cart")
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // GET https://localhost:8080/cart
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            return shoppingCartDao.getByUserId(user.getId());
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // POST https://localhost:8080/cart/products/15
    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProductToCart(@PathVariable int productId, Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            Product product = productDao.getById(productId);
            if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");

            shoppingCartDao.addProduct(user.getId(), productId);

            return shoppingCartDao.getByUserId(user.getId());
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


    @PutMapping("/products/{productId}")
    public ShoppingCart updateProductInCart(@PathVariable int productId,
                                            @RequestBody ShoppingCartItem item,
                                            Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            if (item == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing body.");

            Product product = productDao.getById(productId);
            if (product == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");

            shoppingCartDao.updateProduct(user.getId(), productId, item.getQuantity());

            return shoppingCartDao.getByUserId(user.getId());
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean clearCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

            shoppingCartDao.clearCart(user.getId());
            return true;
        }
        catch (ResponseStatusException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
