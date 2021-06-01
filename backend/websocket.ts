import WebSocket from 'ws'
import logger from './logger'
import {getUserFromToken, getUserIdFromTokenString} from './middleware/general'
import UserModel from './models/user'
import ConversationModel from './models/conversations'
import {ObjectId, Types} from 'mongoose'

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
  connectedUsers[userId] = ws
}

async function handleTextMessage(data: ConversationMessage) {
  const currentUser = await getUserFromToken(data.token)
  const conv = await ConversationModel.findById(data.conversationId).populate('users')
  const userIds: string[] = conv.users.map(x=>String(x._id))
  if (!userIds.includes(currentUser._id) ||  !userIds.includes(data.otherUserId)) {
    return
  }
  const otherUserWs = connectedUsers[data.otherUserId]
  if (!otherUserWs) {
    console.log("user ", data.otherUserId, " not connected", connectedUsers)
    // handle not connected
  } else {
    otherUserWs.send(JSON.stringify(data))
  }
}

function handleMessage(text, ws) {
  const data = JSON.parse(text)
  if (data.type === 'init') {
    handleInitMessage(data, ws)
  } else if (data.type === 'text') {
    handleTextMessage(data).then(() => {return null}).catch(err => console.error(err))
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
