const express = require('express')
const fs = require('fs');
const spawn = require('child_process').spawn;
const api = express.Router();
const elem = [];

api.get('/getFile', async(req, res) => {
   await res.json({
      Question: x + 1,
      Answer: 2
   });
});

api.post('/storeResults', async(req, res) => {
  const response = req.body;
  let file = JSON.stringify(response, null, 1);
  await res.json({success: true});

    fs.writeFile('src/data/questionAnswers.json', file, err => {
      if (err){console.log('ERROR!');}
      else{
        console.log('saved');}
      });
 });

module.exports = api;
