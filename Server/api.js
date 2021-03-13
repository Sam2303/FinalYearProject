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

 api.post('/testPython', async(req, res) => {
   const response = req.body;
   let question = response.question;
   console.log(question);
   let pyPath = "./src/data/python/testing.py";

   fs.writeFile(pyPath, question , err => {
     if (err){console.log('ERROR!');}
     else{
       console.log('WRITTEN TO PYTHON');}
     });

// Test to see if python is valid pyhton
     let dataToSend = '';
     const process = spawn('python', ['./src/data/python/testing.py']);
      process.stdout.on('data', data => {
         console.log("RUN THE PYTHON FILE ANSWER : " + data.toString());
         dataToSend = data.toString();
     });
     process.on('close', async(code) => {
        if(dataToSend === ''){
            await res.json({success:false});
        }else{await res.json({success:true});}

     });



  });

module.exports = api;
