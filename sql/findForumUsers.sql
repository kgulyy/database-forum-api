SELECT * FROM
(
	(
		SELECT DISTINCT u.nickname, u.fullname, u.email, u.about
		FROM users u, threads t
		WHERE u.nickname = t.author
		AND t.forum = :forum::citext
	)
	UNION
	(
		SELECT DISTINCT u.nickname, u.fullname, u.email, u.about
		FROM users u, posts p
		WHERE u.nickname = p.author
		AND p.forum = :forum::citext
	)
) AS u
-- WHERE nickname > :since::citext COLLATE UCS_BASIC
ORDER BY LOWER(u.nickname) COLLATE UCS_BASIC
LIMIT 100;