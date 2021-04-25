import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'
import logger from '../logger'

const router = Router()

router.post('/getAll', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const contacts = await userModel
    .findById(userId)
    .populate('contacts')
    .select('contacts')
  res.json(contacts)
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
    const currentUser = await userModel.findById(userId)
    currentUser.contacts.push(newContact)
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
