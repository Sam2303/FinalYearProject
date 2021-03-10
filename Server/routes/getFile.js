const elem = [];
const spawn = require('child_process').spawn;
const testRoutes = (app, fs) => {
const dataPath = "./src/data/questionAnswers.json";
const {MongoClient} = require('mongodb');
const URI = "mongodb+srv://dbUser:dbUser@cluster0.fzis5.mongodb.net/fypDb?retryWrites=true&w=majority"
const client = new MongoClient(URI, { useUnifiedTopology: true });

const dbName = 'fyp';

app.post('/createLogin', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);
        // Creates Login in Users DB
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
        console.log("login uploaded");
// Creates file

        const counter = "0";
        const highScore = "0";
        const score = null;

        const col2 = db.collection("counterAndScore");

        let scoreAndCounterInput = {
            "userName" : userName,
            "counter" : "0",
            "highScore" : "0",
            "score" : ""
        };
          const send = await col2.insertOne(scoreAndCounterInput);
          const myDoc2 = await col.findOne();
          console.log("Score and Counter been uploaded");

        await res.json({success:"true"});
     }catch(err){
         console.log(err.stack);
     }

});
// Adds data to the database after level progression
app.post('/sendCounterAndScore', async(req,res)=>{
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);
           const response = req.body;
           const userName = response.userName;
           const counter = response.counter;
           const score = response.score;

           const col = db.collection("counterAndScore");

           let query = {
               "userName" : userName
           };

           let newValues = {
               $set: {
               "userName" : userName,
               "counter" : counter,
               "score" : score
           }
           };
           let options = {"upsert":true};

           const result = await col.updateOne(query, newValues, options);
           console.log("updated file");
           await res.json({success: true});
       }catch(err){
           console.log(err.stack);
       }
});
// gets all relevant information on login
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
        console.log("This is the user info: "+user);
        if(user === null){
            console.log("not valid login");
            await res.json({"success":"false"});
        }else{
            await res.json({"userName":userName});
    }
}
    catch(err){
        console.log("RORRRRRRRRRRR");
        console.log(err.stack);
    }
});

app.post('/getCounterAndScore', async(req, res) => {
   try {
       await client.connect();
       console.log('Connected to database!');
       const db = client.db(dbName);

       const response = req.body;
       const userName = response.userName;

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
           "userName" : userName,
           "counter": counterScore.counter,
           "highScore": counterScore.highScore
       }


       if (counterScore !== null){
           console.log('user in db');
           await res.json(sendToClient);
         }else{
             console.log('user not in db');
             await res.json({success:'false'});
         }
    }catch(err){
       console.log("RORRRRRRRRRRR");
       console.log(err.stack);
    }

});






//------------------//
// Actual app functionality

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

}
module.exports = testRoutes;
