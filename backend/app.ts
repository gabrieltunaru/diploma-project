import express from 'express'
import config from 'config'
import bodyParser from 'body-parser'
import db from './db'
import routes from './middleware/routes'
import logger from './logger'
import * as http from 'http'
import * as WebSocket from 'ws'
import websocket from './websocket'
import {errorHandler} from './utils'
import sendMail from './mail'

if (!config.get('privateKey')) {
  console.error('FATAL ERROR: private key not defined')
  process.exit(1)
}


db.init()

const app = express()
app.use(bodyParser.json())
app.use('/', routes)
app.use(errorHandler)
const port = 3000
const server = http.createServer(app)
const wss = new WebSocket.Server({server})

websocket.init(wss)

server.listen(port, () => {
  logger.info(`server is listening on ${port}`)
})
