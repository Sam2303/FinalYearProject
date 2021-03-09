const elem = [];
const spawn = require('child_process').spawn;
const testRoutes = (app, fs) => {
const dataPath = "./src/data/questionAnswers.json";
const {MongoClient} = require('mongodb');
const URI = "mongodb+srv://dbUser:dbUser@cluster0.fzis5.mongodb.net/fypDb?retryWrites=true&w=majority"
const client = new MongoClient(URI, { useUnifiedTopology: true });

const dbName = 'fyp';

app.post('/Createlogin', async(req,res)=>{
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

app.post('/sendCounterAndScore', async(req,res)=>{
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

app.post('/getCounterAndScore', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);

        const response = req.body;
        const userName = response.userName;

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

app.post('/getLogin', async(req, res)=>{
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

        const getCounter = db.collection('counterAndScore');
        const query2 = {
            "userName" : userName
        }
        const options2 = {
            sort: {rating : -1},
            projection : {_id:0, userName:1, counter:1, highScore:1},
        };

        const counterScore = await getCounter.findOne(query2, options2);
        console.log(counterScore);


        let sendToClient = {
            "userName" : user.userName,
            "password": user.password,
            "counter": counterScore.counter,
            "highScore": counterScore.highScore
        }


        if (user !== null){
            console.log('user in db');
            await res.json(sendToClient);
        }else{console.log('user not in db');await res.json({success:'false'});}

    }catch(err){
        console.log(err.stack);
    }
});













//------------------//

    app.get('/test', (req, res) => {
       fs.readFile(dataPath, 'utf8', (err, data) => {
           if(err){throw err;}
           res.send(JSON.parse(data));
       });
    });


    app.post('/sendQuestion', async(req, res) => {
        const response = req.body;
        let file = JSON.stringify(response);


        console.log("this is the question from the app: "+file);
        let question = response.question;
        console.log("this is the bare question : \n" + question);
        fs.writeFile('./src/data/python/python.py', question , err => {
          if (err){console.log('ERROR!');}
          else{
            console.log('WRITTEN TO PYTHON');}
          });

          const process = spawn('python', ['./src/data/python/python.py']);
           process.stdout.on('data', async(data) => {
              console.log("RUN THE PYTHON FILE ANSWER : " + data.toString());

              let answerStr = data.toString();
              answerStr = answerStr.replace("\n", "");

              let json1 = {
                  'answerStr' : answerStr
              }
              let json = JSON.stringify(json1);

              await res.send(JSON.parse(json));
                });


    });


    app.get('/writePython', async(req, res) => {

        let question = elem.question;
        fs.writeFile('./src/data/python/python.py', question , err => {
          if (err){console.log('ERROR!');}
          else{
            console.log('WRITTEN TO PYTHON');}
          });

          const process = spawn('python', ['./src/data/python/python.py']);
           process.stdout.on('data', data => {
              console.log("RUN THE PYTHON FILE ANSWER : " + data.toString());

              let dataString = data.toString();
              let dataInt = parseInt(dataString, 10);
              elem.json1 = {
                  'answer': dataInt
              }
              elem.json = JSON.stringify(elem.json1);

              res.send(JSON.parse(elem.json));
                });
           });





}
module.exports = testRoutes;
