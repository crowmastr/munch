-- phpMyAdmin SQL Dump
-- version 4.0.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 07, 2014 at 12:57 AM
-- Server version: 5.1.69
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `recipes`
--

-- --------------------------------------------------------

--
-- Table structure for table `ingredients`
--

CREATE TABLE IF NOT EXISTS `ingredients` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`,`name`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4451 ;

-- --------------------------------------------------------

--
-- Table structure for table `recipes`
--

CREATE TABLE IF NOT EXISTS `recipes` (
  `id` int(11) NOT NULL,
  `name` tinytext NOT NULL,
  `yield` int(11) NOT NULL,
  `instructions` text NOT NULL,
  `cost_per_recipe` float NOT NULL,
  `cost_per_serving` float NOT NULL,
  `source` tinytext NOT NULL,
  `notes` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `recipe_ingredients`
--

CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
  `recipe` int(11) NOT NULL,
  `ingredient` int(11) NOT NULL,
  `amt` tinytext NOT NULL,
  `info` tinytext NOT NULL,
  PRIMARY KEY (`recipe`,`ingredient`),
  KEY `recipe` (`recipe`),
  KEY `ingredient` (`ingredient`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `unique_id` varchar(23) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `encrypted_password` varchar(80) NOT NULL,
  `salt` varchar(10) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique_id` (`unique_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=19 ;

-- --------------------------------------------------------

--
-- Table structure for table `user_auth_tokens`
--

CREATE TABLE IF NOT EXISTS `user_auth_tokens` (
  `uid` varchar(23) NOT NULL,
  `token` varchar(40) NOT NULL,
  `created` int(11) NOT NULL,
  PRIMARY KEY (`token`),
  KEY `created` (`created`),
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_ingredient_list`
--

CREATE TABLE IF NOT EXISTS `user_ingredient_list` (
  `type` enum('shopping','inventory') NOT NULL,
  `user` varchar(23) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ingredient` int(11) NOT NULL,
  `created` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user` (`user`,`type`,`ingredient`),
  UNIQUE KEY `type` (`type`,`user`,`id`),
  KEY `ingredient` (`ingredient`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `recipe_ingredients`
--
ALTER TABLE `recipe_ingredients`
  ADD CONSTRAINT `recipe_ingredients_ibfk_2` FOREIGN KEY (`ingredient`) REFERENCES `ingredients` (`id`),
  ADD CONSTRAINT `recipe_ingredients_ibfk_1` FOREIGN KEY (`recipe`) REFERENCES `recipes` (`id`);

--
-- Constraints for table `user_auth_tokens`
--
ALTER TABLE `user_auth_tokens`
  ADD CONSTRAINT `user_auth_tokens_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `users` (`unique_id`);

--
-- Constraints for table `user_ingredient_list`
--
ALTER TABLE `user_ingredient_list`
  ADD CONSTRAINT `user_ingredient_list_ibfk_2` FOREIGN KEY (`ingredient`) REFERENCES `ingredients` (`id`),
  ADD CONSTRAINT `user_ingredient_list_ibfk_1` FOREIGN KEY (`user`) REFERENCES `users` (`unique_id`);
