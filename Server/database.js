const {MongoClient} = require('mongodb');
const fetch = require('node-fetch');
console.log("trying to connect db....");
const URI = "mongodb+srv://dbUser:dbUser@cluster0.fzis5.mongodb.net/fypDb?retryWrites=true&w=majority"
const client = new MongoClient(URI, { useUnifiedTopology: true });

const dbName = 'fyp';

async function run() {
    try {
        await client.connect();
        console.log('Connected to database!');
        const db = client.db(dbName);


    }catch(err){
        console.log(err.stack);
    }
}

run().catch(console.dir);


async function listDatabases(client) {
    databasesList = await client.db().admin().listDatabases();

    console.log("Databases:");
    databasesList.databases.forEach(db => console.log(` - ${db.name}`));
};
