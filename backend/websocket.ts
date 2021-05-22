import WebSocket from 'ws'
import logger from './logger'
import {getUserIdFromTokenString} from './middleware/general'

interface ConversationMessage {
  type: string,
  token: string,
  text?: string,
  otherUserId: string,
  conversationId: string
}

const connectedUsers = {}

function handleInitMessage(data: ConversationMessage, ws) {
  const userId = getUserIdFromTokenString(data.token)
  if (connectedUsers) {
    connectedUsers[userId] = ws
  } else {
    connectedUsers[userId] = ws
  }
}

function handleTextMessage(data: ConversationMessage) {
  const otherUserWs = connectedUsers[data.otherUserId]
  if (!otherUserWs) {
    console.log("user ", data.otherUserId, " not connected", connectedUsers)
    // handle not connected
  } else {
    logger.debug(`ws: ${otherUserWs}, data: ${data}`)
    otherUserWs.send(JSON.stringify(data))
  }
}

function handleMessage(text, ws) {
  const data = JSON.parse(text)
  logger.debug(`received: ${text}`)
  if (data.type === 'init') {
    handleInitMessage(data, ws)
  } else if (data.type === 'text') {
    handleTextMessage(data)
  }
}

function init(wss: WebSocket) {
  wss.on('connection', function connection(ws) {
    ws.on('message', function incoming(message) {
      handleMessage(message, ws)
    })
  })
}

export default {init}
