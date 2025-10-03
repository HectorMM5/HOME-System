SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

CREATE TABLE `task` (
  `taskId` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `completed` tinyint(1) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `createdDate` datetime DEFAULT NULL,
  `dueDate` datetime DEFAULT NULL,
  `completedDate` datetime DEFAULT NULL,
  `priority` varchar(255) DEFAULT NULL,
  `taskWeight` int DEFAULT NULL,
  `taskSize` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `task_assignees` (
  `taskId` varchar(36) NOT NULL,
  `userId` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `task_changelog` (
  `id` varchar(36) NOT NULL,
  `taskId` varchar(36) NOT NULL,
  `description` text NOT NULL,
  `changedBy` varchar(36) NOT NULL,
  `changedAt` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `userId` varchar(36) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `passwordHash` blob NOT NULL,
  `salt` blob NOT NULL,
  `workloadCapacity` int NOT NULL,
  `sickness` tinyint NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


ALTER TABLE `task`
  ADD PRIMARY KEY (`taskId`) USING BTREE;

ALTER TABLE `task_assignees`
  ADD PRIMARY KEY (`taskId`,`userId`),
  ADD KEY `fk_userId` (`userId`),
  ADD KEY `fk_taskId` (`taskId`) USING BTREE;

ALTER TABLE `task_changelog`
  ADD PRIMARY KEY (`id`),
  ADD KEY `taskId` (`taskId`),
  ADD KEY `changedBy` (`changedBy`);

ALTER TABLE `user`
  ADD PRIMARY KEY (`userId`);

ALTER TABLE `task_assignees`
  ADD CONSTRAINT `fk_taskId` FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_userId` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `task_changelog`
  ADD CONSTRAINT `task_changelog_ibfk_1` FOREIGN KEY (`taskId`) REFERENCES `task` (`taskId`) ON DELETE CASCADE,
  ADD CONSTRAINT `task_changelog_ibfk_2` FOREIGN KEY (`changedBy`) REFERENCES `user` (`userId`) ON DELETE CASCADE;
COMMIT;
