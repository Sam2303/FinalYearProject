const express = require('express')
const fs = require('fs');
const spawn = require('child_process').spawn;
const mongoose = require('mongoose');
const api = express.Router();
const elem = [];
const {MongoClient} = require('mongodb');
const URI = "mongodb+srv://dbUser:dbUser@cluster0.fzis5.mongodb.net/fypDb?retryWrites=true&w=majority"
const client = new MongoClient(URI, { useUnifiedTopology: true });

const dbName = 'fyp';

api.post('/Createlogin', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);

        const response = req.body;
        const userName = response.userName;
        const password = response.password;

        const col = db.collection("users");

        let loginDets = {
            "userName" : userName,
            "password" : password
        }

        const p = await col.insertOne(loginDets);
        const myDoc = await col.findOne();
        console.log(myDoc);
     }catch(err){
         console.log(err.stack);
     }

});

api.post('/sendCounterAndScore', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);
           const response = req.body;
           const counter = response.counter;
           const score = response.score;

           const col = db.collection("counterAndScore");

           let counterAndScore = {
               "userName" : userName,
               "counter" : counter,
               "score" : score
           }
           const p = await col.insertOne(counterAndScore);
           const myDoc = await col.findOne();
           console.log(myDoc);
           await res.json(myDoc);
       }catch(err){
           console.log(err.stack);
       }
});

api.post('/getCounterAndScore', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);

        const response = req.body;
        const userName = response.userName;
        const counter = response.counter;
        const score = response.score;

        const counterAndScore = db.collection('counterAndScore');
        const query = {
            "userName" : userName
        }
        const options = {
            sort: {rating : -1},
            projection : {_id:0, userName:1, counter:1, score:1},
        };

        const user = await user.findOne(query, options);
        console.log(user);

        if (user !== null){
            console.log('user in db');
            await res.json(user);
        }else{console.log('user not in db');}

    }catch(err){
        console.log(err.stack);
    }
});

api.post('/getLogin', async(req, res)=>{
    console.log("fetching login");
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);

        const response = req.body;
        const userName = response.userName;
        const password = response.password;

        const users = db.collection('users');
        const query = {
            "userName" : userName,
            "password" : password
        }
        const options = {
            sort: {rating : -1},
            projection : {_id:0, userName:1, password:1},
        };

        const user = await users.findOne(query, options);
        console.log(user);

        if (user !== null){
            console.log('user in db');
        }else{console.log('user not in db');}

    }catch(err){
        console.log(err.stack);
    }
});





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
