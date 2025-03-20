# azul-test-task

This is a test task for Software Intern position at Azul.

## How to run? 
In the main folder, run
```
docker-compose build && docker-compose up
```
The app will be available at port 8081.  
The documentation will be available at http://localhost:8081/swagger-ui/index.html
## Task: Bookstore Inventory Management System

### Overview:

As a contractor, you are tasked with designing and developing the first version of the
Bookstore Inventory Management System for a client. The system is a web application
(REST API only) that enables bookstore owners to efficiently manage their inventory. It
offers features for adding, updating, and searching books. The system will be developed
using a Java technology or framework of your choice.
You should aim to complete the solution within 6 hours, but it’s up to you to decide how
much time to invest in solving the problem.
Approach this project as if you were working for a real client. Consider the future
implications of your decisions and how they might affect the project's evolution.
Note: If you have any questions or concerns, do not hesitate to reach out.
Key Features:

1. Book CRUD Operations [REST API]:
   1. Add new books, including details such as title, author, genre, and price.
   2. Update existing book information.
   3. Delete books from the inventory.
2. Search Functionality [REST API]:
   1. Search for books by title, author, or genre.
   2. Display search results in a paginated format.
3. Authentication and Authorization:
   1. Implement basic authentication for bookstore staff.
   2. Differentiate between admin and regular users.
   3. Admin users can perform all CRUD operations, while regular users can
   only view books.
4. Database:
   1. Set up a database to store book information.
   2. Define the necessary tables and relationships (e.g., Book, Author, Genre).
