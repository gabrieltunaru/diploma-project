import WebSocket from 'ws'
import logger from './logger'
import * as winston from 'winston'
import {getUserId} from './middleware/general'

interface ConversationMessage {
  type: string,
  token: string,
  text?: string,
  otherUserId: string,
  conversationId: string
}

const connectedUsers = {}

function handleInitMessage(data: ConversationMessage, ws) {
  const userId = getUserId(data.token)
  if (!connectedUsers[data.conversationId]) {
    connectedUsers[data.conversationId][userId] = ws
  } else {
    connectedUsers[data.conversationId]= {[userId]: ws}
  }
}

function handleTextMessage(data: ConversationMessage) {
  if (!connectedUsers[data.otherUserId]) {
    // handle not connected
  } else {
    connectedUsers[data.otherUserId].send(JSON.stringify(data))
  }
}

function handleMessage(text, ws) {
  const data = JSON.parse(text)
  if (data.type === 'init') {
    handleInitMessage(data, ws)
  } else if (data.type === 'text') {
    handleTextMessage(data)
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
