import {Router} from 'express'
import auth from './auth'
import {decoded, getUser, getUserId} from './general'
import userModel, {IUser} from '../models/user'
import logger from '../logger'
import ConversationModel, {IConversation} from '../models/conversations'

const router = Router()

function parseConversation(conn, userId) {
  const foundId = conn.users.find(x => String(x._id) !== userId)._id
  console.log("ids: ", foundId !== userId, foundId, userId, typeof foundId, typeof userId)
  return {
    _id: conn._id,
    isPrivate: conn.isPrivate,
    otherUser: conn.isPrivate ? null : conn.users.find(x => String(x._id) !== userId)
  }
}

function parseConversations(conversations, userId) {
  const conns = []
  conversations.forEach(conn => {
    conns.push(parseConversation(conn, userId))
  })
  return conns
}

router.post('/getAll', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const {conversations} = await userModel
    .findById(userId)
    .populate({path: 'conversations', populate: 'users'})
    .select('conversations')
  const conns = parseConversations(conversations, userId)
  res.json({conversations: conns})
  next()
})

async function createConversation(currentUser, newContact) {
  const newConversation = new ConversationModel()
  newConversation.users.push(currentUser)
  newConversation.users.push(newContact)
  await newConversation.save()
  currentUser.conversations.push(newConversation)
  newContact.conversations.push(newConversation)
  await currentUser.save()
  await newContact.save()
  return newConversation
}

router.post('/add', [auth], async (req, res, next) => {
  const userId = getUserId(req.headers)
  const {contactPseudoId} = req.body
  const newContact = await userModel.findOne({
    "$or": [
      {email: contactPseudoId},
      {"profile.username": contactPseudoId}
    ]
  })
  if (newContact) {
    const currentUser = await userModel.findById(userId).populate('conversations')
    for (const conversation of currentUser.conversations) {
      if (conversation.users.includes(newContact._id)) {
        res.status(400).send('Cannot add a contact already added')
        next()
        return
      }
    }
    const newConversation = await createConversation(currentUser, newContact)
    res.json(parseConversation(newConversation, userId))
    logger.debug(`Add contact: ${newContact}`)
  } else {
    logger.debug('Add contact: 404')
    res.status(404).send('User not found')
  }
  next()
})


router.post('/addPrivate', auth, async (req, res) => {
  const currentUser = await getUser(req.headers)
  const {privateId} = req.body
  const newContact = await userModel.findOne({privateId})
  const newConversation = await createConversation(currentUser, newContact)
  res.json(parseConversation(newConversation, currentUser._id))
})

export default router
