package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    private final ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao)
    {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";

        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                Product product = productDao.getById(productId);
                if (product == null)
                {
                    // if product was deleted but cart row still exists, skip it
                    continue;
                }

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(quantity);

                cart.add(item);
            }

            return cart;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProduct(int userId, int productId)
    {
        // If already exists, increment. Else insert.
        String existsSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement existsPs = connection.prepareStatement(existsSql);
            existsPs.setInt(1, userId);
            existsPs.setInt(2, productId);

            ResultSet rs = existsPs.executeQuery();

            if (rs.next())
            {
                PreparedStatement updatePs = connection.prepareStatement(updateSql);
                updatePs.setInt(1, userId);
                updatePs.setInt(2, productId);
                updatePs.executeUpdate();
            }
            else
            {
                PreparedStatement insertPs = connection.prepareStatement(insertSql);
                insertPs.setInt(1, userId);
                insertPs.setInt(2, productId);
                insertPs.setInt(3, 1);
                insertPs.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduct(int userId, int productId, int quantity)
    {
        // If quantity <= 0, remove the row
        String deleteSql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String updateSql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = getConnection())
        {
            if (quantity <= 0)
            {
                PreparedStatement ps = connection.prepareStatement(deleteSql);
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }
            else
            {
                PreparedStatement ps = connection.prepareStatement(updateSql);
                ps.setInt(1, quantity);
                ps.setInt(2, userId);
                ps.setInt(3, productId);
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            System.out.println(userId);
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
