CREATE OR REPLACE FUNCTION getFollowers (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $uname$
DECLARE uname VARCHAR(200);
BEGIN
  FOR uname IN select username FROM users_follows
   INNER JOIN users on users_follows.follower_id = users.id
   WHERE followed_id = xx
   LOOP
    RETURN NEXT uname;
  END LOOP;
END;
$uname$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION getFollowing (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $uname$
DECLARE uname VARCHAR(200);
BEGIN
  FOR uname IN select username FROM users_follows
   INNER JOIN users on users_follows.followed_id = users.id
   WHERE follower_id = xx
   LOOP
    RETURN NEXT uname;
  END LOOP;
END;
$uname$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getMyPhotoUrls (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $urls$
DECLARE urls VARCHAR(200);
BEGIN
  FOR urls IN Select url FROM posts p 
		  	inner join posts_media pm on p.id=pm.post_id
			inner join media m on m.id=pm.media_id
			where p.user_id=xx
   LOOP
    RETURN NEXT urls;
  END LOOP;
END;
$urls$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getMyTaggedPhotoUrls (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $urls$
DECLARE urls VARCHAR(200);
BEGIN
  FOR urls IN Select url FROM posts_tags pt 
		  	inner join posts_media pm on pt.post_id=pm.post_id
			inner join media m on m.id=pm.media_id
			where pt.user_id=xx
   LOOP
    RETURN NEXT urls;
  END LOOP;
END;
$urls$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION getMySavedPhotoUrls (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $urls$
DECLARE urls VARCHAR(200);
BEGIN
  FOR urls IN Select url FROM posts_bookmarks pb 
		  	inner join posts_media pm on pb.post_id=pm.post_id
			inner join media m on m.id=pm.media_id
			where pb.user_id=xx
   LOOP
    RETURN NEXT urls;
  END LOOP;
END;
$urls$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION getBlockedUsersUsername (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $uname$
DECLARE uname VARCHAR(200);
BEGIN
  FOR uname IN select username FROM users_blocks ub
			inner join users u on u.id=ub.blocked_id
		where ub.blocker_id=xx
   LOOP
    RETURN NEXT uname;
  END LOOP;
END;
$uname$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION getMyLikedPhotoUrls (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $urls$
DECLARE urls VARCHAR(200);
BEGIN
  FOR urls IN Select url FROM posts_likes pl 
		  	inner join posts_media pm on pl.post_id=pm.post_id
			inner join media m on m.id=pm.media_id
			where pl.user_id=xx
   LOOP
    RETURN NEXT urls;
  END LOOP;
END;
$urls$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getMyHomePhotoUrls (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $urls$
DECLARE urls VARCHAR(200);
BEGIN
  FOR urls IN Select url from users_follows uf
	INNER JOIN users u ON uf.followed_id = u.id 
	INNER JOIN posts p ON p.user_id = u.id
	INNER JOIN posts_media pm ON pm.post_id = p.id
	INNER JOIN media m ON m.id = pm.media_id
	where follower_id =xx
   LOOP
    RETURN NEXT urls;
  END LOOP;
END;
$urls$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION getLikesUsernamesOnPost (xx VARCHAR(200))
RETURNS SETOF VARCHAR(200) AS $uname$
DECLARE uname VARCHAR(200);
BEGIN
  FOR uname IN select u.username from posts_likes pl
				INNER JOIN users u ON u.id = pl.user_id 
				where pl.post_id =xx
   LOOP
    RETURN NEXT uname;
  END LOOP;
END;
$uname$ LANGUAGE plpgsql;
