const express = require('express');
const bodyParser = require('body-parser');
const api = require('./api');
const app = express();
const fs = require('fs');
const fetch = require('node-fetch');

app.use(bodyParser.json());
app.use('/api', api);
app.use(bodyParser.urlencoded({extended : true}));
app.use('/', express.static('src'));
app.use('/', express.static('src/html'));

const routes = require('./routes/routes.js')(app, fs);

module.exports = app;
app.listen(8080);
console.log('Server running at http://127.0.0.01:8080');
