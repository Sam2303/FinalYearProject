const elem = [];
const spawn = require('child_process').spawn;
const testRoutes = (app, fs) => {
const dataPath = "./src/data/questionAnswers.json";

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
