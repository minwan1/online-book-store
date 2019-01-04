INSERT INTO `member` (`id`, `created_at`, `email`, `first_name`, `last_name`, `password`, `updated_at`)
VALUES
	(1, '2018-12-12 12:12:12', 'test1@gmail.com', 'first', 'last', '{bcrypt}$2a$10$aIN3mx4oaAQCnolgxqGf0uwGIm/bJ08pSO4IxY5m1VNvJLWRmT.8a', '2018-12-12 12:12:12'),
	(2, '2018-12-12 12:12:12', 'test2@gmail.com', 'first', 'last', '{bcrypt}$2a$10$aIN3mx4oaAQCnolgxqGf0uwGIm/bJ08pSO4IxY5m1VNvJLWRmT.8a', '2018-12-12 12:12:12'),
	(3, '2018-12-12 12:12:12', 'test3@gmail.com', 'first', 'last', '{bcrypt}$2a$10$aIN3mx4oaAQCnolgxqGf0uwGIm/bJ08pSO4IxY5m1VNvJLWRmT.8a', '2018-12-12 12:12:12');


INSERT INTO `book` (`book_id`, `created_at`, `name`, `price`, `updated_at`)
VALUES
	('b22b2ffa-115c-4531-b9cc-5cdfdeac2671', '2018-04-14 12:12:12', 'test1', 3000, '2018-04-14 12:12:12'),
	('b22b2ffa-115c-4531-b9cc-5cdfdeac2672', '2018-04-14 12:12:13', 'test2', 3000, '2018-04-14 12:12:12'),
	('b22b2ffa-115c-4531-b9cc-5cdfdeac2673', '2018-04-14 12:12:15', 'test3', 3000, '2018-04-14 12:12:12');
