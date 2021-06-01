import WebSocket from 'ws'
import logger from './logger'
import {getUserFromToken, getUserIdFromTokenString} from './middleware/general'
import ConversationModel from './models/conversations'
import MessageModel from './models/messages'

interface ConversationMessage {
  type: string,
  token: string,
  text?: string,
  otherUserId: string,
  conversationId: string,
  timestamp: string
}

const connectedUsers = {}

function handleInitMessage(data: ConversationMessage, ws) {
  const userId = getUserIdFromTokenString(data.token)
  connectedUsers[userId] = ws
}

function saveMessage(data: ConversationMessage) {
  const message = new MessageModel({...data, senderId: getUserIdFromTokenString(data.token)})
  message.save().catch(err => logger.error(err))
}

async function handleTextMessage(data: ConversationMessage) {
  const currentUser = await getUserFromToken(data.token)
  const conv = await ConversationModel.findById(data.conversationId).populate('users')
  const userIds: string[] = conv.users.map(x => String(x._id))
  if (!userIds.includes(currentUser._id) || !userIds.includes(data.otherUserId)) {
    return
  }
  saveMessage(data)
  const otherUserWs = connectedUsers[data.otherUserId]
  if (otherUserWs) {
    otherUserWs.send(JSON.stringify(data))
  }
}

function handleAckMessage(data: ConversationMessage) {
  MessageModel.deleteOne({
    otherUserId: data.otherUserId,
    timestamp: data.timestamp,
    conversationId: data.conversationId
  })
}

function handleMessage(text, ws) {
  const data = JSON.parse(text)
  if (data.type === 'init') {
    handleInitMessage(data, ws)
  } else if (data.type === 'text') {
    handleTextMessage(data).then(() => {
      return null
    }).catch(err => console.error(err))
  } else if (data.type === 'ack') {
    handleAckMessage(data)
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
