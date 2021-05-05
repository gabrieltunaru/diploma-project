import WebSocket from 'ws'
import logger from './logger'
import * as winston from 'winston'
import {getUserId} from './middleware/general'


const connectedUsers = {}

function handleInitMessage(data, ws) {
  const userId = getUserId(data.token)
  connectedUsers[userId] = ws
}

function handleMessage(text, ws) {
  const data = JSON.parse(text)
  if (data.type === 'init') {
    handleInitMessage(data, ws)
  }
}

function init(wss: WebSocket) {

  wss.on('connection', function connection(ws) {
    logger.debug('connection lel')

    ws.on('message', function incoming(message) {
      logger.debug(`received: ${message}`)
      ws.send(`received: ${message}`)
      ws.send('ba da chiar merge')
    })

    ws.send('something')
  })

}

export default {init}
