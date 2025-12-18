// server.js
const express = require('express');
const app = express();

app.use(express.static('public')); // отдаём папку с играми

app.listen(3000, () => {
  console.log('Сервер запущен на http://localhost:3000');
});
