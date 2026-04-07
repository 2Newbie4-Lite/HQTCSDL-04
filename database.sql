-- Tạo database
create database ShopDepTrai;
go

use ShopDepTrai;
go

-- Bảng danh mục
create table Categories (
    CategoryID int primary key identity(1,1),
    Name nvarchar(100) not null,
    Description nvarchar(255)
);

-- Bảng sản phẩm
create table Products (
    ProductID int primary key identity(1,1),
    CategoryID int foreign key references Categories(CategoryID),
    Name nvarchar(200) not null,
    Price decimal(18,2) not null,
    Stock int default 0,
    ImageURL nvarchar(500),
    CreatedAt datetime default getdate()
);

-- Bảng người dùng
create table Users (
    UserID int primary key identity(1,1),
    FullName nvarchar(100) not null,
    Email nvarchar(100) unique not null,
    Password nvarchar(255) not null,
    Role nvarchar(20) default 'customer',
    CreatedAt datetime default getdate()
);

-- Bảng đơn hàng
create table Orders (
    OrderID int primary key identity(1,1),
    UserID int foreign key references Users(UserID),
    TotalAmount decimal(18,2) not null,
    Status nvarchar(50) default 'pending',
    CreatedAt datetime default getdate()
);

-- Bảng chi tiết đơn hàng
create table OrderDetails (
    DetailID int primary key identity(1,1),
    OrderID int foreign key references Orders(OrderID),
    ProductID int foreign key references Products(ProductID),
    Quantity int not null,
    Price decimal(18,2) not null
);

-- Bảng lịch sử (cho Trigger)
create table AuditLog (
    LogID int primary key identity(1,1),
    Action nvarchar(100),
    Description nvarchar(500),
    CreatedAt datetime default getdate()
);

go
create trigger UpdateStock
on OrderDetails after insert
as begin
    update Products
    set Stock = Stock - i.Quantity
    from Products p
    join inserted i on p.ProductID = i.ProductID

    insert into AuditLog(Action, Description)
    select 'UPDATE STOCK', 
           N'Trừ ' + cast(i.Quantity as nvarchar) + N' sản phẩm ID ' + cast(i.ProductID as nvarchar)
    from inserted i
end
go

create procedure GetProducts
    @CategoryID int = null
as begin
    select p.ProductID, p.Name, p.Price, p.Stock,
           p.ImageURL, c.Name as CategoryName
    from Products p
    join Categories c on p.CategoryID = c.CategoryID
    where (@CategoryID is null or p.CategoryID = @CategoryID)
    order by p.CreatedAt desc
end
go

CREATE PROCEDURE PlaceOrder
    @UserID INT,
    @ProductID INT,
    @Quantity INT
AS BEGIN
    BEGIN TRANSACTION
    BEGIN TRY
        DECLARE @Price DECIMAL(18,2)
        DECLARE @Stock INT
        DECLARE @OrderID INT

        -- Kiểm tra tồn kho
        SELECT @Price = Price, @Stock = Stock
        FROM Products WHERE ProductID = @ProductID

        IF @Stock < @Quantity
            THROW 50001, N'Sản phẩm không đủ hàng!', 1

        -- Tạo đơn hàng
        INSERT INTO Orders(UserID, TotalAmount, Status)
        VALUES(@UserID, @Price * @Quantity, 'pending')

        SET @OrderID = SCOPE_IDENTITY()

        -- Tạo chi tiết đơn hàng (Trigger tự trừ kho)
        INSERT INTO OrderDetails(OrderID, ProductID, Quantity, Price)
        VALUES(@OrderID, @ProductID, @Quantity, @Price)

        COMMIT TRANSACTION
        SELECT 'SUCCESS' AS Result, @OrderID AS OrderID

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION
        SELECT 'ERROR' AS Result, ERROR_MESSAGE() AS Message
    END CATCH
END
GO

-- Thêm danh mục
insert into Categories(Name) values (N'Điện thoại'), (N'Laptop'), (N'Phụ kiện');

-- Thêm sản phẩm
insert into Products(CategoryID, Name, Price, Stock)
values
(1, N'iPhone 100', 25000000, 10),
(1, N'Samsung Galaxy S24', 22000000, 15),
(2, N'MacBook Air M2', 32000000, 5),
(3, N'Tai nghe AirPods', 5000000, 20);