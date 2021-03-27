import express from 'express'
import config from 'config'
import bodyParser from 'body-parser'
import db from './db'
import routes from './middleware/routes'

if (!config.get('privateKey')) {
  console.error('FATAL ERROR: private key not defined')
  process.exit(1)
}

const app = express()

app.use(bodyParser.json())

db.init()
const port = 3000

app.use('/', routes)
app.listen(port, () => {
  console.log(`server is listening on ${port}`)
})
