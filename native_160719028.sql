-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 12, 2023 at 08:30 AM
-- Server version: 10.3.37-MariaDB-0ubuntu0.20.04.1
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `native_160719028`
--

-- --------------------------------------------------------

--
-- Table structure for table `memes`
--

CREATE TABLE `memes` (
  `id_meme` int(11) NOT NULL,
  `url_meme` longtext DEFAULT NULL,
  `teks_atas` varchar(45) DEFAULT NULL,
  `teks_bawah` varchar(45) DEFAULT NULL,
  `jumlah_like` int(11) DEFAULT 0,
  `user_id` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `memes`
--

INSERT INTO `memes` (`id_meme`, `url_meme`, `teks_atas`, `teks_bawah`, `jumlah_like`, `user_id`, `created_date`) VALUES
(4, 'https://cdn.idntimes.com/content-images/duniaku/post/20220124/maxresdefault-7183af406be0a606cb1dbf99d47dc8e8_600x400.jpg', 'Pancen Oke', 'Emang Oke', 3, 17, NULL),
(5, 'https://images.solopos.com/2016/04/xSgXFnXQ-meme-3.jpg', 'Tak tendang ndas mu', 'Aduhhhhh', 0, 17, NULL),
(6, 'https://i.imgflip.com/4/46e43q.jpg', 'when', 'bottom text', 1, 17, NULL),
(12, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSfHV5qIuMnFiurKmqo-lun1WsI0HzirjbEZg&usqp=CAU', 'asas', 'asasas', 5, 16, NULL),
(13, 'https://static01.nyt.com/images/2021/04/30/multimedia/30xp-meme/29xp-meme-mobileMasterAt3x-v3.jpg?quality=75&auto=webp&disable=upscale&width=1200', 'bruh', 'e', 6, 16, NULL),
(16, 'https://image-service.usw2.wp-prod-us.cultureamp-cdn.com/OqjzT0ErD2k23PVq3gNsGKXkiJY=/1440x0/cultureampcom/production/1e4/ddf/e68/1e4ddfe687e3363fe3f2e78b/blog-90-funny-jokes-to-share-with-coworkers.png', 'ok', 'ok', 1, 16, NULL),
(17, 'https://static.thehoneycombers.com/wp-content/uploads/sites/4/2020/03/Best-funny-Coronavirus-memes-2020-Honeycombers-Bali-30.jpeg', 'Hahahahaha', 'hihihihi', 1, 24, '2023-01-11 22:03:46');

-- --------------------------------------------------------

--
-- Table structure for table `memes_like`
--

CREATE TABLE `memes_like` (
  `like_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `id_meme` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `memes_like`
--

INSERT INTO `memes_like` (`like_id`, `user_id`, `id_meme`) VALUES
(1, 16, 16),
(2, 16, 12),
(3, 16, 13),
(4, 24, 16),
(5, 24, 13),
(6, 24, 12),
(7, 24, 6),
(8, 24, 4),
(9, 24, 17),
(10, 25, 12);

-- --------------------------------------------------------

--
-- Table structure for table `meme_comments`
--

CREATE TABLE `meme_comments` (
  `comment_id` int(11) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `comment_date` date DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `id_meme` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `meme_comments`
--

INSERT INTO `meme_comments` (`comment_id`, `comment`, `comment_date`, `user_id`, `id_meme`) VALUES
(1, 'test', '2023-01-11', 16, 16),
(2, 'okkok', '2023-01-11', 16, 16),
(3, 'apakah', '2023-01-11', 16, 16),
(4, '1 nih', '2023-01-11', 16, 16),
(5, 'malam', '2023-01-11', 16, 16),
(6, 'mantap', '2023-01-11', 24, 17);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(1000) DEFAULT NULL,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `user_pass` varchar(45) DEFAULT NULL,
  `regis_date` date DEFAULT NULL,
  `url_image` varchar(200) DEFAULT NULL,
  `privacy_setting` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `first_name`, `last_name`, `user_pass`, `regis_date`, `url_image`, `privacy_setting`) VALUES
(16, 'regiii', 'regi', 'oku', '123', NULL, NULL, 1),
(17, 't', NULL, NULL, 't', '2023-01-07', NULL, 0),
(18, 'wendy', NULL, NULL, '123', '2023-01-09', NULL, 0),
(24, 'test123', 'khesa', 'alvandi', '123', '2023-01-11', NULL, 1),
(25, 'khesa', 'vandik', 'alvandi', '123', '2023-01-11', NULL, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `memes`
--
ALTER TABLE `memes`
  ADD PRIMARY KEY (`id_meme`),
  ADD KEY `fk_memes_users1_idx` (`user_id`);

--
-- Indexes for table `memes_like`
--
ALTER TABLE `memes_like`
  ADD PRIMARY KEY (`like_id`),
  ADD KEY `fk_user` (`user_id`),
  ADD KEY `fk_meme` (`id_meme`);

--
-- Indexes for table `meme_comments`
--
ALTER TABLE `meme_comments`
  ADD PRIMARY KEY (`comment_id`),
  ADD KEY `fk_meme_comments_users_idx` (`user_id`),
  ADD KEY `fk_meme_comments_memes1_idx` (`id_meme`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `memes`
--
ALTER TABLE `memes`
  MODIFY `id_meme` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `memes_like`
--
ALTER TABLE `memes_like`
  MODIFY `like_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `meme_comments`
--
ALTER TABLE `meme_comments`
  MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `memes`
--
ALTER TABLE `memes`
  ADD CONSTRAINT `fk_memes_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `memes_like`
--
ALTER TABLE `memes_like`
  ADD CONSTRAINT `fk_meme` FOREIGN KEY (`id_meme`) REFERENCES `memes` (`id_meme`),
  ADD CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `meme_comments`
--
ALTER TABLE `meme_comments`
  ADD CONSTRAINT `fk_meme_comments_memes1` FOREIGN KEY (`id_meme`) REFERENCES `memes` (`id_meme`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_meme_comments_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
