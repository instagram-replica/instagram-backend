const fs = require('fs');
const faker = require('faker');

const getRandomInt = (min, max) =>
  Math.floor(Math.random() * (max - min + 1)) + min;

const mediaArray = [];
const mediaPostArray = [];

const users = new Array(100).fill({}).map(_ => ({
  id: faker.random.uuid(),
  username: faker.internet.userName(),
  email: faker.internet.email(),
  password_hash: faker.internet.password(),
  is_private: 'FALSE',
  full_name: `${faker.name.firstName()} ${faker.name.lastName()}`,
  created_at: new Date().toISOString(),
}));

const posts = new Array(100).fill({}).map(_ => {
  const media = {
    id: faker.random.uuid(),
    class: ['photo', 'video'][getRandomInt(0, 1)],
    url: faker.image.animals(),
    created_at: new Date().toISOString(),
  };

  const post = {
    id: faker.random.uuid(),
    user_id: users[getRandomInt(0, users.length - 1)].id,
    caption: faker.random.words(),
    created_at: new Date().toISOString(),
  };

  const mediaPost = {
    id: faker.random.uuid(),
    post_id: post.id,
    media_id: media.id,
  };

  mediaArray.push(media);
  mediaPostArray.push(mediaPost);

  return post;
});

const comments = new Array(100).fill({}).map(_ => ({
  id: faker.random.uuid(),
  text: faker.random.words(),
  depth: 0,
  user_id: users[getRandomInt(0, users.length - 1)].id,
  post_id: posts[getRandomInt(0, posts.length - 1)].id,
  comments: [],
  created_at: new Date().toISOString(),
  updated_at: new Date().toISOString(),
}));

const threads = new Array(100).fill({}).map(_ => {
  const creatorId = users[getRandomInt(0, users.length - 1)].id;
  const receiverId = users[getRandomInt(0, users.length - 1)].id;

  return {
    id: faker.random.uuid(),
    creator_id: creatorId,
    users_ids: [creatorId, receiverId],
    name: faker.random.word(),
    created_at: new Date().toISOString(),
    messages: new Array(200).fill({}).map((_, i) => ({
      id: faker.random.uuid(),
      text: faker.random.words(),
      user_id:
        i === 1 ? creatorId : [creatorId, receiverId][getRandomInt(0, 1)].id,
      created_at: new Date().toISOString(),
      likers_ids: [],
      media_id: null,
    })),
  };
});

const stories = new Array(100).fill({}).map(_ => {
  const media = {
    id: faker.random.uuid(),
    class: ['photo', 'video'][getRandomInt(0, 1)],
    url: faker.image.animals(),
    created_at: new Date().toISOString(),
  };

  mediaArray.push(media);

  return {
    id: faker.random.uuid(),
    user_id: users[getRandomInt(0, users.length - 1)].id,
    is_featured: false,
    media_id: media.id,
    seen_by_users_ids: [],
    created_at: new Date().toISOString(),
  };
});

// ################################## Generate Users File ##################################

const usersXML = users
  .map(
    ({
      id,
      username,
      email,
      password_hash,
      is_private,
      full_name,
      created_at,
    }) =>
      `${id},${username},${email},${password_hash},${is_private},${full_name},${created_at}`,
  )
  .join('\n');
fs.writeFileSync('./users.csv', usersXML);

// ################################## Generate Posts File ##################################

const postsXML = posts
  .map(
    ({ id, user_id, caption, created_at }) =>
      `${id},${user_id},${caption},${created_at}`,
  )
  .join('\n');
fs.writeFileSync('./posts.csv', postsXML);

// ################################## Generate Media File ##################################

const mediaXML = mediaArray
  .map(
    ({ id, class: mediaClass, url, created_at }) =>
      `${id},${mediaClass},${url},${created_at}`,
  )
  .join('\n');
fs.writeFileSync('./media.csv', mediaXML);

// ################################## Generate Media Posts File ##################################

const mediaPostXML = mediaPostArray
  .map(({ id, post_id, media_id }) => `${id},${post_id},${media_id}`)
  .join('\n');
fs.writeFileSync('./mediaPost.csv', mediaPostXML);

// ################################## Generate Threads File ##################################

fs.writeFileSync('./threads.json', JSON.stringify(threads));

// ################################## Generate Threads File ##################################

fs.writeFileSync('./stories.json', JSON.stringify(stories));
