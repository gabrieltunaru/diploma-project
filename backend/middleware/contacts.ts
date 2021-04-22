import {Router} from 'express'
import auth from './auth'
import {decoded, getUserId} from './general'
import userModel, {IUser} from '../models/user'
import {ProfileModel} from '../models/profileModel'
import * as util from 'util'

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
  let newContact = await userModel.findOne({email: contactPseudoId})
  if (!newContact) {
    const contactProfile = await ProfileModel.findOne({
      "$or": [
        {username: contactPseudoId}
      ]
    })
    console.log({contactProfile})
    newContact = await userModel.populate('profile') }
  console.log(newContact)
  // const user: IUser = await userModel.findById(userId)
  // user.contacts.push(newContact)
  // await user.save()
  res.sendStatus(200)
  next()
})

export default router
