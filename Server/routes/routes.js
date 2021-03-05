const testRoutes = require('./getFile');
const fetch = require('node-fetch');

const appRouter = (app, fs) => {
    app.get('/', async(req, res) => {

        res.send('from website');


    });

    testRoutes(app, fs);
};

module.exports = appRouter;
