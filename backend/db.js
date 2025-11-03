const mysql = require('mysql2');

const mode = 'dev';
if(mode !== 'production'){
	dotenv = require('dotenv');
	dotenv.config();
}

console.log('DB_USER', process.env.DB_USER);

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 5,
  queueLimit: 0
}).promise();

module.exports = pool;