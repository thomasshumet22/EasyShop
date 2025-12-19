package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao
{
    public MySqlOrderDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Order createOrder(int userId, ShoppingCart cart)
    {
        Connection connection = null;

        try
        {
            connection = getConnection();
            connection.setAutoCommit(false);

            // 1) Create order row
            String orderSql =
                    "INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount) " +
                            "VALUES (?, NOW(), '', '', '', '', 0.00)";


            PreparedStatement orderStmt =
                    connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderStmt.setInt(1, userId);
            orderStmt.executeUpdate();


            ResultSet keys = orderStmt.getGeneratedKeys();
            if(!keys.next())
                throw new RuntimeException("Creating order failed - no order_id returned.");

            int orderId = keys.getInt(1);

            // 2) Insert order line items
            // Your DB columns are: order_id, product_id, sales_price, quantity, discount
            String itemSql =
                    "INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount) " +
                            "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement itemStmt = connection.prepareStatement(itemSql);

            for (ShoppingCartItem item : cart.getItems().values())
            {
                // If product is null here, your ShoppingCartDao isn't loading product details.
                if(item.getProduct() == null)
                    throw new RuntimeException("Cart item product is null for productId=" + item.getProductId());

                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setBigDecimal(3, item.getProduct().getPrice());           // maps to sales_price
                itemStmt.setInt(4, item.getQuantity());
                itemStmt.setBigDecimal(5, item.getDiscountPercent());              // maps to discount
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();

            // 3) Clear shopping cart
            String clearSql = "DELETE FROM shopping_cart WHERE user_id = ?";
            PreparedStatement clearStmt = connection.prepareStatement(clearSql);
            clearStmt.setInt(1, userId);
            clearStmt.executeUpdate();

            connection.commit();

            Order order = new Order();
            order.setOrderId(orderId);
            order.setUserId(userId);
            return order;
        }
        catch (Exception ex)
        {
            try
            {
                if(connection != null) connection.rollback();
            }
            catch (SQLException ignored) { }

            throw new RuntimeException(ex);
        }
        finally
        {
            try
            {
                if(connection != null) connection.setAutoCommit(true);
            }
            catch (SQLException ignored) { }
        }
    }
}


