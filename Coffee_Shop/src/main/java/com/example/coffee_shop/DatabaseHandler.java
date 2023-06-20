package com.example.coffee_shop;
import com.example.coffee_shop.ProductModel.*;
import javafx.scene.control.Alert;


import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private static DatabaseHandler instance = null;
    private Connection connection;

    private DatabaseHandler() {
        String databasename = "shop_db" ;
        String user = "root";
        String pass = "alaa123456789";
        String url = "jdbc:mysql://localhost:3306/" + databasename;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean validateLogin(String email , String password) {
        Connection c = this.getConnection();
        String query = "SELECT count(1) FROM users WHERE email = ? AND password = ? " ;
        try {
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet r = ps.executeQuery();

            while(r.next()) {
                if(r.getInt(1) == 1) {
                    return true ;
                }
                else return false ;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false ;
    }

    public void registerUser(String name , String email , String phone , String password) {
        Connection c = this.getConnection();
        try {
            // Create a prepared statement with parameters for the user data
            String query = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = c.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, password);

            // Execute the query to insert the user data
            statement.executeUpdate();

            // Close the statement and connection
            statement.close();

        } catch (SQLException e) {
            // Handle any errors that occur during the database operation
            e.printStackTrace();
        }
    }

    public boolean checkEmailUniqueness(String email) {
        Connection c = this.getConnection();
        String query = "SELECT count(1) FROM users WHERE email = ? " ;
        try {
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, email);
            ResultSet r = ps.executeQuery();

            while(r.next()) {
                if(r.getInt(1) == 1) {
                    return true ;
                }
                else return false ;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false ;
    }


    // Not used since not needed .
    public String getName(String email) {
        Connection c = this.getConnection();
        String query = "SELECT name FROM users WHERE email = ?";
        String name = "";
        try {
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, email);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                name = r.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return name;
    }

    public ArrayList<Object> getProducts() {
        Connection c = this.getConnection();
        String query = "SELECT * FROM product";
        ArrayList<Object> products = new ArrayList<Object>();
        try {
            PreparedStatement ps = c.prepareStatement(query);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                int id = r.getInt("id");
                String name = r.getString("name");
                double price = r.getDouble("price");
                int quantity = r.getInt("quantity");

                Object o = CoffeeShopFactory.createItem(id,name,price);

                products.add(o);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public void addOrderItem(int orderID, int productID, int quantity, double price) {
        Connection c = this.getConnection();
        try {
            // Check if there is enough quantity in the product table
            String checkQuery = "SELECT quantity FROM product WHERE id = ?";
            PreparedStatement checkStatement = c.prepareStatement(checkQuery);
            checkStatement.setInt(1, productID);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                int availableQuantity = resultSet.getInt("quantity");
                if (quantity > availableQuantity) {
                    // Display an alert if there is not enough quantity
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Not Enough Quantity");
                    alert.setHeaderText("The requested quantity is not available");
                    alert.setContentText("There are only " + availableQuantity + " items available");
                    alert.showAndWait();
                    return;
                }
            }

            // Create a prepared statement with parameters for the order item data
            String query = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = c.prepareStatement(query);
            statement.setInt(1, orderID);
            statement.setInt(2, productID);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);

            // Execute the query to insert the order item data
            statement.executeUpdate();

            // Update the quantity of the corresponding product in the product table
            query = "UPDATE product SET quantity = quantity - ? WHERE id = ?";
            statement = c.prepareStatement(query);
            statement.setInt(1, quantity);
            statement.setInt(2, productID);
            statement.executeUpdate();

            // Close the statement and connection
            statement.close();

            // Show a success message or redirect to another page

        } catch (SQLException e) {
            // Handle any errors that occur during the database operation
            e.printStackTrace();
        }
    }



    // it returns the id of the created method
    public int createOrder() {
        int orderId = -1;
        Connection c = this.getConnection();
        try {
            String query = "INSERT INTO orders (date, total) VALUES (NOW(), 0)";
            PreparedStatement statement = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
            statement.close();
            // since it will close all the database connection and lead to errors .
            //c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    // get the sum of orders for related id and update the total for this order .
    public double getOrderTotal(int orderId) {
        double orderTotal = 0.0;
        Connection c = this.getConnection();
        try {
            // Create a prepared statement with parameters for the order id
            String query = "SELECT SUM(price * quantity) as order_total FROM order_items WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);

            // Execute the query to retrieve the order total
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                orderTotal = resultSet.getDouble("order_total");
            }

            // Check if the order total is zero before updating the total column in the orders table
            if (orderTotal != 0) {
                String updateQuery = "UPDATE orders SET total = ? WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setDouble(1, orderTotal);
                updateStatement.setInt(2, orderId);
                updateStatement.executeUpdate();
                updateStatement.close();
            }

            // Close the result set and statements
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            // Handle any errors that occur during the database operation
            e.printStackTrace();
        }
        return orderTotal;
    }


    public void setPaymentType(int orderId, String paymentType) {
        Connection c = this.getConnection();
        try {
            String updateQuery = "UPDATE orders SET payment_type = ? WHERE id = ?";
            PreparedStatement updateStatement = c.prepareStatement(updateQuery);
            updateStatement.setString(1, paymentType);
            updateStatement.setInt(2, orderId);
            updateStatement.executeUpdate();
            updateStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // This method deletes the specific order with all it's related items .
    public void deleteOrder(int orderId) {
        Connection c = null;
        try {
            c = this.getConnection();

            // Disable auto-commit to start a transaction
            c.setAutoCommit(false);

            // Delete all related records from other tables
            String deleteOrderItemsQuery = "DELETE FROM order_items WHERE order_id = ?";
            PreparedStatement deleteOrderItemsStatement = c.prepareStatement(deleteOrderItemsQuery);
            deleteOrderItemsStatement.setInt(1, orderId);
            deleteOrderItemsStatement.executeUpdate();

            // Delete the order record from the orders table
            String deleteOrderQuery = "DELETE FROM orders WHERE id = ?";
            PreparedStatement deleteOrderStatement = c.prepareStatement(deleteOrderQuery);
            deleteOrderStatement.setInt(1, orderId);
            deleteOrderStatement.executeUpdate();

            // Commit the transaction
            c.commit();

            // Close the resources
            deleteOrderItemsStatement.close();
            deleteOrderStatement.close();

        } catch (SQLException e) {
            // Rollback the transaction in case of any errors
            try {
                if (c != null) {
                    c.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    //c.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public ArrayList<String> getAllOrders() {
        ArrayList<String> orders = new ArrayList<>();
        Connection c = this.getConnection();
        try {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id, date, total, payment_type FROM orders");
            while (resultSet.next()) {
                int orderId = resultSet.getInt("id");
                String date = resultSet.getString("date");
                double total = resultSet.getDouble("total");
                String paymentType = resultSet.getString("payment_type");
                String orderString = String.format("Order #%d - Date: %s, Total: $%.2f, Payment Type: %s", orderId, date, total, paymentType);
                orders.add(orderString);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    // Product manager Controller
    // add product to product table
    public void insertProduct(String type, double price, int quantity) {
        // connection
        Connection c = this.getConnection();
        try {
            // Check if the product already exists in the database
            String selectQuery = "SELECT * FROM product WHERE name=?";
            PreparedStatement selectStatement = c.prepareStatement(selectQuery);
            selectStatement.setString(1, type);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Product already exists, update its quantity and price
                int existingQuantity = resultSet.getInt("quantity");
                double existingPrice = resultSet.getDouble("price");
                int newQuantity = existingQuantity + quantity;
                double newPrice = price;
                String updateQuery = "UPDATE product SET quantity=?, price=? WHERE name=?";
                PreparedStatement updateStatement = c.prepareStatement(updateQuery);
                updateStatement.setInt(1, newQuantity);
                updateStatement.setDouble(2, newPrice);
                updateStatement.setString(3, type);
                updateStatement.executeUpdate();
                updateStatement.close();
            }
            else {
                // Product does not exist, insert a new record
                String insertQuery = "INSERT INTO product (name, price, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = c.prepareStatement(insertQuery);
                insertStatement.setString(1, type);
                insertStatement.setDouble(2, price);
                insertStatement.setInt(3, quantity);
                insertStatement.executeUpdate();
                insertStatement.close();
            }

            // Close the statement and connection
            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            // Handle any errors that occur during the database operation
            e.printStackTrace();
        }
    }

    public void deleteAllOrders() throws SQLException {
        // Get a connection to the database
        Connection connection = this.getConnection();

        // Create the SQL statements
        PreparedStatement deleteOrderItems = connection.prepareStatement("DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders)");
        PreparedStatement deleteOrders = connection.prepareStatement("DELETE FROM orders");

        try {
            // Disable auto-commit to allow transaction
            connection.setAutoCommit(false);

            // Delete order items first (to avoid foreign key constraint)
            deleteOrderItems.executeUpdate();

            // Delete orders
            deleteOrders.executeUpdate();

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback transaction on error
            connection.rollback();
            throw e;
        } finally {
            // Restore auto-commit
            connection.setAutoCommit(true);
            //connection.close(); // Close the connection
        }
    }
}
