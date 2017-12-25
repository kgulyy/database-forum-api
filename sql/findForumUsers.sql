SELECT * FROM
(
	(
		SELECT DISTINCT u.nickname, u.fullname, u.email, u.about
		FROM users u, threads t
		WHERE u.nickname = t.author
		AND LOWER(t.forum) = LOWER('I_2X23oW414r8')
	)
	UNION
	(
		SELECT DISTINCT u.nickname, u.fullname, u.email, u.about
		FROM users u, posts p
		WHERE u.nickname = p.author
		AND LOWER(p.forum) = LOWER('I_2X23oW414r8')) 
	) AS u
-- WHERE LOWER(nickname) > LOWER(:since) COLLATE UCS_BASIC
ORDER BY LOWER(u.nickname) COLLATE UCS_BASIC
LIMIT 100;