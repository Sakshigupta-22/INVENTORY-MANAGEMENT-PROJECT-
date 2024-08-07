package Inventorymanagement;

import java.sql.*;
import java.util.Scanner;

public class inventory {
    private static final String url = "jdbc:mysql://localhost:3306/";
    private static final String username = "root";
    private static final String password = "SG22@sakshi";


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully");
            //
            Scanner sc = new Scanner(System.in);
            System.out.println("do you want to create a new database(type YES) +" +
                    "if want to work on existing db set up by you (type NO)");
            String DBdecision=sc.next();
            if(DBdecision.equalsIgnoreCase("yes")){
                CreateDatabase(conn,sc);
            }
            System.out.println("Select database:");
            selectdb(conn,sc);
            System.out.println("want to create table(type YES) and (type No) if already created");
            String tabledecision=sc.next();
            if(tabledecision.equalsIgnoreCase("yes")){
                CreateTable(conn,sc);
                return;
            }

            while (true) {
                System.out.println();
                System.out.println("INVENTORY MANAGEMENT SYSTEM");

                System.out.println("1. insert inventory data for product booking");
                System.out.println("2. view inventory details");
                System.out.println("3. get product purchased");
                System.out.println("4. Update inventory data");
                System.out.println("5. delete product booking");
                System.out.println("6. get bill amount to be paid by a person");
                System.out.println("7. update product details: ");
                System.out.println("0. EXIT");
                System.out.println("choose an option...");
                int choice = sc.nextInt();


                switch (choice) {
                    case 1:
                        Insert(conn, sc);
                        break;
                    case 2:
                        ViewInventory(conn);
                        break;
                    case 3:
                        Getproductpurchased(conn, sc);
                        break;
                    case 4:
                        Update(conn, sc);
                        break;
                    case 5:
                        Deleterecord(conn, sc);
                        break;
                    case 7:
                        Updateproductdetails(conn,sc);
                        break;
                    case 6:
                        billamount(conn,sc);
                        break;
                    case 0:
                        Exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("invalid option selected");


                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
    private static void selectdb(Connection conn,Scanner sc)throws SQLException{
        System.out.println("enter the name of database you want to select:");
        String db_name=sc.next();
        String query="use  "+db_name+";";
        try{
            Statement stmt=conn.createStatement();
            stmt.execute(query);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void CreateDatabase(Connection conn,Scanner sc)throws SQLException{
        System.out.println("enter the name of database you want to create:");
        String db_name=sc.next();
        String query="CREATE database "+db_name+";";
        try(Statement stmt=conn.createStatement()){
            int rowsaffected=stmt.executeUpdate(query);
            if(rowsaffected>0){
                System.out.println("DataBase created successfully:)");
            }
            else{
                System.out.println("DataBase not created successfully or exist");
            }
        }
    }
    private static void CreateTable(Connection conn,Scanner sc)throws SQLException {
        System.out.println("enter the name of table you want to create:");
        String table_name = sc.next();
        String query = "CREATE TABLE " + table_name + " (customer_id INT AUTO_INCREMENT PRIMARY KEY," +
                "customer_name VARCHAR(256) NOT NULL ," +
                "product VARCHAR(256) NOT NULL," +
                "price DOUBLE NOT NULL," +
                "quantity INT NOT NULL," +
                "bill_amount DOUBLE ," +
                "book_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            boolean created = stmt.execute(query);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet res = metaData.getTables(null, null, table_name, new String[]{"TABLE"});
            if (res.next()) {
                System.out.println("Table created successfully:)");
            } else {
                System.out.println("table not created successfully or already exist");
            }
        }
    }
//    private static void Insert(Connection conn, Scanner sc) {
//        try {
//            System.out.println("enter the customer name: ");
//            String customername = sc.next();
//            sc.nextLine();
//            System.out.println("enter the product name: ");
//            String product = sc.next();
//            sc.nextLine();
//            System.out.println("enter the price: ");
//            double price = sc.nextDouble();
//            System.out.println("enter the quantity: ");
//            int quantity=sc.nextInt();
//
//            double billamount= price*quantity;
//
//
//            String query = "INSERT INTO inventory(customer_name,product,price,quantity,bill_amount)" +
//                    "values('" + customername + "',' " +product  + "', " +price  + "," + quantity  + ","+billamount+")";
//
//
//            try (Statement stmt = conn.createStatement()) {
//                int rowsaffected = stmt.executeUpdate(query);
//                if (rowsaffected > 0) {
//                    System.out.println("details inserted  successfully");
//                } else {
//                    System.out.println("insertion failed");
//                }
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    private static void Insert(Connection conn, Scanner sc) {
        try {


            conn.setAutoCommit(false);
            String query = "INSERT INTO inventory(customer_name,product,price,quantity,bill_amount)" +
                    "values(?,?,?,?,?)";

                    PreparedStatement ps = conn.prepareStatement(query);
                    while(true) {

                        System.out.println("enter the customer name: ");
                        String customername = sc.next();
                        sc.nextLine();
                        System.out.println("enter the product name: ");
                        String product = sc.next();
                        sc.nextLine();
                        System.out.println("enter the price: ");
                        double price = sc.nextDouble();
                        System.out.println("enter the quantity: ");
                        int quantity=sc.nextInt();

                        double billamount= price*quantity;
                        ps.setString(1, customername);
                        ps.setString(2, product);
                        ps.setDouble(3, price);
                        ps.setInt(4, quantity);
                        ps.setDouble(5, billamount);
                        ps.addBatch();
                        System.out.println("Add more values: ");
                        String decision=sc.next();
                        if(decision.equalsIgnoreCase("N")){
                            break;
                        }

                    }
                    int[] batchResult=ps.executeBatch();
                    conn.commit();
                    System.out.println("Batch executed  and data inserted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void ViewInventory(Connection conn) throws SQLException {
        String query = "SELECT customer_id , customer_name,product, price,quantity,bill_amount,book_date FROM  inventory";

        try (Statement stmt = conn.createStatement()) {
            ResultSet res = stmt.executeQuery(query);

            System.out.println("Current Reservation: ");
            System.out.println("----------------+---------------------+---------------+-----------------+-----------------+----------------+----------------");
            System.out.println("customer_id     |customer_name        |product        |price            |quantity         |Bill_amount     |book_date");
            System.out.println("----------------+---------------------+---------------+-----------------+-----------------+-----------------+---------------");
            while (res.next()) {
                int id = res.getInt("customer_id");
                String customername = res.getString("customer_name");
                int quantity = res.getInt("quantity");
                String product = res.getString("product");
                double price = res.getDouble("price");
                double billamount = res.getDouble("bill_amount");
                String bookdate = res.getTimestamp("book_date").toString();

                System.out.printf("| %-14d | %-14s | %-18s | %-15f | %-14d | %-19f | %-18s    |\n",
                        id, customername, product, price, quantity, billamount, bookdate);
            }
            System.out.println("----------------+---------------------+---------------+------------------+----------------+-----------------+--------------");
        }
    }
    public static void Getproductpurchased(Connection conn,Scanner sc){
            try{
                System.out.println("Enter the customer id: ");
                int id=sc.nextInt();
                System.out.println("Enter the customer name: ");
                String customername=sc.next();
                String query= "SELECT product,quantity FROM inventory " +
                        "WHERE customer_id = " + id +
                        " AND customer_name = '" + customername + "'";
                try(Statement stmt=conn.createStatement();
                    ResultSet res = stmt.executeQuery(query)){
                    if (res.next()) {
                        int quantity = res.getInt("quantity");
                        String product=res.getString("product");
                        System.out.println("Quantity and Product for customer_id " + id + " and CustomerName " + customername + " is : " + product+":("+quantity+")");
                    } else {
                        System.out.println("inventory record not found for given id and customername");
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();

            }

    }
    public static void Update(Connection conn ,Scanner sc){
        try {
            System.out.println("Enter the customer id to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            if (!customerExist(conn, id)) {
                System.out.println("customer not found");
                return;
            }
            System.out.println("enter the customername: ");
            String newcustomername=sc.nextLine();
            System.out.println("enter the product: ");
            String newproduct=sc.nextLine();
            System.out.println("enter the price: ");
            double newprice=sc.nextDouble();
            System.out.println("enter the quantity: ");
            int newquantity=sc.nextInt();
            double newbill_amount=newprice*newquantity;





            String query = "UPDATE inventory SET customer_name='"+ newcustomername +"',"+
                    " product=' " + newproduct + " ', "+
                    " price= "+newprice+","+
                    " quantity= "+newquantity+","+
                    "bill_amount="+newbill_amount+
                    " WHERE customer_id= "+id;
            try(Statement stmt=conn.createStatement()){
                int rowsaffected=stmt.executeUpdate(query);
                if(rowsaffected>0){
                    System.out.println("details updated successfully");
                }
                else{
                    System.out.println("details not updated");
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public static void Deleterecord(Connection conn,Scanner sc)throws SQLException{

        try {
            System.out.println("Enter the customer id to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            if (!customerExist(conn, id)) {
                System.out.println("customer not found");
                return;
            }





            String query = "DELETE FROM inventory WHERE customer_id="+id;
            try(Statement stmt=conn.createStatement()){
                int rowsaffected=stmt.executeUpdate(query);
                if(rowsaffected>0){
                    System.out.println("details deleted successfully");
                }
                else{
                    System.out.println("details not deleted");
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void billamount(Connection conn,Scanner sc){
            try{
                System.out.println("Enter the customer id: ");
                int id=sc.nextInt();
                System.out.println("Enter the customer name: ");
                String customername=sc.next();
                String query= "SELECT bill_amount FROM inventory " +
                        "WHERE customer_id = " + id +
                        " AND customer_name = '" + customername + "'";
                try(Statement stmt=conn.createStatement();
                    ResultSet res = stmt.executeQuery(query)){
                    if (res.next()) {
                        double billamount = res.getDouble("bill_amount");

                        System.out.println("BILL_AMOUNT for customer_id " + id + " and CustomerName " + customername + " is : " + billamount);
                    } else {
                        System.out.println("inventory record not found for given id and customername");
                    }
                }
            }
            catch(SQLException e){
                e.printStackTrace();

            }

    }



    public static boolean customerExist(Connection conn,int id){
        try{
            String query="SELECT customer_id FROM inventory WHERE customer_id="+id;
            try(Statement stmt=conn.createStatement()){
                ResultSet res=stmt.executeQuery(query);
                return res.next();

            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public static void Exit()throws InterruptedException{
        System.out.println("Exiting");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println("Thankyou for using inventory management System!! :)");
    }
    public static void Updateproductdetails(Connection conn ,Scanner sc){
        try {
            System.out.println("Enter the customer id to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            if (!customerExist(conn, id)) {
                System.out.println("customer not found");
                return;
            }

            System.out.println("enter the product: ");
            String newproduct=sc.nextLine();
            System.out.println("enter the price: ");
            double newprice=sc.nextDouble();
            System.out.println("enter the quantity: ");
            int newquantity=sc.nextInt();
            double newbill_amount=newprice*newquantity;





            String query = "UPDATE inventory SET product=' " + newproduct + " ', "+
                    " price= "+newprice+","+
                    " quantity= "+newquantity+","+
                    "bill_amount="+newbill_amount+
                    " WHERE customer_id= "+id;
            try(Statement stmt=conn.createStatement()){
                int rowsaffected=stmt.executeUpdate(query);
                if(rowsaffected>0){
                    System.out.println("details updated successfully");
                }
                else{
                    System.out.println("details not updated");
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
