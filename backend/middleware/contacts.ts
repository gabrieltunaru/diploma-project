import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'

const router = Router()

router.get('/getAll', [auth], async (req, res, next) => {
  const userId = decoded(req.headers)._id
  const contacts = await userModel
    .findById(userId)
    .populate('contacts')
    .select('contacts')
  console.log(contacts)
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
  console.log(contactProfile)
  res.sendStatus(200)
  next()
})

export default router
