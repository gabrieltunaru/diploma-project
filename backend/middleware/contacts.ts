import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'
import logger from '../logger'
import ConversationModel, {IConversation} from '../models/conversations'

const router = Router()

router.post('/getAll', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const {conversations} = await userModel
    .findById(userId)
    .populate({path: 'conversations', populate: 'users'})
    .select('conversations')
  const conns = []
  conversations.forEach(conn => {
    conns.push({
      _id: conn._id,
      isPrivate: conn.isPrivate,
      otherUser: conn.users.find(x => x._id !== userId)
    })
  })
  logger.debug(conns)
  res.json({conversations: conns})
  next()
})

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
    const newConversation = new ConversationModel()
    newConversation.users.push(currentUser)
    newConversation.users.push(newContact)
    await newConversation.save()
    currentUser.conversations.push(newConversation)
    newContact.conversations.push(newConversation)
    await currentUser.save()
    res.json(newContact)
    logger.debug(`Add contact: ${newContact}`)
  } else {
    logger.debug('Add contact: 404')
    res.status(404).send('User not found')
  }
  next()
})

export default router
