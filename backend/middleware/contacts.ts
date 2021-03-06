import {Router} from 'express'
import auth from './auth'
import {decoded, getUser, getUserId} from './general'
import userModel, {IUser} from '../models/userModel'
import logger from '../logger'
import ConversationModel, {IConversation} from '../models/conversations'

const router = Router()

function specifyOtherUser(conn: IConversation, userId) {
  const otherUser = conn.users.find(x => String(x._id) !== String(userId))
  if (conn.isPrivate) {
    otherUser.profile.details = 'Anonymous user'
    otherUser.profile.photo = null
    otherUser.profile.displayName = otherUser.privateId
  }
  return {
    _id: conn._id,
    isPrivate: conn.isPrivate,
    otherUser
  }
}

function parseConversations(conversations, userId) {
  const conns = []
  conversations.forEach(conn => {
    conns.push(specifyOtherUser(conn, userId))
  })
  return conns
}

router.get('/getAll/:arePrivate', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const arePrivate = req.params.arePrivate === 'true'
  const {conversations} = await userModel
    .findById(userId)
    .populate({path: 'conversations', populate: 'users'})
    .select('conversations')
  const filteredConversations = conversations.filter(con => con.isPrivate === arePrivate)
  const conns = parseConversations(filteredConversations, userId)
  res.json({conversations: conns})
  next()
})

async function createConversation(currentUser: IUser, newContact: IUser, isPrivate = false) {
  const newConversation = new ConversationModel({isPrivate})
  newConversation.users.push(currentUser)
  newConversation.users.push(newContact)
  await newConversation.save()
  currentUser.conversations.push(newConversation)
  newContact.conversations.push(newConversation)
  await currentUser.save()
  await newContact.save()
  return newConversation
}

async function isContactAlreadyAdded(currentUser: IUser, newContactId, isPrivate): Promise<boolean> {
  for (const conversation of currentUser.conversations) {
    if (conversation.users.includes(newContactId) && conversation.isPrivate === isPrivate) {
      return true
    }
  }
  return false
}

router.post('/add', [auth], async (req, res, next) => {
  const userId = getUserId(req.headers)
  const currentUser = await userModel.findById(userId).populate('conversations')
  const {contactPseudoId} = req.body
  const newContact = await userModel.findOne({
    "$or": [
      {email: contactPseudoId},
      {"profile.username": contactPseudoId}
    ]
  })
  if (newContact) {
    if (await isContactAlreadyAdded(currentUser, newContact._id, false)) {
      res.status(400).send('Cannot add a contact already added')
      next()
      return
    }
    const newConversation = await createConversation(currentUser, newContact)
    res.json(specifyOtherUser(newConversation, userId))
  } else {
    res.status(404).send('User not found')
  }
  next()
})


router.post('/addPrivate', auth, async (req, res) => {
  const currentUser = await getUser(req.headers)
  const privateId = req.body.contactPseudoId
  const newContact = await userModel.findOne({privateId})
  if (!newContact) {
    res.sendStatus(404)
    return
  }
  const newConversation = await createConversation(currentUser, newContact, true)
  res.json(specifyOtherUser(newConversation, currentUser._id))
})

export default router
