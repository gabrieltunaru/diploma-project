import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'

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
  const contactProfile = await userModel.findOne({
    "$or": [
      {email: contactPseudoId},
      {"profile.username": contactPseudoId}
    ]
  })
  const currentUser = await userModel.findById(userId)
  currentUser.contacts.push(contactProfile)
  await currentUser.save()
  res.status(200).send({})
  next()
})

export default router
