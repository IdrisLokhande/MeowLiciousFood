const express = require('express');
const db = require('./db'); // use pool just made
const bcrypt = require('bcrypt'); // for hashing
const saltRounds = 10;
const MAX_RETRIES = 3;
const MAX_DELAY_MS = 500;

const app = express();
app.use(express.json());

async function withRetry(fn, label = 'DB operation') {
	let attempt = 0;
	while(attempt < MAX_RETRIES){
		try{
			return await fn();
		}catch(err){
			attempt++;
			console.warn(`${label} failed (attempt ${attempt}):`, err.message);
			if(attempt >= MAX_RETRIES) throw err;
			await new Promise(res => setTimeout(res, MAX_DELAY_MS));
		}
	} 
}

app.listen(3000, '0.0.0.0', () => {
  console.log('Server running on port 3000');
});

app.get('/login', (req, res) => {
  res.send('Login endpoint is POST-only');
});


app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  if (!username || !password) {
    return res.json({ success: false, message: 'Mrrow! Missing username or password!', uid: null });
  }

  const query = 'SELECT * FROM users WHERE username = ?';

  try {
    const results = await withRetry(() => db.query(query, [username]), 'Login query');

    if (results[0].length === 0) {
      return res.json({ success: false, message: 'Mrrow! Invalid username!', uid: null });
    }

    const user = results[0][0];
    const match = await bcrypt.compare(password, user.password);

    if (match) {
      return res.json({ success: true, message: 'Meow! Login successful!', uid: user.uid });
    } else {
      return res.json({ success: false, message: 'Mrrow! Invalid password!', uid: null });
    }
  } catch (err) {
    console.error('Login error:', err);
    return res.json({ success: false, message: 'Server Error!', uid: null });
  }
});

app.post('/register', async (req, res) => {
  const { email, username, firstName, lastName, phone, address, password } = req.body;

  if (!email || !firstName || !lastName || !phone || !address || !password) {
    return res.json({ success: false, message: 'Mrrow! Missing fields!' });
  }

  try {
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const hashedPhone = await bcrypt.hash(phone, saltRounds);

    // Check if email already exists
    const [existing] = await withRetry(() => db.query('SELECT * FROM users WHERE email = ? OR username = ?', [email, username]), 'Email or Username check');

    if (existing.length > 0) {
      return res.json({ success: false, message: 'Mrrow! Registration found with same Email or Username!' });
    }

    // Insert new user
    const [result] = await withRetry(() =>
      db.query(
        'INSERT INTO users (email, username, fname, lname, phone, address, password) VALUES (?,?,?,?,?,?,?)',
        [email, username, firstName, lastName, hashedPhone, address, hashedPassword]
      ), 'User insert');

    if (result.affectedRows > 0) {
      return res.json({ success: true, message: 'Meow! Registration successful!' });
    } else {
      return res.json({ success: false, message: 'Internal Server Error!' });
    }

  } catch (err) {
    console.error('Registration error:', err);
    return res.json({ success: false, message: 'Server Error!' });
  }
});		

app.post('/orders/place', async (req, res) => {
  const { oid, uid, orderdate, total, items } = req.body;

  if (!oid || !uid || !orderdate || !total || !Array.isArray(items) || items.length === 0) {
    return res.json({ success: false, message: 'Mrrow! Missing order details!' });
  }

  try {
    // Insert into orders table
    await withRetry(() =>
      db.query('INSERT INTO orders (oid, uid, orderdate, total) VALUES (?,?,?,?)', [oid, uid, orderdate, total]),
      'Insert order'
    );

    // Insert each item into orderitems table
    for (const item of items) {
      const { fid, qty, subtotal } = item;
      await withRetry(() =>
        db.query('INSERT INTO orderitems (oid, fid, qty, subtotal) VALUES (?,?,?,?)', [oid, fid, qty, subtotal]),
        `Insert item ${fid}`
      );
    }

    return res.json({ success: true, message: 'Meow! Order placed successfully!' });

  } catch (err) {
    console.error('Order placement error:', err);
    return res.json({ success: false, message: 'Server Error!' });
  }
});

app.post('/favorites/post', async (req, res) => {
  const { uid, fid } = req.body;

  if (!uid || !fid) {
    return res.json({ success: false, message: 'Mrrow! Missing favorite info!' });
  }

  try {
    await withRetry(() =>
      db.query('INSERT INTO favorites (uid, fid) VALUES (?, ?)', [uid, fid]),
      'Insert favorite'
    );

    return res.json({ success: true, message: 'Meow! Added to Favorites!' });

  } catch (err) {
    console.error('Favorites error:', err);
    return res.json({ success: false, message: 'Server Error!' });
  }
});

app.post('/favorites/delete', async (req, res) => {
  const { uid, fid } = req.body;

  if (!uid || !fid) {
    return res.json({ success: false, message: 'Mrrow! Missing favorite info!' });
  }

  try {
    const [result] = await withRetry(() =>
      db.query('DELETE FROM favorites WHERE uid = ? AND fid = ?', [uid, fid]),
      'Delete favorite'
    );

    if (result.affectedRows > 0) {
      return res.json({ success: true, message: 'Meow! Removed from Favorites!' });
    } else {
      return res.json({ success: false, message: 'Mrrow! Favorite not found!' });
    }

  } catch (err) {
    console.error('Favorites error:', err);
    return res.json({ success: false, message: 'Server Error!' });
  }
});

app.get('/favorites/get', async (req, res) => {
  const uid = req.query.uid;

  if (!uid) {
    return res.json({ error: 'Mrrow! Missing user ID!' });
  }

  const query = `
    SELECT f.fid, fi.foodname, fi.description, fi.price, fi.imgs,
           r.restaurantname AS rname
    FROM favorites AS f
    JOIN fooditems AS fi ON f.fid = fi.fid
    JOIN restaurants AS r ON fi.rid = r.rid
    WHERE f.uid = ?
  `;

  try {
    const [results] = await withRetry(() => db.query(query, [uid]), 'Fetch favorites');

    const formattedResults = results.map(row => {
      const base64Image = row.imgs ? row.imgs.toString('base64') : null;
      return {
        fid: row.fid,
        foodname: row.foodname,
        description: row.description,
        price: row.price,
        img: base64Image,
        rname: row.rname
      };
    });

    console.log(formattedResults);
    return res.json(formattedResults);

  } catch (err) {
    console.error('Error fetching favorites:', err);
    return res.json({ error: 'Internal Server Error' });
  }
});

app.get('/fooditems/all', async (req, res) => {
  const query = `
    SELECT fid, foodname, description, price, imgs,
           restaurants.restaurantname AS rname
    FROM fooditems
    JOIN restaurants ON fooditems.rid = restaurants.rid
  `;

  try {
    const [results] = await withRetry(() => db.query(query), 'Fetch all food items');

    const formattedResults = results.map(row => {
      const base64Image = row.imgs ? row.imgs.toString('base64') : null;
      return {
        fid: row.fid,
        foodname: row.foodname,
        description: row.description,
        price: row.price,
        img: base64Image,
        rname: row.rname
      };
    });

    console.log(formattedResults);
    return res.json(formattedResults);

  } catch (err) {
    console.error('Error fetching food items:', err);
    return res.json({ error: 'Internal Server Error' });
  }
});