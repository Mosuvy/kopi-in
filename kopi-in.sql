-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jul 16, 2025 at 12:57 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `kopi-in`
--
CREATE DATABASE IF NOT EXISTS `kopi-in` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `kopi-in`;

-- --------------------------------------------------------

--
-- Table structure for table `Categories`
--

CREATE TABLE `Categories` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `code` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Categories`
--

INSERT INTO `Categories` (`id`, `name`, `code`) VALUES
(1, 'Black Coffee', 'BC'),
(2, 'White Coffee', 'WC'),
(3, 'Non Coffee', 'NC'),
(4, 'Cake', 'CK'),
(5, 'Snacks', 'SN'),
(6, 'Signature Menu', 'SG');

-- --------------------------------------------------------

--
-- Table structure for table `Customer_details`
--

CREATE TABLE `Customer_details` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `address` text DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Feedbacks`
--

CREATE TABLE `Feedbacks` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('read','unread') NOT NULL DEFAULT 'unread'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `OrderItems`
--

CREATE TABLE `OrderItems` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` varchar(16) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Orders`
--

CREATE TABLE `Orders` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT 1,
  `created_by` int(11) NOT NULL,
  `status` enum('accepted','rejected','processing') NOT NULL DEFAULT 'processing',
  `order_type` enum('online','pos') NOT NULL DEFAULT 'pos',
  `total_price` decimal(10,2) NOT NULL,
  `promo_id` varchar(16) DEFAULT NULL,
  `final_price` decimal(10,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Products`
--

CREATE TABLE `Products` (
  `id` varchar(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `promo_id` varchar(16) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Products`
--

INSERT INTO `Products` (`id`, `name`, `description`, `price`, `promo_id`, `image_url`, `category_id`, `is_active`, `created_at`, `updated_at`) VALUES
('BC-20250716-564', 'Espresso', NULL, 16000.00, NULL, NULL, 1, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('BC-20250716-623', 'Long Black', 'Single shot espresso yang diberi sedikit air panas, cocok untuk pecinta strong coffee.', 21000.00, NULL, NULL, 1, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('BC-20250716-732', 'Americano', 'Espresso dengan tambahan air panas, rasa kopi tetap kuat tapi ringan di body.', 19000.00, NULL, NULL, 1, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('CK-20250716-250', 'Red Velvet Cake', 'Cake lembut berwarna merah dengan cream cheese frosting.', 28000.00, NULL, NULL, 4, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('CK-20250716-542', 'Banana Bread', 'Roti pisang homemade Kopi.in, manis alami.', 22000.00, NULL, NULL, 4, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('CK-20250716-858', 'Choco Lava', NULL, 30000.00, NULL, NULL, 4, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('NC-20250716-070', 'Matcha Latte', 'Teh hijau Jepang creamy, tanpa kopi.', 27000.00, NULL, NULL, 3, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('NC-20250716-131', 'Choco.in', 'Cokelat manis dan hangat, cocok untuk semua umur.', 25000.00, NULL, NULL, 3, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('NC-20250716-468', 'Lemon Tea', NULL, 18000.00, NULL, NULL, 3, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SG-20250716-215', 'Avocado Coffee', NULL, 28000.00, NULL, NULL, 6, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SG-20250716-288', 'Taro Latte', 'Minuman taro ungu manis dengan foam lembut.', 26000.00, NULL, NULL, 6, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SG-20250716-930', 'Es Kopi.in Signature', 'Kopi susu creamy dengan sentuhan rahasia Kopi.in.', 25000.00, NULL, NULL, 6, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SN-20250716-042', 'French Fries', NULL, 18000.00, NULL, NULL, 5, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SN-20250716-133', 'Tahu Crispy', 'Tahu goreng renyah dengan bumbu khas Kopi.in.', 15000.00, NULL, NULL, 5, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('SN-20250716-811', 'Waffle Mini', 'Waffle kecil isi cokelat, cocok buat ngemil.', 20000.00, NULL, NULL, 5, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('WC-20250716-246', 'Kopi Susu', NULL, 22000.00, NULL, NULL, 2, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('WC-20250716-423', 'Caffe Latte', 'Espresso dengan susu steam, creamy dan lembut.', 23000.00, NULL, NULL, 2, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47'),
('WC-20250716-961', 'Flat White', 'Mirip latte, tapi dengan rasio lebih banyak kopi daripada susu.', 24000.00, NULL, NULL, 2, 1, '2025-07-16 10:30:47', '2025-07-16 10:30:47');

--
-- Triggers `Products`
--
DELIMITER $$
CREATE TRIGGER `before_insert_product` BEFORE INSERT ON `Products` FOR EACH ROW BEGIN
    DECLARE cat_code VARCHAR(5);
    DECLARE today DATE;
    DECLARE rand_suffix VARCHAR(3);

    SET today = CURDATE();

    -- Ambil kode kategori dari tabel Categories
    SELECT code INTO cat_code FROM Categories WHERE id = NEW.category_id;

	-- Generate angka random 000–999
    SET rand_suffix = LPAD(FLOOR(RAND() * 1000), 3, '0');

    -- Set ID produk dengan format: BC-20250731-001
    SET NEW.id = CONCAT(
        cat_code, '-',
        DATE_FORMAT(today, '%Y%m%d'), '-',
        rand_suffix
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `Promo`
--

CREATE TABLE `Promo` (
  `id` varchar(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  `code` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `discount_value` decimal(10,2) DEFAULT NULL,
  `min_purchase` decimal(10,2) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `update_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Promo`
--

INSERT INTO `Promo` (`id`, `name`, `code`, `description`, `discount_value`, `min_purchase`, `start_date`, `end_date`, `created_at`, `update_at`) VALUES
('PR-04468', 'QRIS Cashback', 'QRISCASH', 'Diskon 20% untuk pembayaran QRIS.', 0.20, 30000.00, '2025-07-16', '2025-07-16', '2025-07-16 10:20:22', '2025-07-16 10:20:22'),
('PR-17430', 'Combo Hemat', 'COMBOHEMAT', 'Diskon 15% untuk minuman & snack combo.', 0.15, NULL, '2025-07-16', '2025-08-16', '2025-07-16 10:20:22', '2025-07-16 10:20:22'),
('PR-40172', 'Diskon Kopi Pagi', 'PAGIKOPI', 'Diskon 10% untuk pembelian sebelum jam 11.', 0.10, 20000.00, '2025-07-16', '2025-08-16', '2025-07-16 10:20:22', '2025-07-16 10:20:22'),
('PR-66632', 'Jumat Ngopi', 'JUMATNGOPI', 'Setiap Jumat, diskon 12% semua menu kopi.', 0.12, 25000.00, '2025-07-18', NULL, '2025-07-16 10:20:22', '2025-07-16 10:20:22'),
('PR-80872', 'Promo Pelajar', 'PELAJAR10', 'Diskon 10% untuk pelajar dengan kartu pelajar.', 0.10, NULL, NULL, NULL, '2025-07-16 10:20:22', '2025-07-16 10:20:22');

--
-- Triggers `Promo`
--
DELIMITER $$
CREATE TRIGGER `before_insert_promo` BEFORE INSERT ON `Promo` FOR EACH ROW BEGIN
    IF NEW.id IS NULL OR NEW.id = '' THEN
        SET NEW.id = CONCAT('PR-', LPAD(FLOOR(RAND() * 99999), 5, '0'));
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `Transactions`
--

CREATE TABLE `Transactions` (
  `id` varchar(16) NOT NULL,
  `order_id` int(11) NOT NULL,
  `payment_method` enum('cash','emoney') NOT NULL,
  `paid_amount` decimal(10,2) NOT NULL,
  `paid_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `change_returned` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `Transactions`
--
DELIMITER $$
CREATE TRIGGER `before_insert_transaction` BEFORE INSERT ON `Transactions` FOR EACH ROW BEGIN
    IF NEW.id IS NULL OR NEW.id = '' THEN
        SET NEW.id = CONCAT(
            'NO. ',
            UPPER(SUBSTRING(UUID(), 1, 6)),
            '-',
            FLOOR(RAND() * 10),
            FLOOR(RAND() * 10)
        );
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `id` int(11) NOT NULL,
  `username` varchar(16) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','cashier','customer') NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`id`, `username`, `email`, `password`, `role`, `is_active`, `created_at`) VALUES
(1, 'guest', 'guest@coffeeshop.com', 'dummy', 'customer', 1, '2025-07-16 09:42:44');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Categories`
--
ALTER TABLE `Categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `Customer_details`
--
ALTER TABLE `Customer_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `customer_details_ibfk_1` (`user_id`);

--
-- Indexes for table `Feedbacks`
--
ALTER TABLE `Feedbacks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `feedbacks_ibfk_1` (`user_id`);

--
-- Indexes for table `OrderItems`
--
ALTER TABLE `OrderItems`
  ADD PRIMARY KEY (`id`),
  ADD KEY `orderitems_ibfk_1` (`order_id`),
  ADD KEY `orderitems_ibfk_2` (`product_id`);

--
-- Indexes for table `Orders`
--
ALTER TABLE `Orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `orders_ibfk_1` (`user_id`),
  ADD KEY `orders_ibfk_2` (`promo_id`),
  ADD KEY `orders_ibfk_3` (`created_by`);

--
-- Indexes for table `Products`
--
ALTER TABLE `Products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `products_ibfk_1` (`promo_id`),
  ADD KEY `products_ibfk_2` (`category_id`);

--
-- Indexes for table `Promo`
--
ALTER TABLE `Promo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`);

--
-- Indexes for table `Transactions`
--
ALTER TABLE `Transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transactions_ibfk_1` (`order_id`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`,`email`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username_2` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Categories`
--
ALTER TABLE `Categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `Customer_details`
--
ALTER TABLE `Customer_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Feedbacks`
--
ALTER TABLE `Feedbacks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `OrderItems`
--
ALTER TABLE `OrderItems`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Orders`
--
ALTER TABLE `Orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Customer_details`
--
ALTER TABLE `Customer_details`
  ADD CONSTRAINT `customer_details_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Feedbacks`
--
ALTER TABLE `Feedbacks`
  ADD CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `OrderItems`
--
ALTER TABLE `OrderItems`
  ADD CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Orders`
--
ALTER TABLE `Orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`promo_id`) REFERENCES `Promo` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `Users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Products`
--
ALTER TABLE `Products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`promo_id`) REFERENCES `Promo` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `Categories` (`id`) ON DELETE SET NULL ON UPDATE SET NULL;

--
-- Constraints for table `Transactions`
--
ALTER TABLE `Transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
