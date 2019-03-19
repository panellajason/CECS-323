
package jdbc_project;
import java.sql.*;
import java.util.Scanner;
/**
 *
 * @author Jason Panella and David Garza
 */
public class main {
    static String USER;
    static String PASS;
    static String DBNAME;
   
    static final String WRITING_DISPLAY_FORMAT ="%-25s%-25s%-25s%-20s\n";
    static final String PUBLISHER_DISPLAY_FORMAT = "%-25s%-25s%-25s%-20s\n";
    static final String BOOK_DISPLAY_FORMAT = "%-25s%-25s%-20s%-20s%-4s\n";
    
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";
    static Scanner in = new Scanner(System.in);
    static Scanner in2 = new Scanner(System.in);


    static Connection conn = null; 
    static Statement stmt = null;  
    static PreparedStatement prepStmt = null;
    
    public static void main(String[] args) {
        
        validateDB();
        
        try {

            menu();

        } catch (Exception e) {
            System.out.println("Please restart the program and try again.");
            System.exit(0);
            
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
                if(in != null)
                    in.close();
                if(in2 != null)
                    in2.close();
            } catch (SQLException se) {
                System.out.println("Please restart the program and try again.");
                System.exit(0);
            }
            try {
                if (prepStmt != null) {
                    prepStmt.close();
                }
            } catch (SQLException se) {
                System.out.println("Please restart the program and try again.");
                System.exit(0);
            }
        }
        System.out.println("Goodbye!");
    }
   
    
    
//-------------------------------------Functions-----------------------------------------------------------------------------    
    
    //#1
    public static void listAllWritingGroups() {
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM writing_groups";
            ResultSet rs = stmt.executeQuery(sql); 
            
            System.out.println("-----All Writing Groups-----");
            System.out.printf(WRITING_DISPLAY_FORMAT, "Name", "Head Writer", "Year Formed", "Subject");
            while (rs.next()) {
                String gname = rs.getString("group_name");
                String hname = rs.getString("head_writer");
                String year = rs.getString("year_formed");
                String sub = rs.getString("subject");
               
                System.out.printf(WRITING_DISPLAY_FORMAT,
                        dispNull(gname), dispNull(hname), dispNull(year), dispNull(sub));                                  
            }
            } catch (SQLException se) {
                System.out.println("Please restart program.");
                System.exit(0);
            } 
        System.out.println("----------------------------");
        System.out.println("");
    }
    
    //#2
    public static void displaySpecifiedWritingGroup() {
        Scanner scan = new Scanner(System.in);
        
        try {
            System.out.println("Choose a writing group: ");
            String userInput;

            String displayGroup = "%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n";

            userInput = scan.nextLine();
            System.out.println("");
            
            prepStmt = conn.prepareStatement("SELECT * FROM writing_groups NATURAL JOIN books NATURAL JOIN publishers WHERE group_name =?");
            prepStmt.setObject(1, userInput);

            ResultSet rs = prepStmt.executeQuery();

            while(!rs.next()) {
               System.out.println("No records in database match your query. Writing group may have not written a book.");
               System.out.println("Choose a writing group: ");
               userInput = scan.nextLine();
               System.out.println("");
               
               prepStmt = conn.prepareStatement("SELECT * FROM writing_groups NATURAL JOIN books NATURAL JOIN publishers WHERE group_name =?");
               prepStmt.setObject(1, userInput);
               rs = prepStmt.executeQuery();
            }
               rs = prepStmt.executeQuery();

            System.out.printf(displayGroup, "Publisher Name", "Group Name", "Head Writer", "Year Formed", "Subject", 
                    "Book Title", "Year Published", "Number Pages", "Publisher Address", "Publisher Phone", "Publisher Email");

            while (rs.next()) {
                String pname = rs.getString("publisher_name");
                String gname = rs.getString("group_name");
                String hname = rs.getString("head_writer");
                String year = rs.getString("year_formed");
                String sub = rs.getString("subject");
                String btitle = rs.getString("book_title");
                String ypublished = rs.getString("year_published");
                String npages = rs.getString("number_pages");
                String paddress = rs.getString("publisher_address");
                String pphone = rs.getString("publisher_phone");
                String pemail = rs.getString("publisher_email");

                System.out.printf(displayGroup, dispNull(pname), dispNull(gname), dispNull(hname), dispNull(year), dispNull(sub),
                        dispNull(btitle), dispNull(ypublished), dispNull(npages), dispNull(paddress), dispNull(pphone), dispNull(pemail));
            }
        } catch (SQLException e) {
            System.out.println("Please try again");
            System.exit(0);            
        }
        
        System.out.println("");
    
    }
    //#3
    public static void listAllPublishers () {
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM publishers";
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("------All Publishers------");
            System.out.printf(PUBLISHER_DISPLAY_FORMAT, "Name", "Address", "Phone", "Email");
            while (rs.next()) {
                String name = rs.getString("publisher_name");
                String address = rs.getString("publisher_address");
                String phone = rs.getString("publisher_phone");
                String email = rs.getString("publisher_email");

                System.out.printf(PUBLISHER_DISPLAY_FORMAT,
                        dispNull(name), dispNull(address), dispNull(phone), dispNull(email));
            }
            } catch (SQLException se) {
                System.out.println("Please restart program.");
                System.exit(0);
            }
            System.out.println("---------------------------");
            System.out.println("");
    }
    
    //#4
    public static void displaySpecifiedPublisher () {
        Scanner scan = new Scanner(System.in);
        try {
            prepStmt = conn.prepareStatement("SELECT * FROM publishers NATURAL JOIN books NATURAL JOIN writing_groups where publisher_name = ?");
            System.out.println("Choose a publisher:");
            String userInput = scan.nextLine();
            System.out.println("");
            prepStmt.setObject(1, userInput);

            String displayPublisher = "%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n";

            ResultSet rs = prepStmt.executeQuery();

            while(!rs.next()) { 
               System.out.println("No records in database match your query. Publisher may have never published a book.\n");
               System.out.println("Choose a publisher:");
               userInput = scan.nextLine();
               System.out.println("");
               
               prepStmt = conn.prepareStatement("SELECT * FROM publishers NATURAL JOIN books NATURAL JOIN writing_groups where publisher_name = ?");
               prepStmt.setObject(1, userInput);
               rs = prepStmt.executeQuery();
            }
            rs = prepStmt.executeQuery();
            
            System.out.printf(displayPublisher, "Group Name", "Name", "Address", "Phone", "Email", "Book Title", "Year Published",
                        "Pages", "Writer", "Year Formed", "Subject");

            while (rs.next()) {
                String gname = rs.getString("group_name");
                String name = rs.getString("publisher_name");
                String address = rs.getString("publisher_address");
                String phone = rs.getString("publisher_phone");
                String email = rs.getString("publisher_email");
                String book = rs.getString("book_title");
                String year = rs.getString("year_published");
                String pages = rs.getString("number_pages");
                String hwriter = rs.getString("head_writer");
                String yearform = rs.getString("year_formed");
                String subject = rs.getString("subject");

                System.out.printf(displayPublisher, dispNull(gname), dispNull(name), dispNull(address), 
                        dispNull(phone), dispNull(email), dispNull(book), dispNull(year), dispNull(pages), dispNull(hwriter), dispNull(yearform),
                        dispNull(subject));
            }
        } catch (SQLException e) {
            System.out.println("Please try again");
            System.exit(0);
        }
        System.out.println("");
    }
    
    //#5
    public static void listAllBooks () {
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM books";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("--------All Books-------");
            System.out.printf(BOOK_DISPLAY_FORMAT, "Group Name", "Book Title", "Publisher Name", "Year Published", "Number of Pages");
            while (rs.next()) {
                String name = rs.getString("group_name");
                String title = rs.getString("book_title");
                String pubname = rs.getString("publisher_name");
                String year = rs.getString("year_published");
                String pages = rs.getString("number_pages");

                System.out.printf(BOOK_DISPLAY_FORMAT, dispNull(name), dispNull(title), dispNull(pubname), dispNull(year), dispNull(pages));
            }
            } catch (SQLException se) {
                System.out.println("Please restart program.");
                System.exit(0);
            }
            System.out.println("-------------------------");
            System.out.println("");
    }
    
    //#6
    public static void displaySpecifiedBook() {
        Scanner scan = new Scanner(System.in);
        
        try {
            
            System.out.println("Choose a Book Title");
            String userInput = scan.nextLine();
            
            String sql = "SELECT publisher_name, group_name, book_title, year_published, number_pages, head_writer, year_formed,"
                    + "subject, publisher_address, publisher_phone, publisher_email FROM books "
                    + "NATURAL JOIN writing_groups NATURAL JOIN publishers WHERE book_title = " + "\'" + userInput + "\'";
            
            String displayBooks = "%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            while(!rs.next()) { 
                System.out.println("No records in database match your query.");
                System.out.println("Choose a Book Title");
                userInput = scan.nextLine();
                System.out.println("");
                
                sql = "SELECT publisher_name, group_name, book_title, year_published, number_pages, head_writer, year_formed,"
                    + "subject, publisher_address, publisher_phone, publisher_email FROM books "
                    + "NATURAL JOIN writing_groups NATURAL JOIN publishers WHERE book_title = " + "\'" + userInput + "\'";
;
                rs = stmt.executeQuery(sql);
            }
            
            System.out.printf(displayBooks, "Publisher Name", "Group Name", "Book Title", "Year Published", "Pages", "Head Writer", "Year Formed",
                                        "Subject", "Address", "Phone", "Email");
                String name = rs.getString("publisher_name");
                String gname = rs.getString("group_name");
                String book = rs.getString("book_title");
                String year = rs.getString("year_published");
                String pages = rs.getString("number_pages");
                String hwriter = rs.getString("head_writer");
                String yearform = rs.getString("year_formed");
                String subject = rs.getString("subject");
                String address = rs.getString("publisher_address");
                String phone = rs.getString("publisher_phone");
                String email = rs.getString("publisher_email");

                System.out.printf(displayBooks, dispNull(name), dispNull(gname), dispNull(book), dispNull(year), dispNull(pages), 
                        dispNull(hwriter), dispNull(yearform), dispNull(subject), dispNull(address), dispNull(phone),
                        dispNull(email));
                        
        }catch (SQLException se) {
            System.out.println("Please try again");
            System.exit(0);
        }
        System.out.println("");
    }

    
    //#7
    public static void insertBook () {
        Scanner scan = new Scanner(System.in);

        Boolean correct = false;
        String groupName = "";
        String bookTitle = "";
        String publisherName = "";
        String yearPublished = "";
        String numOfPages = "";

        try {
          do{
            System.out.println("Enter the writing group: ");
            groupName = scan.nextLine();

            prepStmt = conn.prepareStatement("SELECT * FROM writing_groups where group_name = ?");
            prepStmt.setObject(1, groupName);

            ResultSet rs = prepStmt.executeQuery();

            if(!rs.next()) {
               System.out.println("No records in database match your query.\n");
               listGroupNames();
            }
            else
               correct = true;

          } while (!correct);

          correct = false;
          do{
            System.out.println("Enter the book title: ");
            bookTitle = scan.nextLine();
            
            prepStmt = conn.prepareStatement("SELECT * FROM books where book_title = ?");
            prepStmt.setObject(1, bookTitle);

            ResultSet rs = prepStmt.executeQuery();

            if(!rs.next()) {
                if(bookTitle.length() <= 20)
                    correct = true;
                else
                    System.out.println("Exceeds maximum length (20) Try again.\n");
 
            }
            else
               System.out.println("Book already exists, try again.\n");
  
          } while (!correct);

          correct = false;
          do{
            System.out.println("Enter the publisher name: ");
            publisherName = scan.nextLine();

            prepStmt = conn.prepareStatement("SELECT * FROM publishers where publisher_name = ?");
            prepStmt.setObject(1, publisherName);

            ResultSet rs = prepStmt.executeQuery();

            if(!rs.next()) {
               System.out.println("No records in database match your query.\n");
               listPublisherNames();
            }
            else
               correct = true;

          } while (!correct);

          correct = false;
          do{
            System.out.println("Enter the year published: ");
            yearPublished = scan.nextLine();
            
            try {
                Integer.parseInt(yearPublished);
            } catch(NumberFormatException e) { 
                System.out.println("Please enter a year (yyyy)\n");
                continue; 
            } catch(NullPointerException e) {
                System.out.println("Please try again\n");
                continue;
            }
        
             if(yearPublished.length() > 4)
                System.out.println("Exceeds maximum length (4) Try again.\n");
            else
                correct = true;
             
          } while (!correct);

          correct = false;
          do{
            System.out.println("Enter the number of pages: ");
            numOfPages = scan.nextLine();
        
            try {
                Integer.parseInt(numOfPages);
            } catch(NumberFormatException e) { 
                System.out.println("Please enter a number.\n");
                continue; 
            } catch(NullPointerException e) {
                System.out.println("Please try again\n");
                continue;
            }
            
            if(numOfPages.length() > 5)
                System.out.println("Exceeds maximum number (99999) Try again.\n");
            else
                correct = true;
             
          } while (!correct);

           String query = "Insert into books (group_name, book_title, publisher_name, year_published, number_pages)"
              + " values (?, ?, ?, ?, ?)";

            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, groupName);
            preparedStmt.setString (2, bookTitle);
            preparedStmt.setString (3, publisherName);
            preparedStmt.setString (4, yearPublished);
            preparedStmt.setString (5, numOfPages);

            preparedStmt.executeUpdate();


        } catch (SQLException e) {
           System.out.println("Please try again.\n");
           System.exit(0);
        }
        try {
            prepStmt = conn.prepareStatement("SELECT * FROM books where book_title = ?");
            prepStmt.setObject(1, bookTitle);

            ResultSet rs = prepStmt.executeQuery();

            if(!rs.next()) 
               System.out.println("No records in database match your query.\n");
            else
               rs = prepStmt.executeQuery();
            
            System.out.println("\n-------Your inserted book-------");
            System.out.printf(BOOK_DISPLAY_FORMAT, "Group Name", "Book Title", "Publisher Name", "Year Published", "Number of Pages");
            while (rs.next()) {
                String name = rs.getString("group_name");
                String title = rs.getString("book_title");
                String pubname = rs.getString("publisher_name");
                String year = rs.getString("year_published");
                String pages = rs.getString("number_pages");

                System.out.printf(BOOK_DISPLAY_FORMAT, dispNull(name), dispNull(title), dispNull(pubname), dispNull(year), dispNull(pages));
            }
        } catch (SQLException se) {
            System.out.println("Please restart the program.");
            System.exit(0);
        }
        System.out.println("----------------------------");
        System.out.println("");
    }
    
    //#8
    public static void insertPublisher() {
        Scanner scan = new Scanner(System.in);
        String oldName = "";

        Boolean correct = false;
        String name = "";
        String address = "";
        String phone = "";
        String email = "";
        
        try {
            do{
                System.out.println("Enter the publisher's name: ");
                name = scan.nextLine();

                prepStmt = conn.prepareStatement("SELECT * FROM publishers where publisher_name = ?");
                prepStmt.setObject(1, name);

                ResultSet rs = prepStmt.executeQuery();

                if(!rs.next()) {
                    if(name.length() <= 20)
                        correct = true;
                    else
                        System.out.println("Exceeds maximum length (20) Try again.\n");

                }
                else
                   System.out.println("Publisher already exists, try again.\n");

              } while (!correct);

            correct = false;
            do{
                System.out.println("Enter the publisher's address: ");
                address = scan.nextLine();

                 if(address.length() > 20)
                    System.out.println("Exceeds maximum length (20) Try again.\n");
                else
                    correct = true;

              } while (!correct);

            correct = false;
            do{
                System.out.println("Enter the publisher's phone number: ");
                phone = scan.nextLine();

                 if(phone.length() > 10)
                    System.out.println("Exceeds maximum length (10) Try again.\n");
                else
                    correct = true;

              } while (!correct);

            correct = false;
            do{
                System.out.println("Enter the publisher's email: ");
                email = scan.nextLine();

                 if(email.length() > 20)
                    System.out.println("Exceeds maximum length (20) Try again.\n");
                else
                    correct = true;

              } while (!correct);

               String query = "Insert into publishers (publisher_name, publisher_address, publisher_phone, publisher_email)"
                  + " values (?, ?, ?, ?)";

                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, name);
                preparedStmt.setString (2, address);
                preparedStmt.setString (3, phone);
                preparedStmt.setString (4, email);

                preparedStmt.executeUpdate();

                correct = false;
                do{
                  System.out.println("Which publisher would you like to replace with the new one?");
                  oldName = scan.nextLine();
                  System.out.println("");

                  prepStmt = conn.prepareStatement("SELECT * FROM publishers where publisher_name = ?");
                  prepStmt.setObject(1, oldName);

                  ResultSet rs = prepStmt.executeQuery();

                  if(!rs.next()) {
                     System.out.println("No records in database match your query.\n");
                     listPublisherNames();
                  }
                  else
                     correct = true;

                } while (!correct);

            prepStmt = conn.prepareStatement("UPDATE books SET publisher_name = ? WHERE publisher_name = ?");
            prepStmt.setObject(1, name);
            prepStmt.setObject(2, oldName);
            prepStmt.executeUpdate();

        } catch (SQLException e) {
           System.out.println("Please try again.\n");
           System.exit(0);
        }
        try {
            prepStmt = conn.prepareStatement("Delete FROM publishers where publisher_name = ?");
            prepStmt.setObject(1, oldName);

            prepStmt.executeUpdate();
    
        } catch (SQLException e) {
            System.out.println("Please try again");
            System.exit(0);
        }
        
        listAllPublishers();
    }
    
    
    //#9
    public static void deleteBook() {
        Scanner scan = new Scanner(System.in);
        String input = "";

        try {
            Boolean valid = false;
            do {
                System.out.println("Enter book title in which you want to delete: ");
                input = scan.nextLine();
                System.out.println("");
                
                prepStmt = conn.prepareStatement("SELECT * FROM books where book_title = ?");
                prepStmt.setObject(1, input);

                ResultSet rs = prepStmt.executeQuery();

                if(!rs.next()) 
                   System.out.println("No records in database match your query.\n");
                else
                   valid = true;
            } while (!valid);
            
            prepStmt = conn.prepareStatement("Delete FROM books where book_title = ?");
            prepStmt.setObject(1, input);

            prepStmt.executeUpdate();

            
        } catch (SQLException e) {
            System.out.println("Please try again");
            System.exit(0);            
        }
         listAllBooks();   
    }
    
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    } 
    
    public static void listGroupNames() {
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM writing_groups";
            ResultSet rs = stmt.executeQuery(sql);        
            
            System.out.println("-----Writing Group Names-----");
            while (rs.next()) {
                System.out.println(dispNull(rs.getString("group_name")));                                  
            }
            System.out.println("-----------------------------");


            } catch (SQLException se) {
                System.out.println("Please restart program.");
                System.exit(0);
            }  
        System.out.println("");
    }
    
    public static void listPublisherNames() {
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM publishers";
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("-----Publisher Names-----");
            while (rs.next()) {
                System.out.println(dispNull(rs.getString("publisher_name")));
            }
            System.out.println("-------------------------");

            } catch (SQLException se) {
                System.out.println("Please restart program.");
                System.exit(0);
            }
            System.out.println("");
    }
    
    public static void menu() {
        boolean quit = false;
        String menuItem = "";

        do {
            System.out.println("\n-------Menu-------");
            System.out.println("1. List all writing groups");
            System.out.println("2. List data for writing group of your choice (and books and publishers)");
            System.out.println("3. List all publishers");
            System.out.println("4. List data for publisher of your choice (and books and writing groups)");
            System.out.println("5. List all books");
            System.out.println("6. List data for book of your choice (and publisher and writing group");
            System.out.println("7. Insert a new book");
            System.out.println("8. Insert a new publisher (and update all books with the new publisher)");
            System.out.println("9. Remove a single book of your choice");
            System.out.println("10. Quit");
            System.out.println("-----------------\n");

            System.out.print("Choose menu item: ");

              menuItem = in.next();
              System.out.println("");

              switch (menuItem) {

              case "1":
                    listAllWritingGroups();
                    System.out.println("Press Enter to continue");
                    in2.nextLine();
                    break;
              case "2":
                    displaySpecifiedWritingGroup();
                    System.out.println("Press Enter to continue");
                    in2.nextLine();
                    break;
              case "3":
                    listAllPublishers();
                    System.out.println("Press Enter to continue");
                    in2.nextLine();
                    break;
              case "4":
                    displaySpecifiedPublisher();
                    System.out.println("Press Enter to continue");
                    in2.nextLine();
                    break;
              case "5":
                    listAllBooks();
                    System.out.println("Press Enter to continue");
                    in2.nextLine();
                    break;
              case "6":
                  displaySpecifiedBook();
                  System.out.println("Press Enter to continue");
                  in2.nextLine();
                  break;

              case "7":
                  insertBook();
                  System.out.println("Press Enter to continue");
                  in2.nextLine();
                  break;

              case "8":
                  insertPublisher();
                  System.out.println("Press Enter to continue");
                  in2.nextLine();
                  break;

              case "9":
                  deleteBook();
                  System.out.println("Press Enter to continue");
                  in2.nextLine();
                  break;
              case "10":
                    quit = true;
                    break;
              default:
                    System.out.println("Invalid choice.");
              }
        } while (!quit);
    }
    
    public static void validateDB () {
  
        System.out.print("Name of the database (not the user account): ");
        DBNAME = in.nextLine();
        System.out.print("Database user name: ");
        USER = in.nextLine();
        System.out.print("Database password: ");
        PASS = in.nextLine();

        DB_URL = DB_URL + DBNAME + ";user="+ USER + ";password=" + PASS;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException se) {
            
            System.out.println("No connection to database or incorrect database name. Try again"); 
            System.exit(0);

        } catch (Exception e) {
            System.out.println("No connection to database or incorrect database name. Try again");
            System.exit(0);
        } 
        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM writing_groups";
            stmt.executeQuery(sql);  
        } catch(SQLException e) {
            System.out.println("Username, password, or schema incorrect. Restart the program.");
            System.exit(0);
        }
    }      
}



