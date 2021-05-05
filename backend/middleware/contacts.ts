import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'
import logger from '../logger'
import ConnectionModel, {IConnection} from '../models/connections'

const router = Router()

router.post('/getAll', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const {connections} = await userModel
    .findById(userId)
    .populate({path: 'connections', populate: 'users'})
    .select('connections')
  const conns = []
  connections.forEach(conn =>{
    conns.push({
      _id: conn._id,
      isPrivate: conn.isPrivate,
      otherUser: conn.users.find(x => x._id !== userId)
    })
  } )
  logger.debug(conns)
  res.json({connections: conns})
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
    const currentUser = await userModel.findById(userId).populate('connections')
    for (const connection of currentUser.connections) {
      if (connection.users.includes(newContact._id)) {
        res.status(400).send('Cannot add a contact already added')
        next()
        return
      }
    }
    const newConnection = new ConnectionModel()
    newConnection.users.push(currentUser)
    newConnection.users.push(newContact)
    await newConnection.save()
    currentUser.connections.push(newConnection)
    newContact.connections.push(newConnection)
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
